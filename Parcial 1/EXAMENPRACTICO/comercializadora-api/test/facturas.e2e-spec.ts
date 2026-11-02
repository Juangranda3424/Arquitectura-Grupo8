import type { INestApplication } from '@nestjs/common';
import { Test } from '@nestjs/testing';
import request from 'supertest';
import { DataSource } from 'typeorm';
import { AppModule } from '../src/app.module.js';
import { createValidationPipe } from '../src/config/validation-pipe.js';

/**
 * Integration tests of the invoicing endpoints (S11, S12) against the real
 * PostgreSQL of docker-compose.
 *
 * Only cash sales are exercised on purpose: they never call the BanQuito core,
 * so `npm test` does not grant credits in the bank's database. The credit path
 * (subject check, maximum amount, grant, rejections) is covered by the unit
 * spec with a mocked `BanquitoClient` (`src/facturacion/*.spec.ts`), and the
 * quota range is still checked here because it fails before any bank call.
 *
 * The rows these tests create are deleted again in `beforeAll` (leftovers of an
 * interrupted run) and `afterAll`, so the invoice list goes back to empty.
 */
describe('Facturacion (real database)', () => {
  const demoUser = {
    email: 'vendedor@comercializadora.ec',
    password: 'Vendedor2026!',
  };

  /** Cédulas only these tests use, so the cleanup is exhaustive. */
  const CEDULA = '0900000001';
  const CEDULA_CLIENTE_CONOCIDO = '0900000002';

  let app: INestApplication;
  let cookie: string;
  let dataSource: DataSource;
  /** `codCelular` of the Samsung Galaxy A16 (229.00) of the seeded catalog. */
  let codA16: number;

  const cookieHeader = (response: request.Response): string => {
    const raw = response.headers['set-cookie'] as unknown as string | string[];
    const cookies = Array.isArray(raw) ? raw : [raw];
    return cookies.map((item) => item.split(';')[0]).join('; ');
  };

  const get = (url: string) =>
    request(app.getHttpServer()).get(url).set('Cookie', cookie);

  const post = (url: string, body: object) =>
    request(app.getHttpServer()).post(url).set('Cookie', cookie).send(body);

  const deleteTestRows = async (): Promise<void> => {
    const cedulas = [CEDULA, CEDULA_CLIENTE_CONOCIDO];

    await dataSource.query(
      `delete from detalle_factura
        where num_factura in (
          select f.num_factura from factura f
            join cliente c on c.cod_cliente = f.cod_cliente
           where c.cedula = any($1::text[])
        )`,
      [cedulas],
    );
    await dataSource.query(
      `delete from factura
        where cod_cliente in (
          select cod_cliente from cliente where cedula = any($1::text[])
        )`,
      [cedulas],
    );
    await dataSource.query(
      'delete from cliente where cedula = any($1::text[])',
      [cedulas],
    );
  };

  const efectivo = (overrides: object = {}) => ({
    cedula: CEDULA,
    nombre: 'Cliente de prueba',
    codCelular: codA16,
    formaPago: 'EFE',
    ...overrides,
  });

  beforeAll(async () => {
    const moduleRef = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    // Same DTO validation the CLI bootstrap registers in `main.ts`.
    app = moduleRef.createNestApplication({ bodyParser: false });
    app.useGlobalPipes(createValidationPipe());
    await app.init();

    dataSource = app.get(DataSource);
    await deleteTestRows();

    const signIn = await request(app.getHttpServer())
      .post('/api/auth/sign-in/email')
      .set('Origin', 'http://localhost:3000')
      .send(demoUser);
    expect(signIn.status).toBe(200);
    cookie = cookieHeader(signIn);

    const celulares = await get('/api/celulares');
    expect(celulares.status).toBe(200);
    codA16 = (
      celulares.body as { codCelular: number; modelo: string }[]
    ).find((row) => row.modelo === 'Galaxy A16')!.codCelular;
  });

  afterAll(async () => {
    await deleteTestRows();
    await app.close();
  });

  it('rejects every invoicing route without a session', async () => {
    const server = request(app.getHttpServer());
    const responses = await Promise.all([
      server.get('/api/facturas'),
      server.get('/api/facturas/1'),
      server.get('/api/facturas/1/amortizacion'),
      server.get('/api/credito/evaluacion?cedula=1712345678&codCelular=1'),
      server.post('/api/facturas').send(efectivo()),
    ]);

    for (const response of responses) {
      expect(response.status).toBe(401);
    }
  });

  it('issues a cash invoice with the 42% discount and the phone line', async () => {
    const created = await post('/api/facturas', efectivo());

    expect(created.status).toBe(201);
    expect(created.body).toMatchObject({
      numFactura: expect.any(Number) as number,
      formaPago: 'EFE',
      subtotal: 229,
      descuento: 96.18,
      total: 132.82,
      codCredito: null,
      numCuotas: null,
      valorCuota: null,
      cliente: { cedula: CEDULA, nombre: 'Cliente de prueba' },
      detalles: [
        {
          cantidad: 1,
          precioUnitario: 229,
          subtotal: 229,
          celular: { codCelular: codA16, modelo: 'Galaxy A16', precio: 229 },
        },
      ],
      credito: null,
    });
    expect(typeof (created.body as { fecha: string }).fecha).toBe('string');
  });

  it('reuses the customer row when the same cédula buys again', async () => {
    const first = await post('/api/facturas', efectivo());
    expect(first.status).toBe(201);

    const second = await post(
      '/api/facturas',
      efectivo({ nombre: 'Cliente de prueba renombrado' }),
    );
    expect(second.status).toBe(201);

    const codCliente = (first.body as { cliente: { codCliente: number } })
      .cliente.codCliente;
    expect(second.body).toMatchObject({
      cliente: { codCliente, nombre: 'Cliente de prueba renombrado' },
    });

    const stored: { total: number }[] = await dataSource.query(
      `select count(*)::int as total from cliente where cedula = $1`,
      [CEDULA],
    );
    expect(stored[0].total).toBe(1);
  });

  it('lists invoices newest first and filters them by cédula', async () => {
    const primera = await post('/api/facturas', efectivo());
    const segunda = await post('/api/facturas', efectivo());

    const list = await get('/api/facturas');
    expect(list.status).toBe(200);
    const numeros = (list.body as { numFactura: number }[]).map(
      (row) => row.numFactura,
    );
    expect(numeros).toEqual([...numeros].sort((a, b) => b - a));
    expect(numeros).toContain(
      (primera.body as { numFactura: number }).numFactura,
    );
    expect(numeros).toContain(
      (segunda.body as { numFactura: number }).numFactura,
    );

    const filtered = await get(`/api/facturas?cedula=${CEDULA}`);
    expect(filtered.status).toBe(200);
    expect(
      (filtered.body as { cliente: { cedula: string } }[]).every(
        (row) => row.cliente.cedula === CEDULA,
      ),
    ).toBe(true);

    const empty = await get(`/api/facturas?cedula=${CEDULA_CLIENTE_CONOCIDO}`);
    expect(empty.status).toBe(200);
    expect(empty.body).toEqual([]);

    const invalid = await get('/api/facturas?cedula=123');
    expect(invalid.status).toBe(400);
  });

  it('returns one invoice with its customer and 404 for an unknown number', async () => {
    const created = await post('/api/facturas', efectivo());
    const numFactura = (created.body as { numFactura: number }).numFactura;

    const found = await get(`/api/facturas/${numFactura}`);
    expect(found.status).toBe(200);
    expect(found.body).toEqual(created.body);

    const missing = await get('/api/facturas/999999');
    expect(missing.status).toBe(404);
    expect(missing.body).toMatchObject({ statusCode: 404 });

    const notANumber = await get('/api/facturas/abc');
    expect(notANumber.status).toBe(400);
  });

  it('answers 409 for the amortization of a cash invoice, without calling the bank', async () => {
    const created = await post('/api/facturas', efectivo());
    const numFactura = (created.body as { numFactura: number }).numFactura;

    const response = await get(`/api/facturas/${numFactura}/amortizacion`);

    expect(response.status).toBe(409);
    expect(response.body).toMatchObject({
      statusCode: 409,
      message: 'La factura es en efectivo y no tiene tabla de amortización.',
    });

    expect((await get('/api/facturas/999999/amortizacion')).status).toBe(404);
  });

  it('rejects invalid bodies with 400 and stores nothing', async () => {
    const before = await get('/api/facturas');
    const totalBefore = (before.body as unknown[]).length;

    const invalidBodies: object[] = [
      efectivo({ cedula: '123' }),
      efectivo({ nombre: '' }),
      efectivo({ formaPago: 'TAR' }),
      efectivo({ formaPago: 'CRE', numeroCuotas: 2 }),
      efectivo({ formaPago: 'CRE', numeroCuotas: 19 }),
      efectivo({ formaPago: 'CRE' }),
    ];

    for (const body of invalidBodies) {
      const response = await post('/api/facturas', body);
      expect(response.status).toBe(400);
      expect(response.body).toMatchObject({ statusCode: 400 });
    }

    const after = await get('/api/facturas');
    expect((after.body as unknown[]).length).toBe(totalBefore);
  });

  it('answers 404 for an unknown phone', async () => {
    const response = await post('/api/facturas', efectivo({ codCelular: 999999 }));

    expect(response.status).toBe(404);
    expect(response.body).toMatchObject({
      statusCode: 404,
      message: 'No existe un celular con ese código.',
    });
  });
});
