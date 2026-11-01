/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.service;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.jsonb.JsonBindingFeature;

/**
 *
 * @author crist
 */
public class LoginService {
    private static final String BASE_URL = "http://localhost:8080/WS_EurekaBank_Java_GR8/resources";
    private Client client;

    public LoginService() {
        this.client = ClientBuilder.newBuilder()
                .register(JsonBindingFeature.class)
                .build();
    }

    public boolean auth(String username, String password) {
        try {
            String requestBody = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
            
            Response response = client.target(BASE_URL)
                    .path("/login")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                try {
                    // Intentar leer como Boolean directamente
                    Boolean result = response.readEntity(Boolean.class);
                    return result != null && result;
                } catch (Exception e) {
                    // Si falla, intentar como String
                    String result = response.readEntity(String.class);
                    return Boolean.parseBoolean(result.trim());
                }
            }
            return false;
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
