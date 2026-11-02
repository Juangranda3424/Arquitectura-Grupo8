package ec.edu.espe.banquito.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

import ec.edu.espe.banquito.service.AmortizacionCalculator.CuotaCalculada;
import ec.edu.espe.banquito.service.AmortizacionCalculator.PlanPagos;

class AmortizacionCalculatorTest {

    private final AmortizacionCalculator calculator = new AmortizacionCalculator();

    @Test
    void reproducesTheExamSampleTableExactly() {
        PlanPagos plan = calculator.calcular(new BigDecimal("450.00"), 6, new BigDecimal("16.50"));

        assertThat(plan.valorCuota()).isEqualByComparingTo("78.65");
        assertThat(plan.cuotas()).extracting(CuotaCalculada::numCuota).containsExactly(1, 2, 3, 4, 5, 6);
        assertRow(plan.cuotas().get(0), "78.65", "6.19", "72.46", "377.54");
        assertRow(plan.cuotas().get(1), "78.65", "5.19", "73.46", "304.08");
        assertRow(plan.cuotas().get(2), "78.65", "4.18", "74.47", "229.61");
        assertRow(plan.cuotas().get(3), "78.65", "3.16", "75.49", "154.12");
        assertRow(plan.cuotas().get(4), "78.65", "2.12", "76.53", "77.59");
        assertRow(plan.cuotas().get(5), "78.65", "1.07", "77.58", "0.00");
    }

    @Test
    void matchesExcelPagoForEighteenMonths() {
        // =PAGO(16.5%/12; 18; -1000) = 63.093009... -> 63.09
        PlanPagos plan = calculator.calcular(new BigDecimal("1000.00"), 18, new BigDecimal("16.50"));

        assertThat(plan.valorCuota()).isEqualByComparingTo("63.09");
        assertThat(plan.cuotas()).hasSize(18);
        assertThat(plan.cuotas().get(17).saldo()).isEqualByComparingTo("0.00");
    }

    @Test
    void everyValueHasTwoDecimals() {
        List<CuotaCalculada> cuotas = calculator.calcular(new BigDecimal("999.00"), 3, new BigDecimal("16.50")).cuotas();

        assertThat(cuotas).allSatisfy(c -> {
            assertThat(c.valorCuota().scale()).isEqualTo(2);
            assertThat(c.interesPagado().scale()).isEqualTo(2);
            assertThat(c.capitalPagado().scale()).isEqualTo(2);
            assertThat(c.saldo().scale()).isEqualTo(2);
        });
    }

    private static void assertRow(CuotaCalculada row, String cuota, String interes, String capital, String saldo) {
        assertThat(row.valorCuota()).isEqualByComparingTo(cuota);
        assertThat(row.interesPagado()).isEqualByComparingTo(interes);
        assertThat(row.capitalPagado()).isEqualByComparingTo(capital);
        assertThat(row.saldo()).isEqualByComparingTo(saldo);
    }
}
