package ec.edu.monster.service;

import android.util.Log;

import com.google.gson.Gson;

import ec.edu.monster.model.LoginRequest;
import ec.edu.monster.util.ApiConfig;
import ec.edu.monster.util.OkHttpClientHelper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class LoginService {
    private static final String TAG = "LoginService";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final Gson gson;

    public LoginService() {
        this.client = OkHttpClientHelper.createUnsafeOkHttpClient();
        this.gson = new Gson();
    }

    /**
     * Resultado de autenticación con información detallada del error
     */
    public static class AuthResult {
        public final boolean success;
        public final String errorMessage;
        public final AuthErrorType errorType;

        public AuthResult(boolean success, String errorMessage, AuthErrorType errorType) {
            this.success = success;
            this.errorMessage = errorMessage;
            this.errorType = errorType;
        }

        public static AuthResult success() {
            return new AuthResult(true, null, null);
        }

        public static AuthResult invalidCredentials() {
            return new AuthResult(false, "Credenciales incorrectas", AuthErrorType.INVALID_CREDENTIALS);
        }

        public static AuthResult connectionError(String message) {
            return new AuthResult(false, message, AuthErrorType.CONNECTION_ERROR);
        }

        public static AuthResult serverError(String message) {
            return new AuthResult(false, message, AuthErrorType.SERVER_ERROR);
        }
    }

    public enum AuthErrorType {
        INVALID_CREDENTIALS,
        CONNECTION_ERROR,
        SERVER_ERROR,
        UNKNOWN_ERROR
    }

    /**
     * Autentica al usuario
     * @param username Nombre de usuario
     * @param password Contraseña
     * @return Resultado de la autenticación con detalles del error si falla
     */
    public AuthResult auth(String username, String password) {
        String url = ApiConfig.BASE_URL + ApiConfig.LOGIN;
        Log.d(TAG, "Intentando autenticar en: " + url);
        Log.d(TAG, "Usuario: " + username);

        try {
            LoginRequest request = new LoginRequest(username, password);
            String json = gson.toJson(request);
            Log.d(TAG, "Request JSON: " + json);

            RequestBody body = RequestBody.create(json, JSON);
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                int code = response.code();
                Log.d(TAG, "Response code: " + code);

                if (response.body() != null) {
                    String responseBody = response.body().string().trim();
                    Log.d(TAG, "Response body: " + responseBody);

                    if (response.isSuccessful()) {
                        // El servidor Java retorna un boolean como JSON (true o false)
                        boolean success = Boolean.parseBoolean(responseBody);
                        if (success) {
                            return AuthResult.success();
                        } else {
                            return AuthResult.invalidCredentials();
                        }
                    } else {
                        // Error del servidor (400, 500, etc.)
                        String errorMsg = "Error del servidor (código " + code + ")";
                        if (code == 404) {
                            errorMsg = "Servidor no encontrado. Verifica que el servidor esté corriendo en " + ApiConfig.getCurrentBaseUrl();
                        } else if (code >= 500) {
                            errorMsg = "Error interno del servidor (código " + code + ")";
                        }
                        return AuthResult.serverError(errorMsg);
                    }
                } else {
                    return AuthResult.serverError("Respuesta vacía del servidor (código " + code + ")");
                }
            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "Error: No se pudo resolver el host", e);
            String errorMsg = "No se puede conectar al servidor. Verifica:\n" +
                    "1. Que el servidor esté corriendo\n" +
                    "2. Que la IP en ApiConfig.java sea correcta (" + ApiConfig.getCurrentBaseUrl() + ")\n" +
                    "3. Que el celular y la PC estén en la misma red WiFi\n" +
                    "4. Que el firewall permita conexiones en el puerto 8080";
            return AuthResult.connectionError(errorMsg);
        } catch (ConnectException e) {
            Log.e(TAG, "Error: No se pudo conectar al servidor", e);
            String errorMsg = "No se pudo conectar al servidor en " + ApiConfig.getCurrentBaseUrl() + "\n\n" +
                    "Verifica:\n" +
                    "1. Que el servidor esté corriendo\n" +
                    "2. Que la IP sea correcta\n" +
                    "3. Que el firewall permita conexiones";
            return AuthResult.connectionError(errorMsg);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "Error: Timeout de conexión", e);
            return AuthResult.connectionError("Tiempo de espera agotado. El servidor no responde.\n\n" +
                    "Verifica que el servidor esté corriendo y accesible.");
        } catch (IOException e) {
            Log.e(TAG, "Error de red: " + e.getMessage(), e);
            String errorMsg = "Error de conexión: " + e.getMessage() + "\n\n" +
                    "Verifica tu conexión a Internet y que el servidor esté disponible.";
            return AuthResult.connectionError(errorMsg);
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado", e);
            return AuthResult.connectionError("Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Método legacy para compatibilidad (retorna solo boolean)
     * @deprecated Usar auth() que retorna AuthResult para obtener más información
     */
    @Deprecated
    public boolean authLegacy(String username, String password) {
        AuthResult result = auth(username, password);
        return result.success;
    }
}

