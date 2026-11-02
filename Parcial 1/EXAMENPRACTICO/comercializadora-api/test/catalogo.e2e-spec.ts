import type { INestApplication } from '@nestjs/common';
import { Test } from '@nestjs/testing';
import request from 'supertest';
import { DataSource } from 'typeorm';
import { AppModule } from '../src/app.module.js';
import { createValidationPipe } from '../src/config/validation-pipe.js';

/**
 * Integration tests for the phone catalog (S10).
 *
 * They run against the real PostgreSQL from docker-compose: the RetailerSchema
 * migration creates the tables and inserts the six phones of the exam, and the
 * rows these tests create are hard deleted again, so `npm test` can run as
 * many times as needed and the catalog always goes back to the six seeded
 * phones (a soft delete would leave inactive leftovers behind).
 */
describe('Catalogo de celulares (real database)', () => {
  const demoUser = {
    email: 'vendedor@comercializadora.ec',
    password: 'Vendedor2026!',
  };

  /** The six phones the RetailerSchema migration inserts. */
  const seeded = [
    {
      codCelular: expect.any(Number) as number,
      marca: 'Apple',
      modelo: 'iPhone 16',
      descripcion: '6.1", 128 GB',
      precio: 1099,
      activo: true,
    },
    {
      codCelular: expect.any(Number) as number,
      marca: 'Honor',
      modelo: 'X8c',
      descripcion: '6.7", 256 GB, 8 GB RAM',
      precio: 259,
      activo: true,
    },
    {
      codCelular: expect.any(Number) as number,
      marca: 'Motorola',
      modelo: 'Moto G85',
      descripcion: '6.67", 256 GB, 8 GB RAM',
      precio: 319,
      activo: true,
    },
    {
      codCelular: expect.any(Number) as number,
      marca: 'Samsung',
      modelo: 'Galaxy A16',
      descripcion: '6.7", 128 GB, 4 GB RAM',
      precio: 229,
      activo: true,
    },
    {
      codCelular: expect.any(Number) as number,
      marca: 'Samsung',
      modelo: 'Galaxy S25',
      descripcion: '6.2", 256 GB, 12 GB RAM',
      precio: 999,
      activo: true,
    },
    {
      codCelular: expect.any(Number) as number,
      marca: 'Xiaomi',
      modelo: 'Redmi Note 14',
      descripcion: '6.67", 256 GB, 8 GB RAM',
      precio: 289,
      activo: true,
    },
  ];

  /** Marca of every row these tests create, so the cleanup is exhaustive. */
  const TEST_BRAND = 'Testeo';
  const suffix = `${Date.now()}`;

  let app: INestApplication;
  let cookie: string;
  let dataSource: DataSource;

  const cookieHeader = (response: request.Response): string => {
    const raw = response.headers['set-cookie'] as unknown as string | string[];
    const cookies = Array.isArray(raw) ? raw : [raw];
    return cookies.map((item) => item.split(';')[0]).join('; ');
  };

  const get = (url: string) =>
    request(app.getHttpServer()).get(url).set('Cookie', cookie);

  const post = (url: string, body: object) =>
    request(app.getHttpServer()).post(url).set('Cookie', cookie).send(body);

  const put = (url: string, body: object) =>
    request(app.getHttpServer()).put(url).set('Cookie', cookie).send(body);

  const patch = (url: string) =>
    request(app.getHttpServer()).patch(url).set('Cookie', cookie);

  const del = (url: string) =>
    request(app.getHttpServer()).delete(url).set('Cookie', cookie);

  /** Number of phones the API knows about, inactive ones included. */
  const countAll = async (): Promise<number> => {
    const response = await get('/api/celulares?todos=true');
    expect(response.status).toBe(200);
    return (response.body as unknown[]).length;
  };

  beforeAll(async () => {
    const moduleRef = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    // Same DTO validation the CLI bootstrap registers in `main.ts`.
    app = moduleRef.createNestApplication({ bodyParser: false });
    app.useGlobalPipes(createValidationPipe());
    await app.init();

    dataSource = app.get(DataSource);
    // Leftovers of an interrupted run: the tests only ever create TEST_BRAND.
    await dataSource.query('delete from celular where marca = $1', [
      TEST_BRAND,
    ]);

    const signIn = await request(app.getHttpServer())
      .post('/api/auth/sign-in/email')
      .set('Origin', 'http://localhost:3000')
      .send(demoUser);
    expect(signIn.status).toBe(200);
    cookie = cookieHeader(signIn);
  });

  afterAll(async () => {
    await dataSource.query('delete from celular where marca = $1', [
      TEST_BRAND,
    ]);
    await app.close();
  });

  it('rejects every catalog route without a session', async () => {
    const server = request(app.getHttpServer());
    const responses = await Promise.all([
      server.get('/api/celulares'),
      server.get('/api/celulares/1'),
      server.post('/api/celulares').send({ marca: 'Sin', modelo: 'Sesion', precio: 100 }),
      server.put('/api/celulares/1').send({ marca: 'Sin', modelo: 'Sesion', precio: 100 }),
      server.patch('/api/celulares/1/activar'),
      server.delete('/api/celulares/1'),
    ]);

    for (const response of responses) {
      expect(response.status).toBe(401);
    }
  });

  it('lists the six seeded phones with numeric prices', async () => {
    const response = await get('/api/celulares');

    expect(response.status).toBe(200);
    expect(response.body).toEqual(seeded);
  });

  it('returns one phone by id and 404 for an unknown id', async () => {
    const list = await get('/api/celulares');
    const first = (list.body as { codCelular: number }[])[0];

    const found = await get(`/api/celulares/${first.codCelular}`);
    expect(found.status).toBe(200);
    expect(found.body).toEqual(seeded[0]);

    const missing = await get('/api/celulares/999999');
    expect(missing.status).toBe(404);
    expect(missing.body).toMatchObject({ statusCode: 404 });
  });

  it('creates a phone and returns it in the list with two decimals', async () => {
    const created = await post('/api/celulares', {
      marca: TEST_BRAND,
      modelo: `Nuevo ${suffix}`,
      descripcion: 'Creado por el test e2e',
      precio: 123.45,
    });

    expect(created.status).toBe(201);
    expect(created.body).toEqual({
      codCelular: expect.any(Number) as number,
      marca: TEST_BRAND,
      modelo: `Nuevo ${suffix}`,
      descripcion: 'Creado por el test e2e',
      precio: 123.45,
      activo: true,
    });

    const id = (created.body as { codCelular: number }).codCelular;
    const list = await get('/api/celulares');
    expect(list.status).toBe(200);
    expect(list.body).toContainEqual(created.body);

    const found = await get(`/api/celulares/${id}`);
    expect(found.status).toBe(200);
    expect(found.body).toEqual(created.body);

    // Omitted `descripcion` is stored as null, not as an empty string.
    const withoutDescription = await post('/api/celulares', {
      marca: TEST_BRAND,
      modelo: `Sin descripcion ${suffix}`,
      precio: 10,
    });
    expect(withoutDescription.status).toBe(201);
    expect(withoutDescription.body).toMatchObject({ descripcion: null });
  });

  it('rejects invalid bodies with 400 and stores nothing', async () => {
    const before = await countAll();

    const invalidBodies: object[] = [
      { marca: TEST_BRAND, modelo: `Precio cero ${suffix}`, precio: 0 },
      { marca: TEST_BRAND, modelo: `Precio negativo ${suffix}`, precio: -5 },
      { marca: TEST_BRAND, modelo: `Tres decimales ${suffix}`, precio: 1.234 },
      { modelo: `Sin marca ${suffix}`, precio: 100 },
      { marca: TEST_BRAND, precio: 100 },
      { marca: 'x'.repeat(51), modelo: `Marca larga ${suffix}`, precio: 100 },
      {
        marca: TEST_BRAND,
        modelo: `Descripcion larga ${suffix}`,
        descripcion: 'x'.repeat(256),
        precio: 100,
      },
    ];

    for (const body of invalidBodies) {
      const response = await post('/api/celulares', body);
      expect(response.status).toBe(400);
      expect(response.body).toMatchObject({ statusCode: 400 });
      expect(typeof (response.body as { message: unknown }).message).not.toBe(
        'undefined',
      );
    }

    expect(await countAll()).toBe(before);
  });

  it('rejects a duplicate marca + modelo with 409 and stores nothing', async () => {
    const body = {
      marca: TEST_BRAND,
      modelo: `Duplicado ${suffix}`,
      precio: 100,
    };

    const first = await post('/api/celulares', body);
    expect(first.status).toBe(201);

    const before = await countAll();
    const duplicate = await post('/api/celulares', body);
    expect(duplicate.status).toBe(409);
    expect(duplicate.body).toMatchObject({
      statusCode: 409,
      message: 'Ya existe un celular con esa marca y modelo.',
    });
    expect(await countAll()).toBe(before);
  });

  it('updates a phone, validates the body and 404s for an unknown id', async () => {
    const created = await post('/api/celulares', {
      marca: TEST_BRAND,
      modelo: `Para editar ${suffix}`,
      descripcion: 'Antes',
      precio: 50,
    });
    expect(created.status).toBe(201);
    const id = (created.body as { codCelular: number }).codCelular;

    const updated = await put(`/api/celulares/${id}`, {
      marca: TEST_BRAND,
      modelo: `Editado ${suffix}`,
      precio: 199.99,
    });
    expect(updated.status).toBe(200);
    expect(updated.body).toEqual({
      codCelular: id,
      marca: TEST_BRAND,
      modelo: `Editado ${suffix}`,
      descripcion: null,
      precio: 199.99,
      activo: true,
    });

    const list = await get('/api/celulares');
    expect(list.body).toContainEqual(updated.body);

    const invalid = await put(`/api/celulares/${id}`, {
      marca: TEST_BRAND,
      modelo: `Editado ${suffix}`,
      precio: 1.234,
    });
    expect(invalid.status).toBe(400);

    const missing = await put('/api/celulares/999999', {
      marca: TEST_BRAND,
      modelo: `Fantasma ${suffix}`,
      precio: 100,
    });
    expect(missing.status).toBe(404);
  });

  it('deactivates instead of deleting and reactivates the phone', async () => {
    const created = await post('/api/celulares', {
      marca: TEST_BRAND,
      modelo: `Para desactivar ${suffix}`,
      precio: 100,
    });
    expect(created.status).toBe(201);
    const id = (created.body as { codCelular: number }).codCelular;

    const removed = await del(`/api/celulares/${id}`);
    expect(removed.status).toBe(200);
    expect(removed.body).toMatchObject({ codCelular: id, activo: false });

    const active = await get('/api/celulares');
    const activeIds = (active.body as { codCelular: number }[]).map(
      (row) => row.codCelular,
    );
    expect(activeIds).not.toContain(id);

    const all = await get('/api/celulares?todos=true');
    expect(all.body).toContainEqual(removed.body);

    const restored = await patch(`/api/celulares/${id}/activar`);
    expect(restored.status).toBe(200);
    expect(restored.body).toMatchObject({ codCelular: id, activo: true });

    const afterRestore = await get('/api/celulares');
    expect(afterRestore.body).toContainEqual(restored.body);

    expect((await del('/api/celulares/999999')).status).toBe(404);
    expect((await patch('/api/celulares/999999/activar')).status).toBe(404);
  });
});
