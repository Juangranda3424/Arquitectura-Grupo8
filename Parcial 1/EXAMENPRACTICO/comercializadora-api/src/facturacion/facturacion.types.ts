import type { Motivo, CuotaAmortizacion } from '../banquito/banquito.types.js';
import type { FormaPago } from './factura.entity.js';

/**
 * Bodies the invoicing endpoints answer (S11, S12).
 *
 * They are built by `FacturacionService` instead of returning the TypeORM
 * entities, so the API never leaks a column the screen does not need and the
 * money fields keep their 2 decimals.
 */

/** `GET /api/credito/evaluacion`: what the seller sees before selling on credit. */
export interface EvaluacionCreditoResponse {
  cedula: string;
  /** `null` when the cédula does not belong to a BanQuito client. */
  codCliente: number | null;
  nombre: string | null;
  sujetoCredito: boolean;
  /** Failed rules of the bank (rubric 5), shown as they are in the UI. */
  motivos: Motivo[];
  /** Only asked of a credit subject; `null` otherwise. */
  montoMaximo: number | null;
  precio: number;
  /** True when the bank would grant this credit for this phone today. */
  aprobable: boolean;
}

/** Phone data copied into the invoice response. */
export interface CelularFacturadoResponse {
  codCelular: number;
  marca: string;
  modelo: string;
  descripcion: string | null;
  precio: number;
}

export interface DetalleFacturaResponse {
  codDetalle: number;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  celular: CelularFacturadoResponse;
}

export interface ClienteFacturaResponse {
  codCliente: number;
  cedula: string;
  nombre: string;
}

/** Invoice as `POST /api/facturas` and `GET /api/facturas/:num` return it. */
export interface FacturaResponse {
  numFactura: number;
  /** ISO timestamp. */
  fecha: string;
  formaPago: FormaPago;
  subtotal: number;
  descuento: number;
  total: number;
  /** BanQuito credit code; `null` for a cash invoice. */
  codCredito: number | null;
  numCuotas: number | null;
  valorCuota: number | null;
  cliente: ClienteFacturaResponse;
  detalles: DetalleFacturaResponse[];
  /**
   * Credit the bank returned when the invoice was issued (with its
   * amortization table); `null` for cash and for the list/detail reads, which
   * do not call the bank.
   */
  credito: CreditoFacturaResponse | null;
}

/** The bank's credit, as the invoice response embeds it. */
export interface CreditoFacturaResponse {
  codCredito: number;
  codCliente: number;
  cedula: string;
  nombreCliente: string;
  monto: number;
  numeroCuotas: number;
  tasaAnual: number;
  valorCuota: number;
  fecha: string;
  estado: string;
  tablaAmortizacion: CuotaAmortizacion[];
}

/** `GET /api/facturas/:num/amortizacion` (rubric 11 / S12). */
export interface AmortizacionResponse extends CreditoFacturaResponse {
  numFactura: number;
}
