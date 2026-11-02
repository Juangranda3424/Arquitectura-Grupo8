import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { BanquitoModule } from '../banquito/banquito.module.js';
import { Celular } from '../catalogo/celular.entity.js';
import { Cliente } from './cliente.entity.js';
import { CreditoController } from './credito.controller.js';
import { DetalleFactura } from './detalle-factura.entity.js';
import { Factura } from './factura.entity.js';
import { FacturasController } from './facturas.controller.js';
import { FacturacionService } from './facturacion.service.js';

/**
 * Invoicing model (S11) and the amortization view (S12): `cliente`, `factura`
 * and `detalle_factura` (tables created by the `RetailerSchema` migration),
 * plus the credit evaluation the invoicing screen needs.
 *
 * `Celular` is registered here too because an invoice reads the phone price
 * from the catalog, and `BanquitoModule` provides the client of the core.
 */
@Module({
  imports: [
    TypeOrmModule.forFeature([Cliente, Factura, DetalleFactura, Celular]),
    BanquitoModule,
  ],
  controllers: [CreditoController, FacturasController],
  providers: [FacturacionService],
})
export class FacturacionModule {}
