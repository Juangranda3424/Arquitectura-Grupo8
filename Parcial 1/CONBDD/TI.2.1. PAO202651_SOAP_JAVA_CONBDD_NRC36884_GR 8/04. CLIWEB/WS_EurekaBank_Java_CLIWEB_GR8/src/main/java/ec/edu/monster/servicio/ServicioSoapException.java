package ec.edu.monster.servicio;

/**
 * Error de comunicación con los servicios SOAP de EurekaBank.
 *
 * Los servlets muestran su mensaje tal cual, así que debe ser un texto que el
 * usuario final pueda entender sin ver la traza de JAX-WS.
 */
public class ServicioSoapException extends RuntimeException {

    public ServicioSoapException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
