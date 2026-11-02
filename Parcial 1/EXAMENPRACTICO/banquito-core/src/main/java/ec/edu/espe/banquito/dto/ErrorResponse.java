package ec.edu.espe.banquito.dto;

import java.math.BigDecimal;
import java.util.List;

/** Error body of every 4xx answer; motivos/montoMaximo only when they apply. */
public record ErrorResponse(String codigo, String mensaje, List<Motivo> motivos, BigDecimal montoMaximo) {

    public ErrorResponse(String codigo, String mensaje) {
        this(codigo, mensaje, null, null);
    }
}
