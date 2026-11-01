package ec.edu.monster.climov.modelo;

import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Cliente SOAP de autenticacion contra el servidor WCF WS_EurekaBank_Donet_GR8.
 */
public class WSLoginClient {

    private static final String ETIQUETA = "WSLoginClient";

    /**
     * Devuelve "1" si las credenciales son validas; en caso contrario devuelve
     * el texto de la respuesta del servidor o el mensaje de la excepcion.
     */
    public String login(String usuario, String contrasena) {
        try {
            SoapObject peticion = new SoapObject(ConfiguracionSoap.ESPACIO_NOMBRES, ConfiguracionSoap.OPERACION_LOGIN);
            // El esquema del contrato WCF es elementFormDefault="qualified":
            // los parametros deben viajar en el namespace del servicio.
            peticion.addProperty(ConfiguracionSoap.ESPACIO_NOMBRES, "usuario", usuario);
            peticion.addProperty(ConfiguracionSoap.ESPACIO_NOMBRES, "contrasena", contrasena);

            SoapSerializationEnvelope sobre = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            sobre.dotNet = false;
            sobre.setAddAdornments(false);
            sobre.setOutputSoapObject(peticion);

            HttpTransportSE transporte = new HttpTransportSE(ConfiguracionSoap.URL_SERVICIO, 60000);
            transporte.call(ConfiguracionSoap.accion(ConfiguracionSoap.OPERACION_LOGIN), sobre);

            Object respuesta = sobre.getResponse();
            return respuesta == null ? "Sin respuesta" : respuesta.toString().trim();
        } catch (Exception excepcion) {
            Log.e(ETIQUETA, "Error al invocar login", excepcion);
            return "Error: " + excepcion.getClass().getSimpleName() + " - " + excepcion.getMessage();
        }
    }
}
