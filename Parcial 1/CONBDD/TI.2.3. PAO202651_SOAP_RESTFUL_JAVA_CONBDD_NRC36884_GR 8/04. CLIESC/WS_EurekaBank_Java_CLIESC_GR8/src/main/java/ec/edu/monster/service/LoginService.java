package ec.edu.monster.service;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.jsonb.JsonBindingFeature;

public class LoginService {

    // Usamos la URL centralizada
    private static final String LOGIN_ENDPOINT = Config.BASE_URL + "/login";

    private final Client client;

    public LoginService() {
        this.client = ClientBuilder.newBuilder()
                .register(JsonBindingFeature.class) // igual que en tu proyecto anterior
                .build();
    }

    public boolean auth(String username, String password) {
        if (username == null || password == null
                || username.isBlank() || password.isBlank()) {
            return false;
        }

        try {
            // Cuerpo JSON igual al de tu servicio REST
            String requestBody = String.format(
                    "{\"username\":\"%s\",\"password\":\"%s\"}",
                    username, password
            );

            Response response = client.target(LOGIN_ENDPOINT)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

            int status = response.getStatus();
            if (status != Response.Status.OK.getStatusCode()) {
                System.err.println("Error en autenticación: HTTP " + status);
                return false;
            }

            // 1) Intentar leer directamente como Boolean (si el servicio devuelve true/false)
            try {
                Boolean result = response.readEntity(Boolean.class);
                if (result != null) {
                    return result;
                }
            } catch (Exception ignore) {
                // Si no es boolean directo, seguimos abajo
            }

            // 2) Leer como String
            String body = response.readEntity(String.class).trim();
            // Si es plain "true" / "false"
            if ("true".equalsIgnoreCase(body) || "false".equalsIgnoreCase(body)) {
                return Boolean.parseBoolean(body);
            }

            // 3) Si viene JSON simple tipo {"success":true}
            // sin usar Jackson: validación básica
            body = body.replaceAll("\\s+", "").toLowerCase();
            if (body.contains("\"success\":true")) {
                return true;
            }

            return false;

        } catch (ProcessingException e) {
            // Fallo de conexión: no es lo mismo que credenciales rechazadas
            throw new IllegalStateException("No se pudo conectar con el servidor REST en " + LOGIN_ENDPOINT
                    + ". Verifique que esté en ejecución.", e);
        } catch (Exception e) {
            System.err.println("Error en autenticación: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
