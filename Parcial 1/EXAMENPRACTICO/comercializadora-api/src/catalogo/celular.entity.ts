import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';
import { numericTransformer } from '../database/numeric.transformer.js';

/**
 * A phone on sale (table `celular`, created by `RetailerSchema`).
 *
 * There is no inventory in this exam: the catalog is the list of models the
 * retailer sells. A phone is never deleted, because invoices reference it;
 * `activo` is flipped to `false` instead (`DELETE /api/celulares/:id`).
 */
@Entity({ name: 'celular' })
export class Celular {
  @PrimaryGeneratedColumn({ name: 'cod_celular' })
  codCelular: number;

  @Column({ name: 'marca', type: 'varchar', length: 50 })
  marca: string;

  @Column({ name: 'modelo', type: 'varchar', length: 100 })
  modelo: string;

  @Column({ name: 'descripcion', type: 'varchar', length: 255, nullable: true })
  descripcion: string | null;

  /** Price in USD; `numericTransformer` keeps it a number with 2 decimals. */
  @Column({
    name: 'precio',
    type: 'numeric',
    precision: 10,
    scale: 2,
    transformer: numericTransformer,
  })
  precio: number;

  @Column({ name: 'activo', type: 'boolean', default: true })
  activo: boolean;
}
