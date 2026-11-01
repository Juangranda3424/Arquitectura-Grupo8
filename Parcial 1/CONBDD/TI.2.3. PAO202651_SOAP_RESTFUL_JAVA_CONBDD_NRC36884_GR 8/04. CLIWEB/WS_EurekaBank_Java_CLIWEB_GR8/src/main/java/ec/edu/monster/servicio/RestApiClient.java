package ec.edu.monster.servicio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.model.LoginResponse;
import ec.edu.monster.model.MovimientoModel;
import ec.edu.monster.model.OperacionResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class RestApiClient {
    private final String baseUrl;
    private final Gson gson;

    public RestApiClient() {
        // Base URL del servicio RESTful Java
        // El servidor Java usa /resources como ApplicationPath
        this.baseUrl = "http://localhost:8080/WS_EurekaBank_Java_GR8/resources/";
        this.gson = new GsonBuilder().setLenient().create();
    }

    public <T> T get(String endpoint, Class<T> responseType) throws IOException {
        String url = baseUrl + endpoint;
        HttpURLConnection connection = null;
        
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            int responseCode = connection.getResponseCode();
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String jsonResponse = response.toString();
                    
                    // Para el login que retorna boolean
                    if (responseType == Boolean.class || responseType == boolean.class) {
                        return (T) Boolean.valueOf(jsonResponse.trim());
                    }
                    
                    return gson.fromJson(jsonResponse, responseType);
                }
            } else if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                return null;
            } else {
                throw new IOException("Error en la petición: " + responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public <T> T post(String endpoint, Object data, Class<T> responseType) throws IOException {
        String url = baseUrl + endpoint;
        HttpURLConnection connection = null;
        
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            String jsonData = gson.toJson(data);
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            
            BufferedReader reader;
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            String jsonResponse = response.toString();
            
            if (responseCode >= 200 && responseCode < 300) {
                // Para el login que retorna boolean
                if (responseType == Boolean.class || responseType == boolean.class) {
                    return (T) Boolean.valueOf(jsonResponse.trim());
                }
                
                // Para listas
                if (responseType == List.class) {
                    Type listType = new TypeToken<List<MovimientoModel>>(){}.getType();
                    return gson.fromJson(jsonResponse, listType);
                }
                
                return gson.fromJson(jsonResponse, responseType);
            } else {
                // Intentar parsear como el tipo esperado incluso en caso de error
                try {
                    if (responseType == LoginResponse.class) {
                        return gson.fromJson(jsonResponse, responseType);
                    }
                } catch (Exception e) {
                    // Si no se puede parsear, lanzar excepción
                }
                throw new IOException("Error en la petición: " + responseCode + " - " + jsonResponse);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<MovimientoModel> postMovimientos(String endpoint, Object data) throws IOException {
        String url = baseUrl + endpoint;
        HttpURLConnection connection = null;
        
        try {
            URL urlObj = new URL(url);
            connection = (HttpURLConnection) urlObj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            String jsonData = gson.toJson(data);
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            
            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String jsonResponse = response.toString();
                    
                    Type listType = new TypeToken<List<MovimientoModel>>(){}.getType();
                    return gson.fromJson(jsonResponse, listType);
                }
            } else {
                throw new IOException("Error en la petición: " + responseCode);
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

