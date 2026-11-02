package ec.edu.espe.banquito.service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Fixed-installment (French) amortization with monthly payments.
 *
 * <pre>
 *                 Valor
 * Cuota = -------------------------      TasaPeriodo = TasaAnual / 12
 *          1 - (1 + TasaPeriodo)^-n
 *          ------------------------
 *                TasaPeriodo
 * </pre>
 *
 * The installment is rounded to cents first (as the exam sample and Excel PAGO show it); interest,
 * capital and balance are then carried at full precision from that installment and only the stored
 * values are rounded HALF_UP to cents. The last balance closes at 0.00. This reproduces the exam
 * sample table (450.00, 6 cuotas, 16.5%) to the cent.
 */
@Component
public class AmortizacionCalculator {

    private static final MathContext MC = MathContext.DECIMAL128;
    private static final BigDecimal DOCE = BigDecimal.valueOf(12);
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    public record CuotaCalculada(int numCuota, BigDecimal valorCuota, BigDecimal interesPagado,
            BigDecimal capitalPagado, BigDecimal saldo) {
    }

    public record PlanPagos(BigDecimal valorCuota, List<CuotaCalculada> cuotas) {
    }

    public PlanPagos calcular(BigDecimal monto, int numeroCuotas, BigDecimal tasaAnualPorcentaje) {
        BigDecimal tasaPeriodo = tasaAnualPorcentaje.divide(CIEN, MC).divide(DOCE, MC);
        BigDecimal factor = BigDecimal.ONE.add(tasaPeriodo).pow(numeroCuotas, MC);
        // P * i / (1 - (1+i)^-n)  ==  P * i * (1+i)^n / ((1+i)^n - 1)
        BigDecimal cuota = monto.multiply(tasaPeriodo, MC).multiply(factor, MC)
                .divide(factor.subtract(BigDecimal.ONE), MC)
                .setScale(2, RoundingMode.HALF_UP);

        List<CuotaCalculada> cuotas = new ArrayList<>(numeroCuotas);
        BigDecimal saldo = monto;
        for (int n = 1; n <= numeroCuotas; n++) {
            BigDecimal interes = saldo.multiply(tasaPeriodo, MC);
            BigDecimal capital = cuota.subtract(interes, MC);
            saldo = saldo.subtract(capital, MC);
            BigDecimal saldoMostrado = n == numeroCuotas ? BigDecimal.ZERO.setScale(2) : cents(saldo);
            cuotas.add(new CuotaCalculada(n, cuota, cents(interes), cents(capital), saldoMostrado));
        }
        return new PlanPagos(cuota, List.copyOf(cuotas));
    }

    private static BigDecimal cents(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
