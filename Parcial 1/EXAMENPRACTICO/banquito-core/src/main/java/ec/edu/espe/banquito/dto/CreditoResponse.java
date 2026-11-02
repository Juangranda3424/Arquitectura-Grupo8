package ec.edu.espe.banquito.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ec.edu.espe.banquito.model.Credito;

public record CreditoResponse(Integer codCredito, Integer codCliente, String cedula, String nombreCliente,
        BigDecimal monto, int numeroCuotas, BigDecimal tasaAnual, BigDecimal valorCuota, LocalDate fecha,
        String estado, List<CuotaResponse> tablaAmortizacion) {

    public static CreditoResponse from(Credito c) {
        return new CreditoResponse(c.getCodCredito(), c.getCliente().getCodCliente(), c.getCliente().getCedula(),
                c.getCliente().getNombre(), c.getMonto(), c.getNumCuotas(), c.getTasaAnual(), c.getValorCuota(),
                c.getFecha(), c.getEstado(), c.getTablaAmortizacion().stream().map(CuotaResponse::from).toList());
    }
}
