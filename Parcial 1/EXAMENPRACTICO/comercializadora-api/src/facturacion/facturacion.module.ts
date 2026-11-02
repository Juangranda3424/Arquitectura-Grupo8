import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Cliente } from './cliente.entity.js';
import { DetalleFactura } from './detalle-factura.entity.js';
import { Factura } from './factura.entity.js';

/**
 * Invoicing model (S11): `cliente`, `factura` and `detalle_factura`.
 *
 * T6 creates the tables and their entities because the catalog migration owns
 * the whole retailer schema; the invoicing endpoints and the BanQuito calls
 * arrive in T7 and only have to add their own service and controller to this
 * module. Registering the entities now keeps TypeORM aware of them (and of
 * their relations) instead of leaving the mappings untested until later.
 */
@Module({
  imports: [TypeOrmModule.forFeature([Cliente, Factura, DetalleFactura])],
})
export class FacturacionModule {}
