import { Type } from 'class-transformer';
import { IsInt, IsPositive, IsString, Matches } from 'class-validator';

/**
 * Query of `GET /api/credito/evaluacion` (the "Verificar crédito" step of the
 * invoicing screen): whose cédula, and which phone is being quoted.
 */
export class EvaluacionCreditoQueryDto {
  @IsString()
  @Matches(/^\d{10}$/, { message: 'La cédula debe tener 10 dígitos.' })
  cedula: string;

  // Query strings arrive as text; the global validation pipe transforms them
  // into numbers because of `@Type`, so the service gets a number.
  @Type(() => Number)
  @IsInt()
  @IsPositive()
  codCelular: number;
}
