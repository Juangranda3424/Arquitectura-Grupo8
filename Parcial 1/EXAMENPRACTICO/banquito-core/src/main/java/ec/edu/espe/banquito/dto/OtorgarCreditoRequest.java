package ec.edu.espe.banquito.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

/** Rubric 7 parameters: client code, phone price and number of installments. */
public record OtorgarCreditoRequest(
        @NotNull Integer codCliente,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 8, fraction = 2) BigDecimal precio,
        @NotNull Integer numeroCuotas) {
}
