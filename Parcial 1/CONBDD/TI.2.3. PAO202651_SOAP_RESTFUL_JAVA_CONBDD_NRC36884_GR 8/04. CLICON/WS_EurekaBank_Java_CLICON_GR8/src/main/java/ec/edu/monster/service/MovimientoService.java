/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.service;

import ec.edu.monster.config.ServidorConfig;
import ec.edu.monster.model.MovimientoModel;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.glassfish.jersey.jsonb.JsonBindingFeature;
/**
 *
 * @author crist
 */
public class MovimientoService {
    private static final String BASE_URL = ServidorConfig.url();
    private Client client;

    public MovimientoService() {
        this.client = ClientBuilder.newBuilder()
                .register(JsonBindingFeature.class)
                .build();
    }

    public List<MovimientoModel> obtenerMovimientos(String cuenta) {
        try {
            String requestBody = String.format("{\"numcuenta\":\"%s\"}", cuenta);
            
            Response response = client.target(BASE_URL)
                    .path("/movimiento")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(new GenericType<List<MovimientoModel>>() {});
            }
            return null;
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
