package ec.edu.monster.api.soporte;

import java.util.Map;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonValue;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Constructor del envelope JSON único de la API REST:
 *
 * <pre>{"estado":"OK|ERROR","codigo":&lt;int&gt;,"mensaje":"&lt;texto&gt;","resultado":&lt;json|null&gt;}</pre>
 *
 * <p>Las cuatro claves están siempre presentes; en los errores {@code resultado} es el
 * {@code null} JSON ({@link JsonValue#NULL}) y no la ausencia de la clave. El serializado usa
 * exclusivamente {@code jakarta.json} (JSON-P).</p>
 */
public final class RespuestaAPI {

    public static final String ESTADO_OK = "OK";
    public static final String ESTADO_ERROR = "ERROR";

    /** Tipo de contenido de todas las respuestas del servicio. */
    public static final MediaType TIPO_JSON = new MediaType("application", "json", Map.of("charset", "utf-8"));

    private RespuestaAPI() {
    }

    /**
     * Envelope de éxito con un resultado JSON arbitrario (objeto, arreglo, número o booleano).
     */
    public static String ok(int codigo, String mensaje, JsonValue resultado) {
        return construir(ESTADO_OK, codigo, mensaje, resultado);
    }

    /**
     * Envelope de éxito con un objeto JSON como resultado.
     */
    public static String ok(int codigo, String mensaje, JsonObject resultado) {
        return ok(codigo, mensaje, (JsonValue) resultado);
    }

    /**
     * Envelope de error: {@code resultado} siempre es el {@code null} JSON.
     */
    public static String error(int codigo, String mensaje) {
        return construir(ESTADO_ERROR, codigo, mensaje, JsonValue.NULL);
    }

    /**
     * Construye la respuesta JAX-RS completa (código HTTP + envelope JSON ya serializado).
     *
     * @param codigoHttp código de estado HTTP; coincide con el {@code codigo} del envelope
     * @param json envelope producido por {@link #ok} o {@link #error}
     */
    public static Response respuesta(int codigoHttp, String json) {
        return Response.status(codigoHttp)
                .entity(json)
                .type(TIPO_JSON)
                .build();
    }

    private static String construir(String estado, int codigo, String mensaje, JsonValue resultado) {
        JsonObjectBuilder envelope = Json.createObjectBuilder()
                .add("estado", estado)
                .add("codigo", codigo)
                .add("mensaje", mensaje == null ? "" : mensaje);

        if (resultado == null || resultado.getValueType() == JsonValue.ValueType.NULL) {
            envelope.addNull("resultado");
        } else {
            envelope.add("resultado", resultado);
        }

        return envelope.build().toString();
    }
}
