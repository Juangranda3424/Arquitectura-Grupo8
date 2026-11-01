package ec.edu.monster.service;

import ec.edu.monster.model.CuentaModel;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.jsonb.JsonBindingFeature;

public class CuentaService {

    // Usamos la URL centralizada del Config
    // Config.BASE_URL = "http://localhost:8080/WS_EurekaBank_Java_GR8/resources"
    private static final String BASE_URL = Config.BASE_URL;

    private final Client client;

    public CuentaService() {
        this.client = ClientBuilder.newBuilder()
                .register(JsonBindingFeature.class)
                .build();
    }

    // DEPÓSITO
    public boolean realizarDeposito(String cuenta, String monto, String tipo, String cd) {
        try {
            // Ajusta las claves según tu API REST real.
            // Si tu servicio solo usa cuenta/monto como antes, puedes dejar solo esos campos.
            String requestBody = String.format(
                    "{\"cuenta\":\"%s\",\"monto\":%s,\"tipo\":\"%s\",\"cd\":\"%s\"}",
                    cuenta, monto, tipo, cd
            );

            Response response = client.target(BASE_URL)
                    .path("cuenta/deposito")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String result = response.readEntity(String.class);
                // Soporta respuestas tipo: {"success":true,...} o similar
                return result.replaceAll("\\s+", "").toLowerCase().contains("\"success\":true");
            } else {
                System.err.println("Error al realizar depósito: HTTP " + response.getStatus());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al realizar depósito: " + e.getMessage());
            return false;
        }
    }

    // RETIRO
    public boolean realizarRetiro(String cuenta, String monto) {
        try {
            String requestBody = String.format(
                    "{\"cuenta\":\"%s\",\"monto\":%s}",
                    cuenta, monto
            );

            Response response = client.target(BASE_URL)
                    .path("cuenta/retiro")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String result = response.readEntity(String.class);
                return result.replaceAll("\\s+", "").toLowerCase().contains("\"success\":true");
            } else {
                System.err.println("Error al realizar retiro: HTTP " + response.getStatus());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al realizar retiro: " + e.getMessage());
            return false;
        }
    }

    // TRANSFERENCIA
    public boolean realizarTransferencia(String cuentaOrigen, String cuentaDestino, String monto) {
        try {
            String requestBody = String.format(
                    "{\"cuenta\":\"%s\",\"monto\":%s,\"cuentaDestino\":\"%s\"}",
                    cuentaOrigen, monto, cuentaDestino
            );

            Response response = client.target(BASE_URL)
                    .path("cuenta/transferencia")
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON));

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                String result = response.readEntity(String.class);
                return result.replaceAll("\\s+", "").toLowerCase().contains("\"success\":true");
            } else {
                System.err.println("Error al realizar transferencia: HTTP " + response.getStatus());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error al realizar transferencia: " + e.getMessage());
            return false;
        }
    }

    // CONSULTAR CUENTA
    public CuentaModel obtenerCuentaPorNumero(String cuenta) {
        try {
            Response response = client.target(BASE_URL)
                    .path("cuenta/" + cuenta)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == Response.Status.OK.getStatusCode()) {
                // JsonBindingFeature permite mapear JSON → CuentaModel si coincide la estructura
                return response.readEntity(CuentaModel.class);
            } else if (response.getStatus() == Response.Status.NO_CONTENT.getStatusCode()
                    || response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                return null;
            } else {
                System.err.println("Error al obtener cuenta: HTTP " + response.getStatus());
                return null;
            }
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
