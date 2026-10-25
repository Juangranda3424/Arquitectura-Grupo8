package ec.edu.monster.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ec.edu.monster.modelo.Usuario;

class AutenticadorServicioTest {

    private static final String MENSAJE_INVALIDO = "Usuario invalido";

    private final AutenticadorServicio autenticador = new AutenticadorServicio();

    @Test
    void usuarioNuloDevuelveUsuarioInvalido() {
        assertEquals(MENSAJE_INVALIDO, autenticador.autenticadorLogin(null));
    }

    @Test
    void usuarioSinNombreDevuelveUsuarioInvalido() {
        assertEquals(MENSAJE_INVALIDO, autenticador.autenticadorLogin(new Usuario(null, "MONSTER9")));
        assertEquals(MENSAJE_INVALIDO, autenticador.autenticadorLogin(new Usuario("", "MONSTER9")));
        assertEquals(MENSAJE_INVALIDO, autenticador.autenticadorLogin(new Usuario("   ", "MONSTER9")));
    }

    @Test
    void usuarioSinContrasenaDevuelveUsuarioInvalido() {
        assertEquals(MENSAJE_INVALIDO, autenticador.autenticadorLogin(new Usuario("MONSTER", null)));
        assertEquals(MENSAJE_INVALIDO, autenticador.autenticadorLogin(new Usuario("MONSTER", "")));
    }

    @Test
    void credencialesIncorrectasDevuelvenUsuarioInvalido() {
        assertEquals(MENSAJE_INVALIDO, autenticador.autenticadorLogin(new Usuario("MONSTER", "otra")));
        assertEquals(MENSAJE_INVALIDO, autenticador.autenticadorLogin(new Usuario("OTRO", "MONSTER9")));
        assertEquals(MENSAJE_INVALIDO, autenticador.autenticadorLogin(new Usuario("monster", "MONSTER9")));
        assertEquals(MENSAJE_INVALIDO, autenticador.autenticadorLogin(new Usuario("MONSTER", "monster9")));
    }

    @Test
    void credencialesValidasDevuelvenUnTokenConElUsuario() {
        String token = autenticador.autenticadorLogin(new Usuario("MONSTER", "MONSTER9"));

        assertNotEquals(MENSAJE_INVALIDO, token);
        assertTrue(token.endsWith("MONSTER"));
    }

    @Test
    void dosLoginsGeneranTokensDistintos() {
        String primero = autenticador.autenticadorLogin(new Usuario("MONSTER", "MONSTER9"));
        String segundo = autenticador.autenticadorLogin(new Usuario("MONSTER", "MONSTER9"));

        assertNotEquals(primero, segundo);
    }

    @Test
    void logoutDeUnTokenValidoDevuelveTrue() {
        String token = autenticador.autenticadorLogin(new Usuario("MONSTER", "MONSTER9"));

        assertTrue(autenticador.autenticadorLogout(token));
    }

    @Test
    void logoutRepetidoDevuelveFalse() {
        String token = autenticador.autenticadorLogin(new Usuario("MONSTER", "MONSTER9"));

        assertTrue(autenticador.autenticadorLogout(token));
        assertFalse(autenticador.autenticadorLogout(token));
    }

    @Test
    void logoutDeTokensNulosVaciosODesconocidosDevuelveFalseSinExcepcion() {
        assertFalse(autenticador.autenticadorLogout(null));
        assertFalse(autenticador.autenticadorLogout(""));
        assertFalse(autenticador.autenticadorLogout("   "));
        assertFalse(autenticador.autenticadorLogout("token-inexistente"));
    }
}
