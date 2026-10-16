package ec.edu.monster.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.StringReader;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import ec.edu.monster.api.soporte.MapeadorExcepciones;
import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.servicio.TokenServicio;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Utilidades compartidas por las pruebas de la capa REST.
 *
 * <p>Los recursos se invocan directamente (sin contenedor); cuando un método lanza una
 * excepción se enruta por {@link MapeadorExcepciones} exactamente como lo haría el runtime
 * JAX-RS, de modo que las pruebas verifican el par (código HTTP, envelope JSON).</p>
 */
final class SoporteAPI {

    static final String JSON = "application/json";
    static final String JSON_UTF8 = "application/json;charset=utf-8";
    static final String MENSAJE_USUARIO_INVALIDO = "Usuario invalido";
    static final String MENSAJE_FALTA_BEARER =
            "Acceso denegado: falta el encabezado Authorization con el esquema Bearer.";
    static final String MENSAJE_TOKEN_INVALIDO =
            "Acceso denegado: Token inválido o sesión expirada.";

    private SoporteAPI() {
    }

    /**
     * Invoca el recurso y, si lanza, delega el mapeo de excepciones al provider de la aplicación.
     */
    static Response invocar(Supplier<Response> llamada) {
        try {
            return llamada.get();
        } catch (Throwable error) {
            return new MapeadorExcepciones().toResponse(error);
        }
    }

    static JsonObject envelope(Response respuesta) {
        return Json.createReader(new StringReader((String) respuesta.getEntity())).readObject();
    }

    static JsonObject resultado(Response respuesta) {
        return envelope(respuesta).getJsonObject("resultado");
    }

    static void asertaEnvelope(Response respuesta, int codigo, String estado, String mensaje) {
        assertEquals(codigo, respuesta.getStatus(), "código HTTP");
        asertaTipoJSON(respuesta);
        JsonObject envelope = envelope(respuesta);
        assertEquals(4, envelope.size(), "el envelope siempre expone las cuatro claves");
        assertEquals(estado, envelope.getString("estado"), "estado");
        assertEquals(codigo, envelope.getInt("codigo"), "codigo del envelope");
        assertEquals(mensaje, envelope.getString("mensaje"), "mensaje");
    }

    static void asertaError(Response respuesta, int codigo, String mensaje) {
        asertaEnvelope(respuesta, codigo, "ERROR", mensaje);
        assertEquals(JsonValue.ValueType.NULL, envelope(respuesta).get("resultado").getValueType(),
                "resultado debe ser JSON null en los errores");
    }

    static void asertaTipoJSON(Response respuesta) {
        MediaType tipo = respuesta.getMediaType();
        assertNotNull(tipo, "Content-Type presente");
        assertEquals("application", tipo.getType());
        assertEquals("json", tipo.getSubtype());
        assertEquals("utf-8", parametro(tipo, "charset"), "charset utf-8");
        assertEquals(JSON_UTF8, respuesta.getHeaderString("Content-Type"), "cabecera Content-Type completa");
    }

    private static String parametro(MediaType tipo, String nombre) {
        return tipo.getParameters().entrySet().stream()
                .filter(entrada -> entrada.getKey().equalsIgnoreCase(nombre))
                .map(Map.Entry::getValue)
                .map(valor -> valor.toLowerCase(Locale.ROOT))
                .findFirst()
                .orElse(null);
    }

    static String tokenValido() {
        return new TokenServicio().generarToken(new Usuario(Constantes.USUARIO, Constantes.CONTRASENA));
    }

    static String tokenVencido() {
        return new TokenServicio(0).generarToken(new Usuario(Constantes.USUARIO, Constantes.CONTRASENA));
    }

    static String bearer(String token) {
        return "Bearer " + token;
    }
}
