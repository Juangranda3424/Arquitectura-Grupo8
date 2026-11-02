import { IsOptional, IsString, Matches } from 'class-validator';

/**
 * Query of `GET /api/facturas`: the optional cédula filter of the invoice list.
 */
export class ListarFacturasQueryDto {
  @IsOptional()
  @IsString()
  @Matches(/^\d{10}$/, { message: 'La cédula debe tener 10 dígitos.' })
  cedula?: string;
}
