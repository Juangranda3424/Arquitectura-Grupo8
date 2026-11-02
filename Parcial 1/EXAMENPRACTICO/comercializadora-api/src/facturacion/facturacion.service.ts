import {
  BadRequestException,
  ConflictException,
  Injectable,
  InternalServerErrorException,
  Logger,
  NotFoundException,
  UnprocessableEntityException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { BanquitoClient } from '../banquito/banquito.client.js';
import { rethrowAsHttp } from '../banquito/banquito.errors.js';
import type { Credito } from '../banquito/banquito.types.js';
import { Celular } from '../catalogo/celular.entity.js';
import { round2 } from '../database/numeric.transformer.js';
import { Cliente } from './cliente.entity.js';
import { DetalleFactura } from './detalle-factura.entity.js';
import type { CreateFacturaDto } from './dto/create-factura.dto.js';
import { Factura } from './factura.entity.js';
import type {
  AmortizacionResponse,
  EvaluacionCreditoResponse,
  FacturaResponse,
} from './facturacion.types.js';

/** S11: "compras en efectivo se deberá dar un descuento del 42% del precio". */
const DESCUENTO_EFECTIVO = 0.42;

/** S7: the bank grants credits with a fixed monthly quota between 3 and 18. */
const CUOTAS_MIN = 3;
const CUOTAS_MAX = 18;

const CELULAR_NO_ENCONTRADO = 'No existe un celular con ese código.';
const CELULAR_INACTIVO =
  'El celular está desactivado y no se puede facturar.';
const FACTURA_NO_ENCONTRADA = 'No existe una factura con ese número.';
const CUOTAS_FUERA_DE_RANGO = `El número de cuotas debe estar entre ${CUOTAS_MIN} y ${CUOTAS_MAX}.`;
const FACTURA_SIN_CREDITO =
  'La factura es en efectivo y no tiene tabla de amortización.';
const NO_SUJETO_CREDITO = 'El cliente no es sujeto de crédito.';
const CLIENTE_NO_GUARDADO =
  'No se pudo registrar al cliente de la factura.';

/** Everything `guardar` needs to write one invoice with its single line. */
interface GuardarFacturaInput {
  dto: CreateFacturaDto;
  celular: Celular;
  descuento: number;
  total: number;
  codCredito: number | null;
  numCuotas: number | null;
  valorCuota: number | null;
}

/**
 * Invoicing of the retailer (S11) and the credit amortization view (S12).
 *
 * Every credit decision belongs to the BanQuito core: this service only calls
 * it (`BanquitoClient`) and translates the answer into an invoice. The order
 * matters, because a credit cannot be undone once it is granted:
 *
 * 1. cash (`EFE`): 42% discount on the phone price, no bank call at all;
 * 2. credit (`CRE`): the bank is asked whether the buyer is a credit subject,
 *    then for the maximum amount, and only when both pass is the credit
 *    granted (`POST /api/creditos`) — and only after that is the invoice
 *    stored with the returned credit code and quota value.
 *
 * If the invoice cannot be stored after the bank granted the credit, there is
 * no compensation endpoint in the core: the error is logged with the credit
 * code so the case can be fixed by hand (see the README), and the seller gets
 * the storage error.
 */
@Injectable()
export class FacturacionService {
  private readonly logger = new Logger(FacturacionService.name);

  constructor(
    @InjectRepository(Cliente)
    private readonly clientes: Repository<Cliente>,
    @InjectRepository(Factura)
    private readonly facturas: Repository<Factura>,
    @InjectRepository(DetalleFactura)
    private readonly detalles: Repository<DetalleFactura>,
    @InjectRepository(Celular)
    private readonly celulares: Repository<Celular>,
    private readonly banquito: BanquitoClient,
  ) {}

  /**
   * Step "Verificar crédito" of the invoicing screen: the bank's verdict on
   * the buyer plus the phone price, without granting anything.
   */
  async evaluar(
    cedula: string,
    codCelular: number,
  ): Promise<EvaluacionCreditoResponse> {
    const celular = await this.findCelular(codCelular);
    const sujeto = await this.callBanco(() =>
      this.banquito.sujetoCredito(cedula),
    );
    const codCliente = sujeto.codCliente ?? null;

    // The maximum amount only exists for a client, and the core answers 404
    // for anyone else: ask for it only when the bank already said yes.
    const montoMaximo =
      sujeto.sujetoCredito && codCliente !== null
        ? (
            await this.callBanco(() =>
              this.banquito.montoMaximo(codCliente),
            )
          ).montoMaximo
        : null;

    return {
      cedula,
      codCliente,
      nombre: sujeto.nombre ?? null,
      sujetoCredito: sujeto.sujetoCredito,
      motivos: sujeto.motivos ?? [],
      montoMaximo,
      precio: celular.precio,
      aprobable:
        sujeto.sujetoCredito &&
        montoMaximo !== null &&
        celular.precio <= montoMaximo,
    };
  }

  /** Issues one invoice: cash with the 42% discount, or direct credit. */
  async facturar(dto: CreateFacturaDto): Promise<FacturaResponse> {
    if (dto.formaPago === 'EFE') {
      return this.facturarEfectivo(dto);
    }

    // Validated before any I/O, so a bad quota count costs no bank call.
    const numCuotas = this.readCuotas(dto.numeroCuotas);
    const celular = await this.findCelular(dto.codCelular);

    return this.facturarCredito(dto, celular, numCuotas);
  }

  /** Invoices, newest first; `cedula` narrows the list to one buyer. */
  async listar(cedula?: string): Promise<FacturaResponse[]> {
    const facturas = await this.facturas.find({
      where: cedula ? { cliente: { cedula } } : {},
      relations: { cliente: true, detalles: { celular: true } },
      order: { numFactura: 'DESC' },
    });

    return facturas.map((factura) => this.toResponse(factura, null));
  }

  /** One invoice with its customer and its phone lines. */
  async obtener(numFactura: number): Promise<FacturaResponse> {
    const factura = await this.facturas.findOne({
      where: { numFactura },
      relations: { cliente: true, detalles: { celular: true } },
    });

    if (!factura) {
      throw new NotFoundException(FACTURA_NO_ENCONTRADA);
    }

    return this.toResponse(factura, null);
  }

  /**
   * Rubric 11 (S12): amortization table of the credit behind an invoice, read
   * from the bank so the retailer shows the same numbers the core stores.
   */
  async amortizacion(numFactura: number): Promise<AmortizacionResponse> {
    const factura = await this.facturas.findOneBy({ numFactura });

    if (!factura) {
      throw new NotFoundException(FACTURA_NO_ENCONTRADA);
    }

    if (factura.formaPago !== 'CRE' || factura.codCredito === null) {
      throw new ConflictException(FACTURA_SIN_CREDITO);
    }

    const codCredito = factura.codCredito;
    const credito = await this.callBanco(() =>
      this.banquito.amortizacion(codCredito),
    );

    return { numFactura: factura.numFactura, ...credito };
  }

  private async facturarEfectivo(
    dto: CreateFacturaDto,
  ): Promise<FacturaResponse> {
    const celular = await this.findCelular(dto.codCelular);
    const descuento = round2(celular.precio * DESCUENTO_EFECTIVO);
    const total = round2(celular.precio - descuento);

    const factura = await this.guardar({
      dto,
      celular,
      descuento,
      total,
      codCredito: null,
      numCuotas: null,
      valorCuota: null,
    });

    return this.toResponse(factura, null);
  }

  private async facturarCredito(
    dto: CreateFacturaDto,
    celular: Celular,
    numCuotas: number,
  ): Promise<FacturaResponse> {
    const sujeto = await this.callBanco(() =>
      this.banquito.sujetoCredito(dto.cedula),
    );
    const codCliente = sujeto.codCliente;

    if (!sujeto.sujetoCredito || codCliente === undefined) {
      throw new UnprocessableEntityException({
        statusCode: 422,
        message: NO_SUJETO_CREDITO,
        motivos: sujeto.motivos ?? [],
      });
    }

    const { montoMaximo } = await this.callBanco(() =>
      this.banquito.montoMaximo(codCliente),
    );

    if (celular.precio > montoMaximo) {
      throw new UnprocessableEntityException({
        statusCode: 422,
        message: `El precio de $${celular.precio.toFixed(2)} supera el monto máximo aprobado de $${montoMaximo.toFixed(2)}.`,
        montoMaximo,
      });
    }

    const credito = await this.callBanco(() =>
      this.banquito.otorgarCredito({
        codCliente,
        precio: celular.precio,
        numeroCuotas: numCuotas,
      }),
    );

    let factura: Factura;

    try {
      factura = await this.guardar({
        dto,
        celular,
        // A credit sale pays the full price: the discount is only for cash.
        descuento: 0,
        total: celular.precio,
        codCredito: credito.codCredito,
        numCuotas,
        valorCuota: credito.valorCuota,
      });
    } catch (error) {
      // No compensation endpoint exists in the core: the credit stays granted.
      this.logger.error(
        `El Banco otorgó el crédito ${credito.codCredito} a la cédula ${dto.cedula} por $${celular.precio.toFixed(2)} a ${numCuotas} cuotas, pero la factura no se pudo guardar. El crédito queda otorgado sin factura en la comercializadora.`,
      );
      throw error;
    }

    return this.toResponse(factura, credito);
  }

  /**
   * Writes the customer (upsert by cédula), the invoice and its single phone
   * line. `detalles` is a cascaded relation, so one `save` writes the header
   * and the line in the same transaction.
   */
  private async guardar(input: GuardarFacturaInput): Promise<Factura> {
    const cliente = await this.upsertCliente(
      input.dto.cedula,
      input.dto.nombre,
    );

    const factura = this.facturas.create({
      // Explicit instead of the column default: the response echoes it.
      fecha: new Date(),
      codCliente: cliente.codCliente,
      cliente,
      formaPago: input.dto.formaPago,
      subtotal: input.celular.precio,
      descuento: input.descuento,
      total: input.total,
      codCredito: input.codCredito,
      numCuotas: input.numCuotas,
      valorCuota: input.valorCuota,
      detalles: [
        this.detalles.create({
          codCelular: input.celular.codCelular,
          celular: input.celular,
          cantidad: 1,
          precioUnitario: input.celular.precio,
          subtotal: input.celular.precio,
        }),
      ],
    });

    return this.facturas.save(factura);
  }

  /**
   * Upserts the buyer in `cliente` by cédula: the retailer keeps its own copy
   * of the name, so a repeated sale does not duplicate the customer and a
   * changed name is the latest one. `upsert` keeps the uniqueness of `cedula`
   * in the database even when two sellers invoice the same new buyer at once.
   */
  private async upsertCliente(
    cedula: string,
    nombre: string,
  ): Promise<Cliente> {
    await this.clientes.upsert({ cedula, nombre }, { conflictPaths: ['cedula'] });

    const cliente = await this.clientes.findOneBy({ cedula });

    if (!cliente) {
      throw new InternalServerErrorException(CLIENTE_NO_GUARDADO);
    }

    return cliente;
  }

  /** Phone on sale, or 404 for an unknown code and 400 for a deactivated one. */
  private async findCelular(codCelular: number): Promise<Celular> {
    const celular = await this.celulares.findOneBy({ codCelular });

    if (!celular) {
      throw new NotFoundException(CELULAR_NO_ENCONTRADO);
    }

    if (!celular.activo) {
      throw new BadRequestException(CELULAR_INACTIVO);
    }

    return celular;
  }

  /** `numeroCuotas` of a credit sale: required, integer, 3..18 (S7). */
  private readCuotas(numeroCuotas?: number): number {
    if (
      numeroCuotas === undefined ||
      !Number.isInteger(numeroCuotas) ||
      numeroCuotas < CUOTAS_MIN ||
      numeroCuotas > CUOTAS_MAX
    ) {
      throw new BadRequestException(CUOTAS_FUERA_DE_RANGO);
    }

    return numeroCuotas;
  }

  /** Runs a bank call and turns its failure into the API's own exception. */
  private async callBanco<T>(call: () => Promise<T>): Promise<T> {
    try {
      return await call();
    } catch (error) {
      rethrowAsHttp(error);
    }
  }

  private toResponse(
    factura: Factura,
    credito: Credito | null,
  ): FacturaResponse {
    return {
      numFactura: factura.numFactura,
      fecha: factura.fecha.toISOString(),
      formaPago: factura.formaPago,
      subtotal: factura.subtotal,
      descuento: factura.descuento,
      total: factura.total,
      codCredito: factura.codCredito,
      numCuotas: factura.numCuotas,
      valorCuota: factura.valorCuota,
      cliente: {
        codCliente: factura.cliente.codCliente,
        cedula: factura.cliente.cedula,
        nombre: factura.cliente.nombre,
      },
      detalles: (factura.detalles ?? []).map((detalle) => ({
        codDetalle: detalle.codDetalle,
        cantidad: detalle.cantidad,
        precioUnitario: detalle.precioUnitario,
        subtotal: detalle.subtotal,
        celular: {
          codCelular: detalle.celular.codCelular,
          marca: detalle.celular.marca,
          modelo: detalle.celular.modelo,
          descripcion: detalle.celular.descripcion,
          precio: detalle.celular.precio,
        },
      })),
      credito,
    };
  }
}
