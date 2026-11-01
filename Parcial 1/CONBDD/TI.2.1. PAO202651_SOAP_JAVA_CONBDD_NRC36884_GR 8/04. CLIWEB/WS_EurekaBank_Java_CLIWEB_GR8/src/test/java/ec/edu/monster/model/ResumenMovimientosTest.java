package ec.edu.monster.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regla: el resumen de la pantalla de movimientos suma los importes con su signo
 * (ingreso positivo, egreso negativo) y el saldo neto es ingresos - egresos.
 */
class ResumenMovimientosTest {

    private static MovimientoModel movimiento(int numero, String codigo, double importeConSigno) {
        return new MovimientoModel(numero, "2024-06-09", codigo, "Descripcion", importeConSigno, 0);
    }

    @Test
    @DisplayName("Suma ingresos y egresos y calcula el saldo neto")
    void sumaIngresosYEgresos() {
        List<MovimientoModel> movimientos = Arrays.asList(
                movimiento(1, "001", 2800.00),
                movimiento(2, "003", 3200.00),
                movimiento(3, "004", -800.00));

        ResumenMovimientos resumen = ResumenMovimientos.calcular(5400.00, movimientos);

        assertEquals(6000.00, resumen.getTotalIngresos(), 0.001);
        assertEquals(800.00, resumen.getTotalEgresos(), 0.001);
        assertEquals(5200.00, resumen.getSaldoNeto(), 0.001);
        assertEquals(5400.00, resumen.getSaldoActual(), 0.001);
        assertEquals(3, resumen.getMovimientos().size());
    }

    @Test
    @DisplayName("Sin movimientos los totales quedan en cero y el saldo se conserva")
    void sinMovimientos() {
        ResumenMovimientos resumen = ResumenMovimientos.calcular(100.50, Collections.emptyList());

        assertTrue(resumen.getMovimientos().isEmpty());
        assertEquals(0.00, resumen.getTotalIngresos(), 0.001);
        assertEquals(0.00, resumen.getTotalEgresos(), 0.001);
        assertEquals(0.00, resumen.getSaldoNeto(), 0.001);
        assertEquals(100.50, resumen.getSaldoActual(), 0.001);
    }

    @Test
    @DisplayName("El importe absoluto sirve para pintar el monto sin signo duplicado")
    void importeAbsoluto() {
        assertEquals(800.00, movimiento(3, "004", -800.00).getImporteAbsoluto(), 0.001);
        assertEquals(2800.00, movimiento(1, "001", 2800.00).getImporteAbsoluto(), 0.001);
        assertTrue(movimiento(3, "004", -800.00).isEgreso());
        assertTrue(movimiento(1, "001", 2800.00).isIngreso());
    }
}
