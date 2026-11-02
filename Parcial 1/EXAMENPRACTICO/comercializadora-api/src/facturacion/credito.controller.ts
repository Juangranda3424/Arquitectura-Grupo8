import { Controller, Get, Query } from '@nestjs/common';
import { EvaluacionCreditoQueryDto } from './dto/evaluacion-credito-query.dto.js';
import type { EvaluacionCreditoResponse } from './facturacion.types.js';
import { FacturacionService } from './facturacion.service.js';

/**
 * Credit evaluation the invoicing screen shows before selling on credit
 * ("Verificar crédito"): the bank's verdict on the buyer, its reasons, the
 * maximum amount for the phone price and whether the sale would be approved.
 *
 * It lives under `/api/credito` (not `/api/facturas`) because nothing is
 * issued here: it is a query against the BanQuito core.
 */
@Controller('api/credito')
export class CreditoController {
  constructor(private readonly facturacion: FacturacionService) {}

  @Get('evaluacion')
  evaluar(
    @Query() query: EvaluacionCreditoQueryDto,
  ): Promise<EvaluacionCreditoResponse> {
    return this.facturacion.evaluar(query.cedula, query.codCelular);
  }
}
