package ec.edu.monster.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.edu.monster.model.MovimientoModel;
import ec.edu.monster.util.ApiConfig;
import ec.edu.monster.util.OkHttpClientHelper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MovimientoService {
    private static final String TAG = "MovimientoService";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final Gson gson;

    public MovimientoService() {
        this.client = OkHttpClientHelper.createUnsafeOkHttpClient();
        this.gson = new Gson();
    }

    /**
     * Resultado de consulta de movimientos
     */
    public static class MovimientosResult {
        public final List<MovimientoModel> movimientos;
        public final String errorMessage;
        public final MovimientoErrorType errorType;
        public final boolean success;

        public MovimientosResult(List<MovimientoModel> movimientos, String errorMessage, MovimientoErrorType errorType) {
            this.movimientos = movimientos != null ? movimientos : new ArrayList<>();
            this.errorMessage = errorMessage;
            this.errorType = errorType;
            this.success = errorMessage == null;
        }

        public static MovimientosResult success(List<MovimientoModel> movimientos) {
            return new MovimientosResult(movimientos, null, null);
        }

        public static MovimientosResult error(String message, MovimientoErrorType errorType) {
            return new MovimientosResult(new ArrayList<>(), message, errorType);
        }
    }

    public enum MovimientoErrorType {
        CONNECTION_ERROR,
        SERVER_ERROR,
        INVALID_RESPONSE,
        UNKNOWN_ERROR
    }

    /**
     * Obtiene los movimientos de una cuenta
     */
    public MovimientosResult obtenerMovimientos(String cuenta) {
        String url = ApiConfig.BASE_URL + ApiConfig.MOVIMIENTO;
        Log.d(TAG, "Consultando movimientos - URL: " + url);
        Log.d(TAG, "Número de cuenta: " + cuenta);

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("numcuenta", cuenta);

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
                    String responseBody = response.body().string();
                    Log.d(TAG, "Response body: " + responseBody);

                    if (response.isSuccessful()) {
                        try {
                            Type listType = new TypeToken<List<MovimientoModel>>(){}.getType();
                            List<MovimientoModel> movimientos = gson.fromJson(responseBody, listType);
                            if (movimientos != null) {
                                Log.d(TAG, "Movimientos obtenidos: " + movimientos.size());
                                return MovimientosResult.success(movimientos);
                            } else {
                                return MovimientosResult.success(new ArrayList<>());
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "Error al parsear JSON: " + e.getMessage(), e);
                            return MovimientosResult.error("Error al procesar la respuesta del servidor", MovimientoErrorType.INVALID_RESPONSE);
                        }
                    } else {
                        String errorMsg = "Error del servidor (código " + code + ")";
                        if (code == 404) {
                            errorMsg = "Cuenta no encontrada";
                        } else if (code == 400) {
                            errorMsg = "Solicitud inválida";
                        } else if (code >= 500) {
                            errorMsg = "Error interno del servidor";
                        }
                        return MovimientosResult.error(errorMsg, MovimientoErrorType.SERVER_ERROR);
                    }
                } else {
                    return MovimientosResult.error("Respuesta vacía del servidor", MovimientoErrorType.SERVER_ERROR);
                }
            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "Error: No se pudo resolver el host", e);
            return MovimientosResult.error("No se puede conectar al servidor. Verifica tu conexión.", MovimientoErrorType.CONNECTION_ERROR);
        } catch (ConnectException e) {
            Log.e(TAG, "Error: No se pudo conectar", e);
            return MovimientosResult.error("No se pudo conectar al servidor", MovimientoErrorType.CONNECTION_ERROR);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "Error: Timeout", e);
            return MovimientosResult.error("Tiempo de espera agotado", MovimientoErrorType.CONNECTION_ERROR);
        } catch (IOException e) {
            Log.e(TAG, "Error de red: " + e.getMessage(), e);
            return MovimientosResult.error("Error de conexión: " + e.getMessage(), MovimientoErrorType.CONNECTION_ERROR);
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado", e);
            return MovimientosResult.error("Error inesperado: " + e.getMessage(), MovimientoErrorType.UNKNOWN_ERROR);
        }
    }
}

