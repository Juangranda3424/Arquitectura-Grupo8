package ec.edu.monster.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Resumen de la pantalla de movimientos: saldo actual de la cuenta, totales de
 * ingresos y egresos del historial y saldo neto del período consultado.
 */
public class ResumenMovimientos {

    private final double saldoActual;
    private final double totalIngresos;
    private final double totalEgresos;
    private final List<MovimientoModel> movimientos;

    private ResumenMovimientos(double saldoActual, List<MovimientoModel> movimientos) {
        this.saldoActual = saldoActual;
        this.movimientos = Collections.unmodifiableList(new ArrayList<>(movimientos));

        double ingresos = 0;
        double egresos = 0;
        for (MovimientoModel movimiento : this.movimientos) {
            if (movimiento == null) {
                continue;
            }
            if (movimiento.isIngreso()) {
                ingresos += movimiento.getImporteAbsoluto();
            } else {
                egresos += movimiento.getImporteAbsoluto();
            }
        }
        this.totalIngresos = ingresos;
        this.totalEgresos = egresos;
    }

    public static ResumenMovimientos calcular(double saldoActual, List<MovimientoModel> movimientos) {
        return new ResumenMovimientos(saldoActual, movimientos == null ? Collections.emptyList() : movimientos);
    }

    public double getSaldoActual() {
        return saldoActual;
    }

    public double getTotalIngresos() {
        return totalIngresos;
    }

    public double getTotalEgresos() {
        return totalEgresos;
    }

    public double getSaldoNeto() {
        return totalIngresos - totalEgresos;
    }

    public List<MovimientoModel> getMovimientos() {
        return movimientos;
    }

    public boolean isVacio() {
        return movimientos.isEmpty();
    }
}
