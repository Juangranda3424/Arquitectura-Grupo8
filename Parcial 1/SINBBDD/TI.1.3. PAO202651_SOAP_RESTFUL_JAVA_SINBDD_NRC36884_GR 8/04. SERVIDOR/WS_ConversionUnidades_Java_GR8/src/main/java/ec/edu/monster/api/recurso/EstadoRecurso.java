package ec.edu.monster.api.recurso;

import java.time.Instant;

import ec.edu.monster.api.soporte.RespuestaAPI;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * {@code GET /api/estado}: sonda pública del servicio (no requiere token).
 */
@Path("estado")
public class EstadoRecurso {

    @GET
    public Response obtenerEstado() {
        JsonObject resultado = Json.createObjectBuilder()
                .add("servicio", "WS_ConversionUnidades_Java_GR8")
                .add("version", "1.0-SNAPSHOT")
                .add("protocolo", "REST/JSON")
                .add("horaServidor", Instant.now().toString())
                .build();

        return RespuestaAPI.respuesta(200, RespuestaAPI.ok(200, "Servicio disponible.", resultado));
    }
}
