package ec.edu.monster.seguridad;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import ec.edu.monster.api.soporte.ErrorAPI;
import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.servicio.TokenServicio;

class ValidadorTokenTest {

    private static final String MENSAJE_FALTA_BEARER =
            "Acceso denegado: falta el encabezado Authorization con el esquema Bearer.";
    private static final String MENSAJE_TOKEN_INVALIDO =
            "Acceso denegado: Token inválido o sesión expirada.";

    private final TokenServicio tokens = new TokenServicio();

    @Test
    void extraeElTokenDespuesDelEsquemaBearer() {
        assertEquals("abc123", ValidadorToken.extraerToken("Bearer abc123"));
        assertEquals("abc123", ValidadorToken.extraerToken("bearer abc123"));
        assertEquals("abc123", ValidadorToken.extraerToken("BEARER abc123"));
        assertEquals("abc123", ValidadorToken.extraerToken("  bEaReR   abc123  "));
        assertEquals("abc123.def-456", ValidadorToken.extraerToken("Bearer abc123.def-456"));
    }

    @Test
    void devuelveNullCuandoLaCabeceraNoTraeUnTokenBearer() {
        assertNull(ValidadorToken.extraerToken(null));
        assertNull(ValidadorToken.extraerToken(""));
        assertNull(ValidadorToken.extraerToken("   "));
        assertNull(ValidadorToken.extraerToken("abc123"));
        assertNull(ValidadorToken.extraerToken("Basic abc123"));
        assertNull(ValidadorToken.extraerToken("Bearer"));
        assertNull(ValidadorToken.extraerToken("Bearer   "));
        assertNull(ValidadorToken.extraerToken("Bearertoken"));
    }

    @Test
    void exigirTokenValidoRechazaLaCabeceraAusente() {
        for (String cabecera : new String[] {null, "", "   ", "Token abc"}) {
            ErrorAPI error = assertThrowsErrorAPI(() -> ValidadorToken.exigirTokenValido(cabecera, tokens));
            assertEquals(401, error.getCodigoHttp());
            assertEquals(MENSAJE_FALTA_BEARER, error.getMessage());
        }
    }

    @Test
    void exigirTokenValidoRechazaTokensDesconocidosOVencidos() {
        ErrorAPI desconocido = assertThrowsErrorAPI(
                () -> ValidadorToken.exigirTokenValido("Bearer token-inexistente", tokens));

        assertEquals(401, desconocido.getCodigoHttp());
        assertEquals(MENSAJE_TOKEN_INVALIDO, desconocido.getMessage());

        String vencido = new TokenServicio(0)
                .generarToken(new Usuario(Constantes.USUARIO, Constantes.CONTRASENA));
        ErrorAPI expirado = assertThrowsErrorAPI(
                () -> ValidadorToken.exigirTokenValido("Bearer " + vencido, tokens));

        assertEquals(401, expirado.getCodigoHttp());
        assertEquals(MENSAJE_TOKEN_INVALIDO, expirado.getMessage());
    }

    @Test
    void exigirTokenValidoAceptaUnTokenVigente() {
        String token = tokens.generarToken(new Usuario(Constantes.USUARIO, Constantes.CONTRASENA));

        assertDoesNotThrow(() -> ValidadorToken.exigirTokenValido("Bearer " + token, tokens));
        assertDoesNotThrow(() -> ValidadorToken.exigirTokenValido("bEaReR " + token, tokens));
    }

    private static ErrorAPI assertThrowsErrorAPI(Runnable accion) {
        try {
            accion.run();
        } catch (ErrorAPI error) {
            return error;
        }
        throw new AssertionError("Se esperaba ErrorAPI");
    }
}
