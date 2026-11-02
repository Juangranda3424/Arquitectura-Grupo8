/**
 * Formatters shared by the invoicing screens.
 *
 * They are module constants instead of functions that build a formatter per
 * render: `Intl.NumberFormat` is expensive to construct.
 */

/** Money of the sale, in dollars (locale of the app, `es-EC`). */
export const usd = new Intl.NumberFormat("es-EC", {
  style: "currency",
  currency: "USD",
});

/** Plain amount with 2 decimals, for the amortization table cells. */
export const monto = new Intl.NumberFormat("es-EC", {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2,
});

/**
 * 42% discount of a cash sale (S11), rounded to cents like the API does
 * (`round2` in the API), so the preview and the invoice agree.
 */
export function descuentoEfectivo(precio: number): number {
  return Math.round((precio * 0.42 + Number.EPSILON) * 100) / 100;
}

/** Total of a cash sale: price minus the 42% discount. */
export function totalEfectivo(precio: number): number {
  return Math.round((precio - descuentoEfectivo(precio) + Number.EPSILON) * 100) / 100;
}

/** `2026-01-15T10:30:00.000Z` -> `15/01/2026, 05:30`. */
export function fechaHora(iso: string): string {
  const fecha = new Date(iso);

  if (Number.isNaN(fecha.getTime())) {
    return iso;
  }

  return new Intl.DateTimeFormat("es-EC", {
    dateStyle: "short",
    timeStyle: "short",
  }).format(fecha);
}

/** `EFE` -> `Efectivo`, `CRE` -> `Crédito directo`. */
export function formaPagoLegible(formaPago: "EFE" | "CRE"): string {
  return formaPago === "EFE" ? "Efectivo" : "Crédito directo";
}
