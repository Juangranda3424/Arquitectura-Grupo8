package ec.edu.monster.api.soporte;

/**
 * Excepción de la capa REST que transporta el código HTTP con el que debe responder el servicio.
 *
 * <p>Los recursos la lanzan para señalar errores de contrato (401, 404, 415, ...) y
 * {@link MapeadorExcepciones} la traduce al envelope JSON de error.</p>
 */
public class ErrorAPI extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int codigoHttp;

    public ErrorAPI(int codigoHttp, String mensaje) {
        super(mensaje);
        this.codigoHttp = codigoHttp;
    }

    public int getCodigoHttp() {
        return codigoHttp;
    }
}
