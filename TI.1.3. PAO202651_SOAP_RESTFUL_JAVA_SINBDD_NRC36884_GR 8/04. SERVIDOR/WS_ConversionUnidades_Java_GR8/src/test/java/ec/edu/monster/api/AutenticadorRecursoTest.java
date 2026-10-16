package ec.edu.monster.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ec.edu.monster.api.recurso.AutenticadorRecurso;

import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.ws.rs.core.Response;

class AutenticadorRecursoTest {

    private static final String CUERPO_VALIDO = "{\"username\":\"MONSTER\",\"password\":\"MONSTER9\"}";
    private static final String MENSAJE_JSON_INVALIDO = "El cuerpo de la petición debe ser JSON válido.";
    private static final String MENSAJE_CAMPOS_OBLIGATORIOS =
            "El nombre de usuario y la contraseña son obligatorios.";

    private final AutenticadorRecurso recurso = new AutenticadorRecurso();

    private Response login(String cuerpo, String tipoContenido) {
        return SoporteAPI.invocar(() -> recurso.iniciarSesion(cuerpo, tipoContenido));
    }

    private Response logout(String autorizacion) {
        return SoporteAPI.invocar(() -> recurso.cerrarSesion(autorizacion));
    }

    private String tokenDeLoginValido() {
        Response respuesta = login(CUERPO_VALIDO, SoporteAPI.JSON);
        assertEquals(200, respuesta.getStatus());
        return SoporteAPI.resultado(respuesta).getString("token");
    }

    @Test
    void loginValidoDevuelve200ConTokenUsuarioYVencimiento() {
        Response respuesta = login(CUERPO_VALIDO, SoporteAPI.JSON);

        SoporteAPI.asertaEnvelope(respuesta, 200, "OK", "Autenticación exitosa.");

        JsonObject resultado = SoporteAPI.resultado(respuesta);
        assertEquals(3, resultado.size());
        String token = resultado.getString("token");
        assertFalse(token.isBlank());
        assertTrue(token.endsWith("MONSTER"), "el token conserva el formato UUID+usuario: " + token);
        assertEquals("MONSTER", resultado.getString("usuario"));
        assertTrue(Instant.parse(resultado.getString("expiraEn")).isAfter(Instant.now()));
    }

    @Test
    void loginConContentTypeDistintoDeJsonDevuelve415() {
        SoporteAPI.asertaError(login(CUERPO_VALIDO, "text/plain"),
                415, "El contenido de la petición debe ser JSON.");
        SoporteAPI.asertaError(login(CUERPO_VALIDO, "application/xml"),
                415, "El contenido de la petición debe ser JSON.");
    }

    @Test
    void loginConCharsetEnElContentTypeEsAceptado() {
        Response respuesta = login(CUERPO_VALIDO, "application/json; charset=UTF-8");

        SoporteAPI.asertaEnvelope(respuesta, 200, "OK", "Autenticación exitosa.");
    }

    @Test
    void loginConJSONInvalidoDevuelve400() {
        SoporteAPI.asertaError(login("{no-es-json", SoporteAPI.JSON), 400, MENSAJE_JSON_INVALIDO);
        SoporteAPI.asertaError(login("", SoporteAPI.JSON), 400, MENSAJE_JSON_INVALIDO);
        SoporteAPI.asertaError(login(null, SoporteAPI.JSON), 400, MENSAJE_JSON_INVALIDO);
        SoporteAPI.asertaError(login("[1,2,3]", SoporteAPI.JSON), 400, MENSAJE_JSON_INVALIDO);
    }

    @Test
    void loginConCamposFaltantesOVaciosDevuelve400() {
        SoporteAPI.asertaError(login("{}", SoporteAPI.JSON), 400, MENSAJE_CAMPOS_OBLIGATORIOS);
        SoporteAPI.asertaError(login("{\"username\":\"\",\"password\":\"\"}", SoporteAPI.JSON),
                400, MENSAJE_CAMPOS_OBLIGATORIOS);
        SoporteAPI.asertaError(login("{\"username\":\"MONSTER\"}", SoporteAPI.JSON),
                400, MENSAJE_CAMPOS_OBLIGATORIOS);
        SoporteAPI.asertaError(login("{\"password\":\"MONSTER9\"}", SoporteAPI.JSON),
                400, MENSAJE_CAMPOS_OBLIGATORIOS);
        SoporteAPI.asertaError(login("{\"username\":\"   \",\"password\":\"MONSTER9\"}", SoporteAPI.JSON),
                400, MENSAJE_CAMPOS_OBLIGATORIOS);
        SoporteAPI.asertaError(login("{\"username\":\"MONSTER\",\"password\":\"   \"}", SoporteAPI.JSON),
                400, MENSAJE_CAMPOS_OBLIGATORIOS);
    }

    @Test
    void loginConCredencialesIncorrectasDevuelve401ConElMensajeDeParidad() {
        SoporteAPI.asertaError(login("{\"username\":\"MONSTER\",\"password\":\"otra\"}", SoporteAPI.JSON),
                401, SoporteAPI.MENSAJE_USUARIO_INVALIDO);
        SoporteAPI.asertaError(login("{\"username\":\"OTRO\",\"password\":\"MONSTER9\"}", SoporteAPI.JSON),
                401, SoporteAPI.MENSAJE_USUARIO_INVALIDO);
        SoporteAPI.asertaError(login("{\"username\":\"monster\",\"password\":\"monster9\"}", SoporteAPI.JSON),
                401, SoporteAPI.MENSAJE_USUARIO_INVALIDO);
    }

    @Test
    void logoutSinCabeceraAuthorizationDevuelve401() {
        SoporteAPI.asertaError(logout(null), 401, SoporteAPI.MENSAJE_FALTA_BEARER);
        SoporteAPI.asertaError(logout(""), 401, SoporteAPI.MENSAJE_FALTA_BEARER);
        SoporteAPI.asertaError(logout("   "), 401, SoporteAPI.MENSAJE_FALTA_BEARER);
        SoporteAPI.asertaError(logout("Token abc"), 401, SoporteAPI.MENSAJE_FALTA_BEARER);
    }

    @Test
    void logoutConTokenDesconocidoDevuelve401() {
        SoporteAPI.asertaError(logout("Bearer token-inexistente"), 401, SoporteAPI.MENSAJE_TOKEN_INVALIDO);
    }

    @Test
    void logoutConTokenVencidoDevuelve401() {
        SoporteAPI.asertaError(logout(SoporteAPI.bearer(SoporteAPI.tokenVencido())),
                401, SoporteAPI.MENSAJE_TOKEN_INVALIDO);
    }

    @Test
    @DisplayName("logout tras login responde 200 con resultado true")
    void logoutTrasLoginDevuelve200ConResultadoTrue() {
        String token = tokenDeLoginValido();

        Response respuesta = logout(SoporteAPI.bearer(token));

        SoporteAPI.asertaEnvelope(respuesta, 200, "OK", "Sesión finalizada.");
        assertEquals(JsonValue.ValueType.TRUE, SoporteAPI.envelope(respuesta).get("resultado").getValueType());
    }

    @Test
    void elTokenCanjeadoQuedaInvalidadoTrasElLogout() {
        String token = tokenDeLoginValido();

        assertEquals(200, logout(SoporteAPI.bearer(token)).getStatus());
        SoporteAPI.asertaError(logout(SoporteAPI.bearer(token)), 401, SoporteAPI.MENSAJE_TOKEN_INVALIDO);
    }

    @Test
    void elEsquemaBearerNoDistingueMayusculas() {
        String token = tokenDeLoginValido();

        Response respuesta = logout("bEaReR " + token);

        SoporteAPI.asertaEnvelope(respuesta, 200, "OK", "Sesión finalizada.");
        assertDoesNotThrow(() -> SoporteAPI.envelope(respuesta).get("resultado"));
    }
}
