package ec.edu.espe.banquito.dto;

import java.math.BigDecimal;

import ec.edu.espe.banquito.model.TablaAmortizacion;

public record CuotaResponse(int numCuota, BigDecimal valorCuota, BigDecimal interesPagado, BigDecimal capitalPagado,
        BigDecimal saldo) {

    public static CuotaResponse from(TablaAmortizacion t) {
        return new CuotaResponse(t.getNumCuota(), t.getValorCuota(), t.getInteresPagado(), t.getCapitalPagado(),
                t.getSaldo());
    }
}
