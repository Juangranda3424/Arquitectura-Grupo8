import {
  IsIn,
  IsInt,
  IsOptional,
  IsPositive,
  IsString,
  Length,
  Matches,
} from 'class-validator';
import type { FormaPago } from '../factura.entity.js';

/**
 * Body of `POST /api/facturas` (S11).
 *
 * One phone per invoice (`codCelular` plus the implied `cantidad = 1`), and
 * the credit amount is the phone price, so the body carries no amounts at all:
 * they are read from the catalog and from the bank, never from the seller.
 *
 * `numeroCuotas` is only checked for presence and integer-ness here; the
 * 3..18 range is a business rule of the credit sale and lives in
 * `FacturacionService` (one place, reachable from the unit tests).
 */
export class CreateFacturaDto {
  /** Buyer cédula; the retailer upserts `cliente` by this key. */
  @IsString()
  @Matches(/^\d{10}$/, { message: 'La cédula debe tener 10 dígitos.' })
  cedula: string;

  @IsString()
  @Length(1, 100)
  nombre: string;

  @IsInt()
  @IsPositive()
  codCelular: number;

  @IsIn(['EFE', 'CRE'], {
    message: 'La forma de pago debe ser EFE (efectivo) o CRE (crédito directo).',
  })
  formaPago: FormaPago;

  /** Required for `CRE` (3..18); ignored by a cash sale. */
  @IsOptional()
  @IsInt()
  numeroCuotas?: number;
}
