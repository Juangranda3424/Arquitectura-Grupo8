package ec.edu.monster.api.soporte;

import java.io.StringReader;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

/**
 * Utilidades compartidas por los recursos para leer la petición HTTP: validación del
 * {@code Content-Type} y parseo defensivo del cuerpo JSON con {@code jakarta.json} (JSON-P).
 */
public final class SoportePeticion {

    public static final String MENSAJE_CONTENIDO_NO_JSON = "El contenido de la petición debe ser JSON.";
    public static final String MENSAJE_CUERPO_NO_JSON = "El cuerpo de la petición debe ser JSON válido.";

    private static final String TIPO_JSON = "application/json";

    private SoportePeticion() {
    }

    /**
     * Exige que el {@code Content-Type} sea {@code application/json}, ignorando parámetros
     * (por ejemplo {@code charset}) y mayúsculas. Una cabecera ausente o vacía se acepta.
     *
     * @throws ErrorAPI 415 cuando el tipo declarado no es JSON
     */
    public static void exigirTipoJSON(String tipoContenido) {
        if (tipoContenido == null || tipoContenido.isBlank()) {
            return;
        }
        String tipo = tipoContenido.split(";", 2)[0].trim();
        if (!TIPO_JSON.equalsIgnoreCase(tipo)) {
            throw new ErrorAPI(415, MENSAJE_CONTENIDO_NO_JSON);
        }
    }

    /**
     * Parsea el cuerpo como objeto JSON.
     *
     * @throws ErrorAPI 400 cuando el cuerpo está vacío, no es JSON o no es un objeto JSON
     */
    public static JsonObject leerObjetoJSON(String cuerpo) {
        if (cuerpo == null || cuerpo.isBlank()) {
            throw new ErrorAPI(400, MENSAJE_CUERPO_NO_JSON);
        }
        try (JsonReader lector = Json.createReader(new StringReader(cuerpo))) {
            return lector.readObject();
        } catch (JsonException error) {
            throw new ErrorAPI(400, MENSAJE_CUERPO_NO_JSON);
        }
    }

    /**
     * @return el texto de la clave, o {@code null} si falta o no es una cadena JSON
     */
    public static String texto(JsonObject objeto, String clave) {
        JsonValue valor = objeto.get(clave);
        return valor != null && valor.getValueType() == JsonValue.ValueType.STRING ? objeto.getString(clave) : null;
    }

    /**
     * @return el número de la clave, o {@code null} si falta o no es un número JSON
     */
    public static Double numero(JsonObject objeto, String clave) {
        JsonValue valor = objeto.get(clave);
        return valor != null && valor.getValueType() == JsonValue.ValueType.NUMBER
                ? objeto.getJsonNumber(clave).doubleValue()
                : null;
    }
}
