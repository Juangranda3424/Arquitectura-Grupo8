import type { ValueTransformer } from 'typeorm';

/**
 * Rounds to 2 decimals (HALF_UP for positive amounts), killing the binary
 * noise of the float conversion. It is also what the invoicing module uses for
 * the 42% cash discount (S11), so the amount stored and the amount shown are
 * computed the same way.
 */
export function round2(value: number): number {
  return Math.round((value + Number.EPSILON) * 100) / 100;
}

/**
 * Transformer for the NUMERIC(10,2) columns.
 *
 * The `pg` driver returns NUMERIC as a string ("229.00") to avoid losing
 * precision, so without this transformer every money field would reach the
 * JSON body as text. `from` converts it back to a number and rounds it to 2
 * decimals: the database already stores the scale, and the rounding keeps a
 * value like 0.1 + 0.2 out of the response (229.00000000000003 would be a
 * wrong price to show and to send to BanQuito).
 *
 * Use it on every `numeric` column whose TypeScript type is `number`; nullable
 * money columns (like `factura.valor_cuota`) keep `null` as `null`.
 */
export const numericTransformer: ValueTransformer = {
  to: (value: number | null | undefined) => value ?? null,
  from: (value: string | number | null | undefined) =>
    value === null || value === undefined ? null : round2(Number(value)),
};
