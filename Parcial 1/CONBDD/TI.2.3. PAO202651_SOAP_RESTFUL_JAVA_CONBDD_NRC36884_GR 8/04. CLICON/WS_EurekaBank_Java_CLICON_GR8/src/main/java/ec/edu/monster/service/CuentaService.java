/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.service;

import ec.edu.monster.model.CuentaModel;
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
public class CuentaService {
    private static final String BASE_URL = "http://localhost:8080/WS_EurekaBank_Java_GR8/resources";
    private Client client;

    public CuentaService() {
        this.client = ClientBuilder.newBuilder()
                .register(JsonBindingFeature.class)
                .build();
    }

    public boolean realizarDeposito(String cuenta, String monto, String tipo, String cd) {
        try {
            String requestBody = String.format("{\"cuenta\":\"%s\",\"monto\":%s}", cuenta, monto);
            
            Response response = client.target(BASE_URL)
                    .path("/cuenta/deposito")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String result = response.readEntity(String.class);
                // El servidor devuelve un objeto JSON con success, saldo y mensaje
                return result.contains("\"success\":true");
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al realizar depósito: " + e.getMessage());
            return false;
        }
    }

    public boolean realizarRetiro(String cuenta, String monto) {
        try {
            String requestBody = String.format("{\"cuenta\":\"%s\",\"monto\":%s}", cuenta, monto);
            
            Response response = client.target(BASE_URL)
                    .path("/cuenta/retiro")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String result = response.readEntity(String.class);
                // El servidor devuelve un objeto JSON con success, saldo y mensaje
                return result.contains("\"success\":true");
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al realizar retiro: " + e.getMessage());
            return false;
        }
    }

    public boolean realizarTransferencia(String cuentaOrigen, String cuentaDestino, String monto) {
        try {
            String requestBody = String.format("{\"cuenta\":\"%s\",\"monto\":%s,\"cuentaDestino\":\"%s\"}", 
                    cuentaOrigen, monto, cuentaDestino);
            
            Response response = client.target(BASE_URL)
                    .path("/cuenta/transferencia")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String result = response.readEntity(String.class);
                // El servidor devuelve un objeto JSON con success, saldo y mensaje
                return result.contains("\"success\":true");
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error al realizar transferencia: " + e.getMessage());
            return false;
        }
    }

    public CuentaModel obtenerCuentaPorNumero(String cuenta) {
        try {
            Response response = client.target(BASE_URL)
                    .path("/cuenta/" + cuenta)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                return response.readEntity(CuentaModel.class);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error al obtener cuenta: " + e.getMessage());
            return null;
        }
    }

    public void close() {
        if (client != null) {
            client.close();
        }
    }
}
