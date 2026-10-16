package ec.edu.monster.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.Usuario;

class TokenServicioTest {

    private static final Instant BASE = Instant.parse("2026-01-01T00:00:00Z");
    private static final Usuario MONSTER = new Usuario("MONSTER", "MONSTER9");

    @Test
    void elTtlPorDefectoEsDeTreintaMinutos() {
        assertEquals(30L, Constantes.TOKEN_TTL_MINUTOS);
    }

    @Test
    void elTokenConservaElFormatoUuidMasUsuario() {
        TokenServicio servicio = new TokenServicio(30, new RelojAjustable(BASE));

        String token = servicio.generarToken(MONSTER);

        assertTrue(token.endsWith("MONSTER"), "El token debe terminar con el usuario: " + token);
        assertTrue(token.matches("[0-9a-fA-F-]{36}MONSTER"), "Formato UUID+usuario inesperado: " + token);
    }

    @Test
    void unTokenRecienGeneradoEsValido() {
        TokenServicio servicio = new TokenServicio(30, new RelojAjustable(BASE));

        String token = servicio.generarToken(MONSTER);

        assertTrue(servicio.validar(token));
        assertNotNull(servicio.vencimientoDe(token));
    }

    @Test
    void elVencimientoEsElInstanteDeGeneracionMasElTtl() {
        RelojAjustable reloj = new RelojAjustable(BASE);
        TokenServicio servicio = new TokenServicio(30, reloj);

        String token = servicio.generarToken(MONSTER);

        assertEquals(BASE.plus(30, ChronoUnit.MINUTES), servicio.vencimientoDe(token));
    }

    @Test
    void elTokenSigueSiendoValidoAntesDelVencimiento() {
        RelojAjustable reloj = new RelojAjustable(BASE);
        TokenServicio servicio = new TokenServicio(30, reloj);
        String token = servicio.generarToken(MONSTER);

        reloj.avanzar(Duration.ofMinutes(29));

        assertTrue(servicio.validar(token));
    }

    @Test
    void elTokenExpiraAlCumplirElTtl() {
        RelojAjustable reloj = new RelojAjustable(BASE);
        TokenServicio servicio = new TokenServicio(30, reloj);
        String token = servicio.generarToken(MONSTER);

        reloj.avanzar(Duration.ofMinutes(30));

        assertFalse(servicio.validar(token));
    }

    @Test
    void unTokenExpiradoSeEliminaAlValidarlo() {
        RelojAjustable reloj = new RelojAjustable(BASE);
        TokenServicio servicio = new TokenServicio(30, reloj);
        String token = servicio.generarToken(MONSTER);
        reloj.avanzar(Duration.ofMinutes(31));

        assertFalse(servicio.validar(token));
        assertNull(servicio.vencimientoDe(token), "El token expirado debe quedar removido del almacén");
        assertFalse(servicio.invalidar(token));
    }

    @Test
    void unTtlCeroExpiraDeInmediato() {
        TokenServicio servicio = new TokenServicio(0, new RelojAjustable(BASE));

        String token = servicio.generarToken(MONSTER);

        assertFalse(servicio.validar(token));
    }

    @Test
    void unTtlNegativoExpiraDeInmediato() {
        TokenServicio servicio = new TokenServicio(-5, new RelojAjustable(BASE));

        String token = servicio.generarToken(MONSTER);

        assertFalse(servicio.validar(token));
    }

    @Test
    void validarRechazaTokensNulosVaciosODesconocidos() {
        TokenServicio servicio = new TokenServicio(30, new RelojAjustable(BASE));

        assertFalse(servicio.validar(null));
        assertFalse(servicio.validar(""));
        assertFalse(servicio.validar("   "));
        assertFalse(servicio.validar("token-inexistente"));
    }

    @Test
    void invalidarSacaElTokenYDevuelveTrue() {
        TokenServicio servicio = new TokenServicio(30, new RelojAjustable(BASE));
        String token = servicio.generarToken(MONSTER);

        assertTrue(servicio.invalidar(token));
        assertFalse(servicio.validar(token));
        assertNull(servicio.vencimientoDe(token));
    }

    @Test
    void invalidarRechazaTokensNulosOVacios() {
        TokenServicio servicio = new TokenServicio(30, new RelojAjustable(BASE));

        assertFalse(servicio.invalidar(null));
        assertFalse(servicio.invalidar("  "));
        assertFalse(servicio.invalidar("token-inexistente"));
    }

    @Test
    void vencimientoDeUnTokenDesconocidoEsNulo() {
        TokenServicio servicio = new TokenServicio(30, new RelojAjustable(BASE));

        assertNull(servicio.vencimientoDe("token-inexistente"));
        assertNull(servicio.vencimientoDe(null));
    }

    @Test
    void elRegistroDeTokensEsCompartidoEntreInstancias() {
        // Paridad con TI.1.1: el login y la validación por petición usan el mismo registro global.
        TokenServicio emisor = new TokenServicio(30, new RelojAjustable(BASE));
        TokenServicio validador = new TokenServicio(30, new RelojAjustable(BASE));

        String token = emisor.generarToken(MONSTER);

        assertTrue(validador.validar(token));
        assertTrue(validador.invalidar(token));
        assertFalse(emisor.validar(token));
    }

    @Test
    void elAlmacenSoportaGeneracionConcurrente() throws Exception {
        int hilos = 8;
        int porHilo = 50;
        TokenServicio servicio = new TokenServicio(30, new RelojAjustable(BASE));
        Set<String> generados = ConcurrentHashMap.newKeySet();
        ExecutorService pool = Executors.newFixedThreadPool(hilos);
        try {
            List<Future<?>> tareas = new ArrayList<>();
            for (int h = 0; h < hilos; h++) {
                tareas.add(pool.submit(() -> {
                    for (int i = 0; i < porHilo; i++) {
                        generados.add(servicio.generarToken(MONSTER));
                    }
                }));
            }
            for (Future<?> tarea : tareas) {
                tarea.get(30, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }

        assertEquals(hilos * porHilo, generados.size());
        assertTrue(generados.stream().allMatch(servicio::validar));
    }

    /**
     * Reloj de prueba que permite avanzar el instante para provocar vencimientos sin esperas reales.
     */
    private static final class RelojAjustable extends Clock {

        private Instant instante;

        RelojAjustable(Instant inicial) {
            this.instante = inicial;
        }

        void avanzar(Duration duracion) {
            this.instante = this.instante.plus(duracion);
        }

        @Override
        public ZoneId getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zona) {
            return this;
        }

        @Override
        public Instant instant() {
            return instante;
        }
    }
}
