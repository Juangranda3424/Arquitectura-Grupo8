import { HttpException } from '@nestjs/common';
import { Test } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import type { Repository } from 'typeorm';
import { BanquitoClient } from '../banquito/banquito.client.js';
import {
  BanquitoApiError,
  BanquitoUnavailableError,
} from '../banquito/banquito.errors.js';
import type {
  Credito,
  MontoMaximo,
  SujetoCredito,
} from '../banquito/banquito.types.js';
import { Celular } from '../catalogo/celular.entity.js';
import { Cliente } from './cliente.entity.js';
import { DetalleFactura } from './detalle-factura.entity.js';
import type { CreateFacturaDto } from './dto/create-factura.dto.js';
import { Factura } from './factura.entity.js';
import { FacturacionService } from './facturacion.service.js';

/**
 * Unit tests of the invoicing rules (S11) with a mocked `BanquitoClient`.
 *
 * No database and no HTTP: the repositories are small fakes that only record
 * what the service tried to store, so "nothing was stored" is a real
 * assertion, and the bank is a `vi.fn()` per service, which is what lets the
 * tests check that a rejected credit never reaches `POST /api/creditos`.
 */

const ANA = { cedula: '1712345678', nombre: 'Ana Torres', codCliente: 1 };

const A16 = {
  codCelular: 2,
  marca: 'Samsung',
  modelo: 'Galaxy A16',
  descripcion: '6.7", 128 GB, 4 GB RAM',
  precio: 229,
  activo: true,
};

const S25 = {
  codCelular: 5,
  marca: 'Samsung',
  modelo: 'Galaxy S25',
  descripcion: '6.2", 256 GB, 12 GB RAM',
  precio: 999,
  activo: true,
};

const IPHONE = {
  codCelular: 3,
  marca: 'Apple',
  modelo: 'iPhone 16',
  descripcion: '6.1", 128 GB',
  precio: 1099,
  activo: true,
};

/** Soft deleted phone: it stays in the catalog but cannot be sold. */
const DESACTIVADO = {
  codCelular: 9,
  marca: 'Honor',
  modelo: 'X8c',
  descripcion: '6.7", 256 GB, 8 GB RAM',
  precio: 259,
  activo: false,
};

/** The 450.00 / 6 quotas example of the statement, as the core returns it. */
const CREDITO: Credito = {
  codCredito: 9,
  codCliente: ANA.codCliente,
  cedula: ANA.cedula,
  nombreCliente: ANA.nombre,
  monto: 1099,
  numeroCuotas: 12,
  tasaAnual: 16.5,
  valorCuota: 100.13,
  fecha: '2026-01-15',
  estado: 'ACT',
  tablaAmortizacion: [
    { numCuota: 1, valorCuota: 100.13, interesPagado: 15.11, capitalPagado: 85.02, saldo: 1013.98 },
    { numCuota: 2, valorCuota: 100.13, interesPagado: 13.94, capitalPagado: 86.19, saldo: 927.79 },
  ],
};

const SUJETO: SujetoCredito = {
  cedula: ANA.cedula,
  codCliente: ANA.codCliente,
  nombre: ANA.nombre,
  sujetoCredito: true,
  motivos: [],
};

describe('FacturacionService (unit)', () => {
  let service: FacturacionService;

  /** Phones the fake catalog knows, by code. */
  const celulares: Repository<Celular> = {
    findOneBy: vi.fn(),
  } as unknown as Repository<Celular>;

  const clientes = {
    upsert: vi.fn(),
    findOneBy: vi.fn(),
  };

  const facturas = {
    create: vi.fn(),
    save: vi.fn(),
    find: vi.fn(),
    findOne: vi.fn(),
    findOneBy: vi.fn(),
  };

  const detalles = {
    create: vi.fn(),
  };

  const banquito = {
    sujetoCredito: vi.fn(),
    montoMaximo: vi.fn(),
    otorgarCredito: vi.fn(),
    amortizacion: vi.fn(),
  };

  /** `numFactura` of the next invoice the fake repository stores. */
  let siguienteFactura: number;

  const store = (): Repository<Factura> => facturas as unknown as Repository<Factura>;

  /** Number of invoices the service actually tried to store. */
  const facturasGuardadas = (): Factura[] =>
    facturas.save.mock.calls.map(([factura]) => factura as Factura);

  /** Body of the exception a promise rejects with. */
  const bodyOf = async (promise: Promise<unknown>): Promise<Record<string, unknown>> => {
    try {
      await promise;
    } catch (error) {
      if (error instanceof HttpException) {
        return error.getResponse() as Record<string, unknown>;
      }

      throw error;
    }

    throw new Error('Se esperaba un error y la operación terminó bien.');
  };

  const statusOf = async (promise: Promise<unknown>): Promise<number> => {
    try {
      await promise;
    } catch (error) {
      if (error instanceof HttpException) {
        return error.getStatus();
      }

      throw error;
    }

    throw new Error('Se esperaba un error y la operación terminó bien.');
  };

  const dto = (overrides: Partial<CreateFacturaDto>): CreateFacturaDto => ({
    cedula: ANA.cedula,
    nombre: ANA.nombre,
    codCelular: A16.codCelular,
    formaPago: 'EFE',
    ...overrides,
  });

  beforeEach(async () => {
    vi.clearAllMocks();
    siguienteFactura = 100;

    // The fake catalog answers with the phone the test asks for, or null.
    (celulares.findOneBy as unknown as ReturnType<typeof vi.fn>).mockImplementation(
      async ({ codCelular }: { codCelular: number }) =>
        [A16, S25, IPHONE, DESACTIVADO].find(
          (phone) => phone.codCelular === codCelular,
        ) ?? null,
    );

    clientes.upsert.mockResolvedValue(undefined);
    clientes.findOneBy.mockResolvedValue({
      codCliente: ANA.codCliente,
      cedula: ANA.cedula,
      nombre: ANA.nombre,
    });

    facturas.create.mockImplementation((values: Partial<Factura>) => values);
    facturas.save.mockImplementation(async (factura: Factura) => {
      // A stored invoice always carries the number the sequence generated.
      factura.numFactura = siguienteFactura++;
      return factura;
    });
    detalles.create.mockImplementation(
      (values: Partial<DetalleFactura>) => values,
    );

    const moduleRef = await Test.createTestingModule({
      providers: [
        FacturacionService,
        { provide: getRepositoryToken(Cliente), useValue: clientes },
        { provide: getRepositoryToken(Factura), useValue: store() },
        { provide: getRepositoryToken(DetalleFactura), useValue: detalles },
        { provide: getRepositoryToken(Celular), useValue: celulares },
        { provide: BanquitoClient, useValue: banquito },
      ],
    }).compile();

    service = moduleRef.get(FacturacionService);
  });

  describe('pago en efectivo (S11: 42% de descuento)', () => {
    it.each([
      { precio: 229, descuento: 96.18, total: 132.82 },
      { precio: 999, descuento: 419.58, total: 579.42 },
    ])(
      'aplica el 42% a $precio y guarda descuento $descuento con total $total',
      async ({ precio, descuento, total }) => {
        const phone = [A16, S25].find((row) => row.precio === precio)!;
        const codCelular = phone.codCelular;

        const factura = await service.facturar(dto({ codCelular }));

        expect(facturas.save).toHaveBeenCalledTimes(1);
        expect(facturasGuardadas()[0]).toMatchObject({
          formaPago: 'EFE',
          subtotal: precio,
          descuento,
          total,
          codCredito: null,
          numCuotas: null,
          valorCuota: null,
        });

        expect(factura).toMatchObject({
          numFactura: 100,
          formaPago: 'EFE',
          subtotal: precio,
          descuento,
          total,
          codCredito: null,
          credito: null,
          cliente: { cedula: ANA.cedula, nombre: ANA.nombre },
          detalles: [
            {
              cantidad: 1,
              precioUnitario: precio,
              subtotal: precio,
              celular: { codCelular, precio },
            },
          ],
        });

        // A cash sale never asks the bank for anything.
        expect(banquito.sujetoCredito).not.toHaveBeenCalled();
        expect(banquito.otorgarCredito).not.toHaveBeenCalled();
      },
    );

    it('da de alta al cliente por cédula y reutiliza el que ya existe', async () => {
      await service.facturar(dto({}));

      expect(clientes.upsert).toHaveBeenCalledWith(
        { cedula: ANA.cedula, nombre: ANA.nombre },
        { conflictPaths: ['cedula'] },
      );
      expect(facturasGuardadas()[0]).toMatchObject({
        codCliente: ANA.codCliente,
      });
    });
  });

  describe('crédito directo (S11: primero el Banco, después la factura)', () => {
    it('guarda el código del crédito y el valor de la cuota que otorgó el Banco', async () => {
      banquito.sujetoCredito.mockResolvedValue(SUJETO);
      banquito.montoMaximo.mockResolvedValue({
        codCliente: ANA.codCliente,
        promedioDepositos: 1212.5,
        promedioRetiros: 400,
        montoMaximo: 1706.25,
      } satisfies MontoMaximo);
      banquito.otorgarCredito.mockResolvedValue(CREDITO);

      const factura = await service.facturar(
        dto({ codCelular: IPHONE.codCelular, formaPago: 'CRE', numeroCuotas: 12 }),
      );

      expect(banquito.otorgarCredito).toHaveBeenCalledWith({
        codCliente: ANA.codCliente,
        precio: IPHONE.precio,
        numeroCuotas: 12,
      });

      expect(facturasGuardadas()[0]).toMatchObject({
        formaPago: 'CRE',
        subtotal: IPHONE.precio,
        descuento: 0,
        total: IPHONE.precio,
        codCredito: CREDITO.codCredito,
        numCuotas: 12,
        valorCuota: CREDITO.valorCuota,
      });

      // The answer carries the bank's amortization table for the invoice.
      expect(factura.credito).toEqual(CREDITO);
      expect(factura.credito?.tablaAmortizacion).toHaveLength(2);
    });

    it('responde 422 con los motivos del Banco y no guarda nada si no es sujeto de crédito', async () => {
      const motivos = [
        { codigo: 'NO_ES_CLIENTE', mensaje: 'La cédula no pertenece a un cliente.' },
      ];
      banquito.sujetoCredito.mockResolvedValue({
        cedula: '1799999999',
        sujetoCredito: false,
        motivos,
      } satisfies SujetoCredito);

      const body = await bodyOf(
        service.facturar(
          dto({ cedula: '1799999999', nombre: 'Nadie', formaPago: 'CRE', numeroCuotas: 6 }),
        ),
      );

      expect(body).toMatchObject({ statusCode: 422, motivos });
      expect(banquito.montoMaximo).not.toHaveBeenCalled();
      expect(banquito.otorgarCredito).not.toHaveBeenCalled();
      expect(clientes.upsert).not.toHaveBeenCalled();
      expect(facturas.save).not.toHaveBeenCalled();
    });

    it('responde 422 con el monto máximo y no pide el crédito cuando el precio lo supera', async () => {
      banquito.sujetoCredito.mockResolvedValue(SUJETO);
      banquito.montoMaximo.mockResolvedValue({
        codCliente: ANA.codCliente,
        promedioDepositos: 500,
        promedioRetiros: 0,
        montoMaximo: 525,
      } satisfies MontoMaximo);

      const body = await bodyOf(
        service.facturar(
          dto({ codCelular: S25.codCelular, formaPago: 'CRE', numeroCuotas: 18 }),
        ),
      );

      expect(body).toMatchObject({ statusCode: 422, montoMaximo: 525 });
      expect(banquito.otorgarCredito).not.toHaveBeenCalled();
      expect(clientes.upsert).not.toHaveBeenCalled();
      expect(facturas.save).not.toHaveBeenCalled();
    });

    it.each([2, 19])('responde 400 con %i cuotas y no llama al Banco', async (numeroCuotas) => {
      const body = await bodyOf(
        service.facturar(dto({ formaPago: 'CRE', numeroCuotas })),
      );

      expect(body).toMatchObject({ statusCode: 400 });
      expect(banquito.sujetoCredito).not.toHaveBeenCalled();
      expect(facturas.save).not.toHaveBeenCalled();
    });

    it('responde 400 cuando falta el número de cuotas', async () => {
      expect(
        await statusOf(service.facturar(dto({ formaPago: 'CRE' }))),
      ).toBe(400);
      expect(banquito.sujetoCredito).not.toHaveBeenCalled();
    });

    it('responde 502 cuando el Banco no está disponible y no guarda nada', async () => {
      banquito.sujetoCredito.mockRejectedValue(
        new BanquitoUnavailableError('GET /api/creditos/sujeto-credito'),
      );

      const body = await bodyOf(
        service.facturar(dto({ formaPago: 'CRE', numeroCuotas: 6 })),
      );

      expect(body).toMatchObject({
        statusCode: 502,
        message: 'El servicio del Banco BanQuito no está disponible',
      });
      expect(facturas.save).not.toHaveBeenCalled();
    });

    it('relaya el rechazo 4xx del Banco con su mensaje', async () => {
      banquito.sujetoCredito.mockResolvedValue(SUJETO);
      banquito.montoMaximo.mockResolvedValue({
        codCliente: ANA.codCliente,
        promedioDepositos: 1212.5,
        promedioRetiros: 400,
        montoMaximo: 1706.25,
      } satisfies MontoMaximo);
      banquito.otorgarCredito.mockRejectedValue(
        new BanquitoApiError(400, {
          codigo: 'CUOTAS_FUERA_DE_RANGO',
          mensaje: 'El número de cuotas debe estar entre 3 y 18.',
        }),
      );

      const body = await bodyOf(
        service.facturar(
          dto({ codCelular: IPHONE.codCelular, formaPago: 'CRE', numeroCuotas: 12 }),
        ),
      );

      expect(body).toMatchObject({
        statusCode: 400,
        message: 'El número de cuotas debe estar entre 3 y 18.',
        codigo: 'CUOTAS_FUERA_DE_RANGO',
      });
      expect(facturas.save).not.toHaveBeenCalled();
    });
  });

  describe('consulta de la evaluación de crédito (paso "Verificar crédito")', () => {
    it('devuelve el monto máximo y si la compra es aprobable', async () => {
      banquito.sujetoCredito.mockResolvedValue(SUJETO);
      banquito.montoMaximo.mockResolvedValue({
        codCliente: ANA.codCliente,
        promedioDepositos: 1212.5,
        promedioRetiros: 400,
        montoMaximo: 1706.25,
      } satisfies MontoMaximo);

      const evaluacion = await service.evaluar(ANA.cedula, IPHONE.codCelular);

      expect(evaluacion).toEqual({
        cedula: ANA.cedula,
        codCliente: ANA.codCliente,
        nombre: ANA.nombre,
        sujetoCredito: true,
        motivos: [],
        montoMaximo: 1706.25,
        precio: IPHONE.precio,
        aprobable: true,
      });
    });

    it('no pide el monto máximo de quien no es sujeto de crédito', async () => {
      banquito.sujetoCredito.mockResolvedValue({
        cedula: '1799999999',
        sujetoCredito: false,
        motivos: [{ codigo: 'NO_ES_CLIENTE', mensaje: 'No es cliente.' }],
      } satisfies SujetoCredito);

      const evaluacion = await service.evaluar('1799999999', S25.codCelular);

      expect(evaluacion).toMatchObject({
        codCliente: null,
        nombre: null,
        sujetoCredito: false,
        montoMaximo: null,
        precio: S25.precio,
        aprobable: false,
        motivos: [{ codigo: 'NO_ES_CLIENTE', mensaje: 'No es cliente.' }],
      });
      expect(banquito.montoMaximo).not.toHaveBeenCalled();
    });

    it('responde 404 con un celular desconocido y 400 con uno desactivado', async () => {
      expect(await statusOf(service.evaluar(ANA.cedula, 999999))).toBe(404);
      expect(
        await statusOf(service.evaluar(ANA.cedula, DESACTIVADO.codCelular)),
      ).toBe(400);
      expect(
        await statusOf(
          service.facturar(dto({ codCelular: DESACTIVADO.codCelular })),
        ),
      ).toBe(400);
      expect(banquito.sujetoCredito).not.toHaveBeenCalled();
      expect(facturas.save).not.toHaveBeenCalled();
    });
  });

  describe('tabla de amortización de una factura (S12)', () => {
    it('devuelve la tabla del Banco para una factura a crédito', async () => {
      facturas.findOneBy.mockResolvedValue({
        numFactura: 7,
        formaPago: 'CRE',
        codCredito: CREDITO.codCredito,
      });
      banquito.amortizacion.mockResolvedValue(CREDITO);

      const amortizacion = await service.amortizacion(7);

      expect(banquito.amortizacion).toHaveBeenCalledWith(CREDITO.codCredito);
      expect(amortizacion).toMatchObject({
        numFactura: 7,
        codCredito: CREDITO.codCredito,
        numeroCuotas: CREDITO.numeroCuotas,
        valorCuota: CREDITO.valorCuota,
        tablaAmortizacion: CREDITO.tablaAmortizacion,
      });
    });

    it('responde 409 con una factura en efectivo sin consultar al Banco', async () => {
      facturas.findOneBy.mockResolvedValue({
        numFactura: 8,
        formaPago: 'EFE',
        codCredito: null,
      });

      expect(await statusOf(service.amortizacion(8))).toBe(409);
      expect(banquito.amortizacion).not.toHaveBeenCalled();
    });

    it('responde 404 con una factura que no existe', async () => {
      facturas.findOneBy.mockResolvedValue(null);

      expect(await statusOf(service.amortizacion(999999))).toBe(404);
      expect(banquito.amortizacion).not.toHaveBeenCalled();
    });
  });
});
