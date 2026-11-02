/**
 * Bodies of the retailer API the invoicing screens consume (S11, S12).
 *
 * They mirror `src/facturacion/facturacion.types.ts` of `comercializadora-api`
 * and `src/banquito/banquito.types.ts` of the bank, so the UI only has to
 * render what the API already computed.
 */

/** Phone as `GET /api/celulares` returns it. */
export interface Celular {
  codCelular: number;
  marca: string;
  modelo: string;
  descripcion: string | null;
  precio: number;
  activo: boolean;
}

/** A failed credit rule of the bank (rubric 5). */
export interface Motivo {
  codigo: string;
  mensaje: string;
}

/** `GET /api/credito/evaluacion`: the "Verificar crédito" answer. */
export interface EvaluacionCredito {
  cedula: string;
  codCliente: number | null;
  nombre: string | null;
  sujetoCredito: boolean;
  motivos: Motivo[];
  montoMaximo: number | null;
  precio: number;
  aprobable: boolean;
}

/** One row of the bank's amortization table. */
export interface CuotaAmortizacion {
  numCuota: number;
  valorCuota: number;
  interesPagado: number;
  capitalPagado: number;
  saldo: number;
}

/** The bank's credit, as the invoice response embeds it. */
export interface CreditoFactura {
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

/** `GET /api/facturas/:num/amortizacion`. */
export interface Amortizacion extends CreditoFactura {
  numFactura: number;
}

/** One invoice line: always a single phone. */
export interface DetalleFactura {
  codDetalle: number;
  cantidad: number;
  precioUnitario: number;
  subtotal: number;
  celular: {
    codCelular: number;
    marca: string;
    modelo: string;
    descripcion: string | null;
    precio: number;
  };
}

/** Invoice as the API returns it (`POST /api/facturas`, `GET /api/facturas`). */
export interface Factura {
  numFactura: number;
  fecha: string;
  formaPago: "EFE" | "CRE";
  subtotal: number;
  descuento: number;
  total: number;
  codCredito: number | null;
  numCuotas: number | null;
  valorCuota: number | null;
  cliente: {
    codCliente: number;
    cedula: string;
    nombre: string;
  };
  detalles: DetalleFactura[];
  credito: CreditoFactura | null;
}
