package ec.edu.monster.climov.modelo;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Cliente SOAP de movimientos (consulta y registro) contra el servidor WCF
 * WS_EurekaBank_Donet_GR8.
 */
public class WSEurekabankCliente {

    private static final String ETIQUETA = "WSEurekabankCliente";

    /**
     * Consulta los movimientos de una cuenta.
     */
    public List<Movimiento> traerMovimientos(String numeroCuenta) {
        List<Movimiento> movimientos = new ArrayList<>();

        try {
            SoapObject peticion = new SoapObject(ConfiguracionSoap.ESPACIO_NOMBRES,
                    ConfiguracionSoap.OPERACION_TRAER_MOVIMIENTOS);
            // Parametros calificados con el namespace del contrato WCF.
            peticion.addProperty(ConfiguracionSoap.ESPACIO_NOMBRES, "cuenta", numeroCuenta);

            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setAddAdornments(false);
            sobre.setOutputSoapObject(peticion);

            HttpTransportSE transporte = new HttpTransportSE(ConfiguracionSoap.URL_SERVICIO, 60000);
            transporte.call(ConfiguracionSoap.accion(ConfiguracionSoap.OPERACION_TRAER_MOVIMIENTOS), sobre);

            List<SoapObject> nodos = new ArrayList<>();
            recolectarMovimientos(sobre.getResponse(), nodos);

            for (SoapObject nodo : nodos) {
                Movimiento movimiento = new Movimiento();
                movimiento.setCuenta(texto(nodo, "cuenta"));
                movimiento.setNromov(entero(nodo, "nromov"));
                movimiento.setFecha(texto(nodo, "fecha"));
                movimiento.setTipo(texto(nodo, "tipo"));
                movimiento.setAccion(texto(nodo, "accion"));
                movimiento.setImporte(decimal(nodo, "importe"));
                movimientos.add(movimiento);
            }
        } catch (Exception excepcion) {
            Log.e(ETIQUETA, "Error al consultar movimientos", excepcion);
            return null;
        }

        return movimientos;
    }

    /**
     * Registra un movimiento. Devuelve "1" si se registro y "-1" si fallo.
     */
    public String regDeposito(String cuenta, String importe, String tipoMovimiento, String cuentaDestino) {
        try {
            SoapObject peticion = new SoapObject(ConfiguracionSoap.ESPACIO_NOMBRES,
                    ConfiguracionSoap.OPERACION_REG_DEPOSITO);
            // Parametros calificados con el namespace del contrato WCF.
            peticion.addProperty(ConfiguracionSoap.ESPACIO_NOMBRES, "cuenta", cuenta);
            peticion.addProperty(ConfiguracionSoap.ESPACIO_NOMBRES, "importe", importe);
            peticion.addProperty(ConfiguracionSoap.ESPACIO_NOMBRES, "tipoMovimiento", tipoMovimiento);
            peticion.addProperty(ConfiguracionSoap.ESPACIO_NOMBRES, "cuentaDestino",
                    cuentaDestino == null ? "" : cuentaDestino);

            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setAddAdornments(false);
            sobre.setOutputSoapObject(peticion);

            HttpTransportSE transporte = new HttpTransportSE(ConfiguracionSoap.URL_SERVICIO, 60000);
            transporte.call(ConfiguracionSoap.accion(ConfiguracionSoap.OPERACION_REG_DEPOSITO), sobre);

            Object respuesta = sobre.getResponse();
            return respuesta == null ? "-1" : respuesta.toString().trim();
        } catch (Exception excepcion) {
            Log.e(ETIQUETA, "Error al registrar el movimiento", excepcion);
            return "Error: " + excepcion.getMessage();
        }
    }

    private void recolectarMovimientos(Object respuesta, List<SoapObject> encontrados) {
        if (respuesta == null) {
            return;
        }

        if (respuesta instanceof Vector) {
            Vector<?> vector = (Vector<?>) respuesta;
            for (Object elemento : vector) {
                recolectarMovimientos(elemento, encontrados);
            }
            return;
        }

        if (!(respuesta instanceof SoapObject)) {
            return;
        }

        SoapObject objeto = (SoapObject) respuesta;
        if (objeto.hasProperty("nromov") || objeto.hasProperty("fecha")) {
            encontrados.add(objeto);
            return;
        }

        for (int i = 0; i < objeto.getPropertyCount(); i++) {
            recolectarMovimientos(objeto.getProperty(i), encontrados);
        }
    }

    private String texto(SoapObject objeto, String propiedad) {
        try {
            Object valor = objeto.getProperty(propiedad);
            return valor == null ? "" : valor.toString();
        } catch (Exception excepcion) {
            return "";
        }
    }

    private int entero(SoapObject objeto, String propiedad) {
        try {
            return Integer.parseInt(texto(objeto, propiedad).trim());
        } catch (NumberFormatException excepcion) {
            return 0;
        }
    }

    private double decimal(SoapObject objeto, String propiedad) {
        try {
            return Double.parseDouble(texto(objeto, propiedad).trim());
        } catch (NumberFormatException excepcion) {
            return 0d;
        }
    }
}
