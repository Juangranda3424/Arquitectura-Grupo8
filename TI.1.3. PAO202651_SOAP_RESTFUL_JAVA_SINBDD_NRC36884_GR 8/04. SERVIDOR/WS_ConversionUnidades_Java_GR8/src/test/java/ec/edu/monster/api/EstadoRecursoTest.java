package ec.edu.monster.api;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import ec.edu.monster.api.recurso.EstadoRecurso;

import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

class EstadoRecursoTest {

    private final EstadoRecurso recurso = new EstadoRecurso();

    @Test
    void estadoResponde200ConElEnvelopeEsperado() {
        Response respuesta = recurso.obtenerEstado();

        SoporteAPI.asertaEnvelope(respuesta, 200, "OK", "Servicio disponible.");
        assertEquals(200, respuesta.getStatus());

        JsonObject resultado = SoporteAPI.resultado(respuesta);
        assertEquals(4, resultado.size());
        assertEquals("WS_ConversionUnidades_Java_GR8", resultado.getString("servicio"));
        assertEquals("1.0-SNAPSHOT", resultado.getString("version"));
        assertEquals("REST/JSON", resultado.getString("protocolo"));
        assertDoesNotThrow(() -> Instant.parse(resultado.getString("horaServidor")));
    }
}
