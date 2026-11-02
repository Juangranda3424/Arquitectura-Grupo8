package ec.edu.espe.banquito.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Business parameters of the credit module (application.properties, prefix banquito.credito).
 *
 * @param porcentajeMontoMaximo     share of the (deposits - withdrawals) average difference, 0.35
 * @param multiplicadorMontoMaximo  months multiplier of the maximum amount, 6
 * @param tasaInteresAnual          annual interest rate in percent, 16.50
 * @param cuotasMinimas             minimum term in months, 3
 * @param cuotasMaximas             maximum term in months, 18
 */
@ConfigurationProperties(prefix = "banquito.credito")
public record CreditoProperties(
        BigDecimal porcentajeMontoMaximo,
        int multiplicadorMontoMaximo,
        BigDecimal tasaInteresAnual,
        int cuotasMinimas,
        int cuotasMaximas) {
}
