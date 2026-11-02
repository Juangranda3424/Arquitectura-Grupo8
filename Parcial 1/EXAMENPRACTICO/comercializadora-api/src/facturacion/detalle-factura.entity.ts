import {
  Column,
  Entity,
  JoinColumn,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { Celular } from '../catalogo/celular.entity.js';
import { numericTransformer } from '../database/numeric.transformer.js';
import { Factura } from './factura.entity.js';

/**
 * One phone line of an invoice (table `detalle_factura`, created by
 * `RetailerSchema`).
 *
 * `precioUnitario` is copied from the catalog at sale time instead of being
 * read from `celular`, so a later price change never rewrites an issued
 * invoice.
 */
@Entity({ name: 'detalle_factura' })
export class DetalleFactura {
  @PrimaryGeneratedColumn({ name: 'cod_detalle' })
  codDetalle: number;

  @Column({ name: 'num_factura', type: 'int' })
  numFactura: number;

  @ManyToOne(() => Factura, (factura) => factura.detalles, {
    onDelete: 'CASCADE',
  })
  @JoinColumn({ name: 'num_factura' })
  factura: Factura;

  @Column({ name: 'cod_celular', type: 'int' })
  codCelular: number;

  @ManyToOne(() => Celular)
  @JoinColumn({ name: 'cod_celular' })
  celular: Celular;

  @Column({ name: 'cantidad', type: 'int' })
  cantidad: number;

  @Column({
    name: 'precio_unitario',
    type: 'numeric',
    precision: 10,
    scale: 2,
    transformer: numericTransformer,
  })
  precioUnitario: number;

  @Column({
    name: 'subtotal',
    type: 'numeric',
    precision: 10,
    scale: 2,
    transformer: numericTransformer,
  })
  subtotal: number;
}
