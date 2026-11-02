import { IsNumber, IsOptional, IsPositive, IsString, Length, MaxLength } from 'class-validator';

/**
 * Body of `POST /api/celulares` (and, through `UpdateCelularDto`, of
 * `PUT /api/celulares/:id`).
 *
 * The limits mirror the column sizes of the `celular` table, so an invalid
 * body is rejected with 400 instead of failing inside PostgreSQL. `precio` is
 * a positive amount with at most 2 decimals, the scale of NUMERIC(10,2).
 */
export class CreateCelularDto {
  @IsString()
  @Length(1, 50)
  marca: string;

  @IsString()
  @Length(1, 100)
  modelo: string;

  @IsOptional()
  @IsString()
  @MaxLength(255)
  descripcion?: string;

  @IsNumber({ maxDecimalPlaces: 2 })
  @IsPositive()
  precio: number;
}
