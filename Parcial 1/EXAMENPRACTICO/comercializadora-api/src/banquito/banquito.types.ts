/**
 * Bodies of the BanQuito Core REST services the retailer consumes (S5-S9).
 *
 * They mirror the Java records of `banquito-core` one to one, including the
 * optional fields: the core omits `codCliente` and `nombre` when the cédula
 * does not belong to a client, and `motivos`/`montoMaximo` only appear in the
 * rejection bodies.
 */

/** A failed credit rule: machine code plus a readable message. */
export interface Motivo {
  codigo: string;
  mensaje: string;
}

/** `GET /api/creditos/sujeto-credito?cedula=X` (rubric 5). */
export interface SujetoCredito {
  cedula: string;
  codCliente?: number;
  nombre?: string;
  sujetoCredito: boolean;
  motivos: Motivo[];
}

/** `GET /api/creditos/monto-maximo/{codCliente}` (rubric 6). */
export interface MontoMaximo {
  codCliente: number;
  promedioDepositos: number;
  promedioRetiros: number;
  montoMaximo: number;
}

/** One row of the amortization table (rubric 7 and 10). */
export interface CuotaAmortizacion {
  numCuota: number;
  valorCuota: number;
  interesPagado: number;
  capitalPagado: number;
  saldo: number;
}

/** A granted credit with its amortization table (rubrics 7, 9 and 10). */
export interface Credito {
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

/** Body of `POST /api/creditos` (rubric 7). */
export interface OtorgarCreditoRequest {
  codCliente: number;
  precio: number;
  numeroCuotas: number;
}

/**
 * Error body of the core (`ErrorResponse` in Java): `motivos` only comes with
 * `NO_ES_SUJETO_CREDITO` and `montoMaximo` only with `MONTO_EXCEDE_MAXIMO`.
 */
export interface BancoError {
  codigo?: string;
  mensaje?: string;
  motivos?: Motivo[];
  montoMaximo?: number;
}
