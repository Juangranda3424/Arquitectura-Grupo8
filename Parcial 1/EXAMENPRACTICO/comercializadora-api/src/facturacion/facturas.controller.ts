import {
  Body,
  Controller,
  Get,
  Param,
  ParseIntPipe,
  Post,
  Query,
} from '@nestjs/common';
import { CreateFacturaDto } from './dto/create-factura.dto.js';
import { ListarFacturasQueryDto } from './dto/listar-facturas-query.dto.js';
import type {
  AmortizacionResponse,
  FacturaResponse,
} from './facturacion.types.js';
import { FacturacionService } from './facturacion.service.js';

/**
 * Invoicing of the retailer (S11) and the credit amortization query (S12).
 *
 * No `@AllowAnonymous()`: the global Better Auth guard of the auth module
 * rejects every route with 401 when there is no session.
 *
 * | Route | Effect |
 * |---|---|
 * | `POST /api/facturas` | issues a cash or credit invoice, 201 |
 * | `GET /api/facturas` | invoices, newest first; `?cedula=` filters one buyer |
 * | `GET /api/facturas/:num` | one invoice with its customer and phone lines |
 * | `GET /api/facturas/:num/amortizacion` | the bank's table of a credit invoice |
 */
@Controller('api/facturas')
export class FacturasController {
  constructor(private readonly facturacion: FacturacionService) {}

  @Get()
  findAll(@Query() query: ListarFacturasQueryDto): Promise<FacturaResponse[]> {
    return this.facturacion.listar(query.cedula);
  }

  // Declared before `:num` so the two-segment route is never read as an id.
  @Get(':num/amortizacion')
  amortizacion(
    @Param('num', ParseIntPipe) num: number,
  ): Promise<AmortizacionResponse> {
    return this.facturacion.amortizacion(num);
  }

  @Get(':num')
  findOne(@Param('num', ParseIntPipe) num: number): Promise<FacturaResponse> {
    return this.facturacion.obtener(num);
  }

  @Post()
  create(@Body() dto: CreateFacturaDto): Promise<FacturaResponse> {
    return this.facturacion.facturar(dto);
  }
}
