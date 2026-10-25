package ec.edu.monster.servicio;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

/**
 * Envelope JSON único de la API REST:
 *
 * <pre>{"estado":"OK|ERROR","codigo":&lt;int&gt;,"mensaje":"&lt;texto&gt;","resultado":&lt;json|null&gt;}</pre>
 *
 * <p>Se parsea tanto en respuestas exitosas como en errores HTTP: el cliente siempre debe poder
 * mostrar el {@code mensaje} que envió el servidor.</p>
 */
public class RespuestaAPI {

    public static final String ESTADO_OK = "OK";
    public static final String ESTADO_ERROR = "ERROR";

    private final String estado;
    private final int codigo;
    private final String mensaje;
    private final JsonObject resultado;

    private RespuestaAPI(String estado, int codigo, String mensaje, JsonObject resultado) {
        this.estado = estado == null ? "" : estado;
        this.codigo = codigo;
        this.mensaje = mensaje == null ? "" : mensaje;
        this.resultado = resultado;
    }

    /**
     * Parsea el cuerpo JSON de una respuesta del servidor.
     *
     * @param json cuerpo recibido
     * @return la respuesta interpretada
     * @throws jakarta.json.JsonException si el cuerpo no es un objeto JSON válido
     */
    public static RespuestaAPI parsear(String json) {
        try (JsonReader reader = Json.createReader(new StringReader(json))) {
            JsonObject objeto = reader.readObject();

            String estado = objeto.getString("estado", "");
            int codigo = objeto.getInt("codigo", 0);
            String mensaje = objeto.getString("mensaje", "");

            JsonObject resultado = null;
            JsonValue valor = objeto.get("resultado");
            if (valor != null && valor.getValueType() == JsonValue.ValueType.OBJECT) {
                resultado = valor.asJsonObject();
            }

            return new RespuestaAPI(estado, codigo, mensaje, resultado);
        }
    }

    public String getEstado() {
        return estado;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    /**
     * @return el objeto {@code resultado} del envelope, o {@code null} si el resultado no es un
     *         objeto JSON (por ejemplo {@code null} o {@code true}).
     */
    public JsonObject getResultado() {
        return resultado;
    }

    /** @return {@code true} si el envelope reporta {@code estado = OK}. */
    public boolean esExitosa() {
        return ESTADO_OK.equalsIgnoreCase(estado);
    }
}
