package ec.edu.monster.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Regla: el sentido (ingreso/egreso) de un movimiento sale de la tabla
 * tipomovimiento, no de una heurística sobre la descripción ni sobre el saldo.
 */
class TipoMovimientoTest {

    @Test
    @DisplayName("Los codigos con accion INGRESO de tipomovimiento son ingresos")
    void codigosDeIngreso() {
        assertTrue(TipoMovimiento.esIngreso("001"));  // Apertura de Cuenta
        assertTrue(TipoMovimiento.esIngreso("003"));  // Deposito
        assertTrue(TipoMovimiento.esIngreso("005"));  // Interes
        assertTrue(TipoMovimiento.esIngreso("008"));  // Transferencia (entrante)

        assertFalse(TipoMovimiento.esEgreso("001"));
        assertFalse(TipoMovimiento.esEgreso("003"));
        assertFalse(TipoMovimiento.esEgreso("005"));
        assertFalse(TipoMovimiento.esEgreso("008"));
    }

    @Test
    @DisplayName("Los codigos con accion SALIDA de tipomovimiento son egresos")
    void codigosDeEgreso() {
        assertTrue(TipoMovimiento.esEgreso("002"));   // Cancelar Cuenta
        assertTrue(TipoMovimiento.esEgreso("004"));   // Retiro
        assertTrue(TipoMovimiento.esEgreso("006"));   // Mantenimiento
        assertTrue(TipoMovimiento.esEgreso("007"));   // ITF
        assertTrue(TipoMovimiento.esEgreso("009"));   // Transferencia (saliente)
        assertTrue(TipoMovimiento.esEgreso("010"));   // Cargo por Movimiento

        assertFalse(TipoMovimiento.esIngreso("002"));
        assertFalse(TipoMovimiento.esIngreso("004"));
        assertFalse(TipoMovimiento.esIngreso("006"));
        assertFalse(TipoMovimiento.esIngreso("007"));
        assertFalse(TipoMovimiento.esIngreso("009"));
        assertFalse(TipoMovimiento.esIngreso("010"));
    }

    @Test
    @DisplayName("Un codigo desconocido o ausente no se clasifica")
    void codigosDesconocidos() {
        assertFalse(TipoMovimiento.esIngreso("999"));
        assertFalse(TipoMovimiento.esEgreso("999"));
        assertFalse(TipoMovimiento.esIngreso(null));
        assertFalse(TipoMovimiento.esEgreso(null));
        assertFalse(TipoMovimiento.esIngreso(""));
        assertFalse(TipoMovimiento.esEgreso(""));
    }
}
