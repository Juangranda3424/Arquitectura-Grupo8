package ec.edu.monster.servicio;

import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.model.MovimientoModel;
import ec.edu.monster.model.TipoMovimiento;
import ec.edu.monster.ws.WSLogin;
import ec.edu.monster.ws.WSLogin_Service;
import ec.edu.monster.ws.WSCuenta;
import ec.edu.monster.ws.WSCuenta_Service;
import ec.edu.monster.ws.WSMovimiento;
import ec.edu.monster.ws.WSMovimiento_Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Puente entre el cliente web y los servicios SOAP del servidor TI.2.1
 * (04. SERVIDOR/WS_EurekaBank_Java_GR8).
 *
 * Los stubs JAX-WS viven en {@code ec.edu.monster.ws} y ya traen la ubicación del
 * WSDL del servidor; aquí solo se usan los ports y se adaptan las respuestas a los
 * modelos que consumen las JSP.
 *
 * Los ports se crean una sola vez (la creación implica leer el WSDL por HTTP) y se
 * reutilizan en todas las peticiones: los proxies de JAX-WS son seguros entre hilos.
 */
public class EurekaBankSoapClient {

    /** URL base del servidor SOAP; coincide con el wsdlLocation de los stubs de ws/. */
    public static final String URL_SERVIDOR = "http://localhost:8080/WS_EurekaBank_Java_GR8";

    /** El esquema del servidor no expone el valor de la fecha de creación. */
    private static final String FECHA_NO_DISPONIBLE = "No disponible";

    private static volatile WSLogin puertoLogin;
    private static volatile WSCuenta puertoCuenta;
    private static volatile WSMovimiento puertoMovimiento;

    /**
     * Operación auth(username, password) de WSLogin.
     */
    public boolean autenticar(String usuario, String clave) {
        try {
            return puertoLogin().auth(usuario, clave);
        } catch (RuntimeException ex) {
            throw noDisponible(ex);
        }
    }

    /**
     * Operación obtenerCuentaPorNumero(cuenta) de WSCuenta.
     *
     * @return los datos de la cuenta, o {@code null} si el servidor no la encuentra
     */
    public CuentaModel obtenerCuenta(String numeroCuenta) {
        try {
            ec.edu.monster.ws.CuentaModel cuenta = puertoCuenta().obtenerCuentaPorNumero(numeroCuenta);
            return cuenta == null ? null : aModelo(cuenta);
        } catch (RuntimeException ex) {
            throw noDisponible(ex);
        }
    }

    /**
     * Operación cuenta(cuenta, monto, tipo, cd) de WSCuenta.
     *
     * @param tipo DEP para depósito, RET para retiro, TRA para transferencia
     * @param cuentaDestino solo se usa en transferencias (parámetro cd)
     */
    public boolean registrarOperacion(String numeroCuenta, String monto, String tipo, String cuentaDestino) {
        try {
            return puertoCuenta().cuenta(numeroCuenta, monto, tipo, cuentaDestino);
        } catch (RuntimeException ex) {
            throw noDisponible(ex);
        }
    }

    /**
     * Operación consultarMovimientos(numcuenta) de WSMovimiento.
     */
    public List<MovimientoModel> consultarMovimientos(String numeroCuenta) {
        try {
            List<ec.edu.monster.ws.MovimientoModel> movimientos =
                    puertoMovimiento().consultarMovimientos(numeroCuenta);

            List<MovimientoModel> modelo = new ArrayList<>();
            if (movimientos != null) {
                for (ec.edu.monster.ws.MovimientoModel movimiento : movimientos) {
                    if (movimiento != null) {
                        modelo.add(aModelo(movimiento));
                    }
                }
            }
            return modelo;
        } catch (RuntimeException ex) {
            throw noDisponible(ex);
        }
    }

    private static CuentaModel aModelo(ec.edu.monster.ws.CuentaModel cuenta) {
        return new CuentaModel(
                cuenta.getChrCuenCodigo(),
                cuenta.getChrMoneCodigo(),
                cuenta.getChrSucucodigo(),
                cuenta.getChrClieCodigo(),
                cuenta.getDecCuenSaldo(),
                cuenta.getIntCuenContMov(),
                cuenta.getVchCuenEstado(),
                FECHA_NO_DISPONIBLE);
    }

    private static MovimientoModel aModelo(ec.edu.monster.ws.MovimientoModel movimiento) {
        String codigo = movimiento.getCodigoTipoMovimiento();
        return new MovimientoModel(
                movimiento.getNumeroMovimiento(),
                movimiento.getFechaMovimiento(),
                codigo,
                movimiento.getTipoDescripcion(),
                TipoMovimiento.conSigno(codigo, movimiento.getImporteMovimiento()),
                movimiento.getSaldo(),
                movimiento.getCuentaReferencia());
    }

    private static ServicioSoapException noDisponible(RuntimeException causa) {
        return new ServicioSoapException(
                "No se pudo conectar con los servicios SOAP de EurekaBank en " + URL_SERVIDOR
                        + ". Verifique que el servidor esté en ejecución.", causa);
    }

    private static WSLogin puertoLogin() {
        WSLogin puerto = puertoLogin;
        if (puerto == null) {
            synchronized (EurekaBankSoapClient.class) {
                puerto = puertoLogin;
                if (puerto == null) {
                    puerto = new WSLogin_Service().getWSLoginPort();
                    puertoLogin = puerto;
                }
            }
        }
        return puerto;
    }

    private static WSCuenta puertoCuenta() {
        WSCuenta puerto = puertoCuenta;
        if (puerto == null) {
            synchronized (EurekaBankSoapClient.class) {
                puerto = puertoCuenta;
                if (puerto == null) {
                    puerto = new WSCuenta_Service().getWSCuentaPort();
                    puertoCuenta = puerto;
                }
            }
        }
        return puerto;
    }

    private static WSMovimiento puertoMovimiento() {
        WSMovimiento puerto = puertoMovimiento;
        if (puerto == null) {
            synchronized (EurekaBankSoapClient.class) {
                puerto = puertoMovimiento;
                if (puerto == null) {
                    puerto = new WSMovimiento_Service().getWSMovimientoPort();
                    puertoMovimiento = puerto;
                }
            }
        }
        return puerto;
    }
}
