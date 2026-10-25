package ec.edu.monster.api.soporte;

import jakarta.json.JsonException;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Traduce cualquier excepción de la capa REST al envelope JSON de error con el código HTTP
 * correspondiente, de modo que el cliente nunca reciba un cuerpo distinto del contrato.
 *
 * <p>Mapeos: {@link ErrorAPI} conserva su código; {@link IllegalArgumentException} es 400 con el
 * mensaje de validación (paridad con TI.1.1); 404/405/415 para los errores del runtime JAX-RS y
 * 500 genérico para cualquier otra causa (sin filtrar detalles internos).</p>
 */
@Provider
public class MapeadorExcepciones implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable error) {
        if (error instanceof ErrorAPI excepcion) {
            return error(excepcion.getCodigoHttp(), excepcion.getMessage());
        }
        if (error instanceof IllegalArgumentException excepcion) {
            return error(400, mensajeDe(excepcion));
        }
        if (error instanceof NotFoundException) {
            return error(404, "Recurso no encontrado.");
        }
        if (error instanceof NotAllowedException) {
            return error(405, "Método HTTP no permitido para este recurso.");
        }
        if (error instanceof NotSupportedException || error instanceof JsonException) {
            return error(415, SoportePeticion.MENSAJE_CONTENIDO_NO_JSON);
        }
        return error(500, "Error interno del servidor.");
    }

    private static String mensajeDe(IllegalArgumentException error) {
        String mensaje = error.getMessage();
        return mensaje == null || mensaje.isBlank() ? "Solicitud inválida." : mensaje;
    }

    private static Response error(int codigoHttp, String mensaje) {
        return RespuestaAPI.respuesta(codigoHttp, RespuestaAPI.error(codigoHttp, mensaje));
    }
}
