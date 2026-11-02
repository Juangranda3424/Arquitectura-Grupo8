package ec.edu.espe.banquito.dto;

import java.math.BigDecimal;

public record MontoMaximoResponse(Integer codCliente, BigDecimal promedioDepositos, BigDecimal promedioRetiros,
        BigDecimal montoMaximo) {
}
