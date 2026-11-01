package ec.edu.monster.service;

import ec.edu.monster.model.MovimientoModel;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.glassfish.jersey.jsonb.JsonBindingFeature;

public class MovimientoService {

    // Config.BASE_URL = "http://localhost:8080/WS_EurekaBank_Java_GR8/resources"
    private static final String BASE_URL = Config.BASE_URL;

    private final Client client;

    public MovimientoService() {
        this.client = ClientBuilder.newBuilder()
                .register(JsonBindingFeature.class) // Para mapear JSON <-> MovimientoModel con JSON-B
                .build();
    }

    /**
     * Obtiene la lista de movimientos de una cuenta.
     * Envíamos:
     *   POST {BASE_URL}/movimiento
     *   Body: {"numcuenta":"<cuenta>"}
     * Recibimos:
     *   200 OK con un JSON array de MovimientoModel.
     */
    public List<MovimientoModel> obtenerMovimientos(String cuenta) {
        if (cuenta == null || cuenta.isBlank()) {
            return null;
        }

        try {
            String requestBody = String.format("{\"numcuenta\":\"%s\"}", cuenta);

            Response response = client.target(BASE_URL)
                    .path("movimiento") // O "/movimiento" según tengas en tu recurso; si tu BASE_URL ya termina en /resources, esto está bien.
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<List<MovimientoModel>>() {});
            } else if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()) {
                return null;
            } else {
                System.err.println("Error al obtener movimientos: HTTP " + response.getStatus());
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error al obtener movimientos: " + e.getMessage());
            return null;
        }
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
