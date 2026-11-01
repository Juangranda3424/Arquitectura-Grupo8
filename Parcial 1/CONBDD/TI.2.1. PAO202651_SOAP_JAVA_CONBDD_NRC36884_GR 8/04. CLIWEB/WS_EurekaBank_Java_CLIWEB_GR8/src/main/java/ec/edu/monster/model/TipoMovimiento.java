package ec.edu.monster.model;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Sentido de un movimiento según la tabla `tipomovimiento` del servidor
 * (columna vch_tipoaccion: INGRESO / SALIDA).
 *
 * El servicio SOAP entrega el importe siempre en positivo y no expone la acción,
 * por eso el cliente web la deriva del código de tipo de movimiento, igual que lo
 * hace el servidor para calcular el saldo acumulado.
 */
public final class TipoMovimiento {

    /** 001 Apertura de Cuenta, 003 Deposito, 005 Interes, 008 Transferencia (entrante). */
    private static final Set<String> INGRESOS =
            Collections.unmodifiableSet(new LinkedHashSet<>(Set.of("001", "003", "005", "008")));

    /** 002 Cancelar Cuenta, 004 Retiro, 006 Mantenimiento, 007 ITF, 009 Transferencia (saliente), 010 Cargo. */
    private static final Set<String> EGRESOS =
            Collections.unmodifiableSet(new LinkedHashSet<>(Set.of("002", "004", "006", "007", "009", "010")));

    private TipoMovimiento() {
    }

    public static boolean esIngreso(String codigoTipoMovimiento) {
        return codigoTipoMovimiento != null && INGRESOS.contains(codigoTipoMovimiento.trim());
    }

    public static boolean esEgreso(String codigoTipoMovimiento) {
        return codigoTipoMovimiento != null && EGRESOS.contains(codigoTipoMovimiento.trim());
    }

    /**
     * Aplica el signo del movimiento al importe que entrega el servicio SOAP.
     * Un código desconocido conserva el importe tal como llegó.
     */
    public static double conSigno(String codigoTipoMovimiento, double importe) {
        if (esEgreso(codigoTipoMovimiento)) {
            return -Math.abs(importe);
        }
        if (esIngreso(codigoTipoMovimiento)) {
            return Math.abs(importe);
        }
        return importe;
    }
}
