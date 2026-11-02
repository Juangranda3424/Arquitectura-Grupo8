import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

/**
 * Customer a `factura` belongs to (table `cliente`, created by
 * `RetailerSchema`). The retailer keeps its own copy of the buyer, keyed by
 * `cedula`, because the invoice has to survive without asking BanQuito.
 */
@Entity({ name: 'cliente' })
export class Cliente {
  @PrimaryGeneratedColumn({ name: 'cod_cliente' })
  codCliente: number;

  @Column({ name: 'cedula', type: 'varchar', length: 10, unique: true })
  cedula: string;

  @Column({ name: 'nombre', type: 'varchar', length: 100 })
  nombre: string;
}
