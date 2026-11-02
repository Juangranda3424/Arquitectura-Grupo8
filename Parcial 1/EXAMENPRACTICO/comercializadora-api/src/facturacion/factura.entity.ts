import {
  Column,
  Entity,
  JoinColumn,
  ManyToOne,
  OneToMany,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { numericTransformer } from '../database/numeric.transformer.js';
import { Cliente } from './cliente.entity.js';
import { DetalleFactura } from './detalle-factura.entity.js';

/** The two payment methods of the exam: cash and direct credit. */
export type FormaPago = 'EFE' | 'CRE';

/**
 * Invoice header (table `factura`, created by `RetailerSchema`).
 *
 * Cash (`EFE`) invoices carry the 42% discount and no credit; credit (`CRE`)
 * invoices store the BanQuito credit code plus its quota plan, and a check
 * constraint in the database enforces both shapes (3..18 quotas for credit).
 * The `detalles` relation is cascaded so an invoice and its lines are written
 * in a single `save`.
 */
@Entity({ name: 'factura' })
export class Factura {
  @PrimaryGeneratedColumn({ name: 'num_factura' })
  numFactura: number;

  @Column({
    name: 'fecha',
    type: 'timestamp',
    default: () => 'CURRENT_TIMESTAMP',
  })
  fecha: Date;

  @Column({ name: 'cod_cliente', type: 'int' })
  codCliente: number;

  @ManyToOne(() => Cliente)
  @JoinColumn({ name: 'cod_cliente' })
  cliente: Cliente;

  @Column({ name: 'forma_pago', type: 'varchar', length: 3 })
  formaPago: FormaPago;

  @Column({
    name: 'subtotal',
    type: 'numeric',
    precision: 10,
    scale: 2,
    transformer: numericTransformer,
  })
  subtotal: number;

  @Column({
    name: 'descuento',
    type: 'numeric',
    precision: 10,
    scale: 2,
    default: 0,
    transformer: numericTransformer,
  })
  descuento: number;

  @Column({
    name: 'total',
    type: 'numeric',
    precision: 10,
    scale: 2,
    transformer: numericTransformer,
  })
  total: number;

  /** BanQuito credit code; only for `formaPago = 'CRE'`. */
  @Column({ name: 'cod_credito', type: 'int', nullable: true })
  codCredito: number | null;

  @Column({ name: 'num_cuotas', type: 'int', nullable: true })
  numCuotas: number | null;

  @Column({
    name: 'valor_cuota',
    type: 'numeric',
    precision: 10,
    scale: 2,
    nullable: true,
    transformer: numericTransformer,
  })
  valorCuota: number | null;

  @OneToMany(() => DetalleFactura, (detalle) => detalle.factura, {
    cascade: true,
  })
  detalles: DetalleFactura[];
}
