package ec.edu.monster.service;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import ec.edu.monster.model.CuentaModel;
import ec.edu.monster.model.OperacionResponse;
import ec.edu.monster.util.ApiConfig;
import ec.edu.monster.util.OkHttpClientHelper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CuentaService {
    private static final String TAG = "CuentaService";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final Gson gson;

    public CuentaService() {
        this.client = OkHttpClientHelper.createUnsafeOkHttpClient();
        this.gson = new Gson();
    }

    /**
     * Resultado de operación con información detallada
     */
    public static class OperacionResult {
        public final OperacionResponse response;
        public final String errorMessage;
        public final OperacionErrorType errorType;
        public final boolean success;

        public OperacionResult(OperacionResponse response, String errorMessage, OperacionErrorType errorType) {
            this.response = response;
            this.errorMessage = errorMessage;
            this.errorType = errorType;
            this.success = response != null && response.isSuccess();
        }

        public static OperacionResult success(OperacionResponse response) {
            return new OperacionResult(response, null, null);
        }

        public static OperacionResult error(String message, OperacionErrorType errorType) {
            return new OperacionResult(null, message, errorType);
        }
    }

    public enum OperacionErrorType {
        CONNECTION_ERROR,
        SERVER_ERROR,
        INVALID_RESPONSE,
        UNKNOWN_ERROR
    }

    /**
     * Realiza un depósito
     */
    public OperacionResult realizarDeposito(String cuenta, double monto) {
        String url = ApiConfig.BASE_URL + ApiConfig.CUENTA + ApiConfig.DEPOSITO;
        Log.d(TAG, "Realizando depósito - URL: " + url);
        Log.d(TAG, "Cuenta: " + cuenta + ", Monto: " + monto);

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("cuenta", cuenta);
            request.put("monto", monto);

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
                            OperacionResponse operacionResponse = gson.fromJson(responseBody, OperacionResponse.class);
                            if (operacionResponse != null) {
                                return OperacionResult.success(operacionResponse);
                            } else {
                                return OperacionResult.error("Respuesta inválida del servidor", OperacionErrorType.INVALID_RESPONSE);
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "Error al parsear JSON: " + e.getMessage(), e);
                            return OperacionResult.error("Error al procesar la respuesta del servidor", OperacionErrorType.INVALID_RESPONSE);
                        }
                    } else {
                        // Error del servidor
                        String errorMsg = "Error del servidor (código " + code + ")";
                        if (code == 400) {
                            errorMsg = "Solicitud inválida. Verifica los datos ingresados.";
                        } else if (code == 404) {
                            errorMsg = "Servicio no encontrado.";
                        } else if (code >= 500) {
                            errorMsg = "Error interno del servidor. Intenta más tarde.";
                        }
                        return OperacionResult.error(errorMsg, OperacionErrorType.SERVER_ERROR);
                    }
                } else {
                    return OperacionResult.error("Respuesta vacía del servidor", OperacionErrorType.SERVER_ERROR);
                }
            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "Error: No se pudo resolver el host", e);
            return OperacionResult.error("No se puede conectar al servidor. Verifica tu conexión.", OperacionErrorType.CONNECTION_ERROR);
        } catch (ConnectException e) {
            Log.e(TAG, "Error: No se pudo conectar", e);
            return OperacionResult.error("No se pudo conectar al servidor. Verifica que el servidor esté corriendo.", OperacionErrorType.CONNECTION_ERROR);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "Error: Timeout", e);
            return OperacionResult.error("Tiempo de espera agotado. El servidor no responde.", OperacionErrorType.CONNECTION_ERROR);
        } catch (IOException e) {
            Log.e(TAG, "Error de red: " + e.getMessage(), e);
            return OperacionResult.error("Error de conexión: " + e.getMessage(), OperacionErrorType.CONNECTION_ERROR);
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado", e);
            return OperacionResult.error("Error inesperado: " + e.getMessage(), OperacionErrorType.UNKNOWN_ERROR);
        }
    }

    /**
     * Realiza un retiro
     */
    public OperacionResult realizarRetiro(String cuenta, double monto) {
        String url = ApiConfig.BASE_URL + ApiConfig.CUENTA + ApiConfig.RETIRO;
        Log.d(TAG, "Realizando retiro - URL: " + url);
        Log.d(TAG, "Cuenta: " + cuenta + ", Monto: " + monto);

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("cuenta", cuenta);
            request.put("monto", monto);

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
                            OperacionResponse operacionResponse = gson.fromJson(responseBody, OperacionResponse.class);
                            if (operacionResponse != null) {
                                return OperacionResult.success(operacionResponse);
                            } else {
                                return OperacionResult.error("Respuesta inválida del servidor", OperacionErrorType.INVALID_RESPONSE);
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "Error al parsear JSON: " + e.getMessage(), e);
                            return OperacionResult.error("Error al procesar la respuesta del servidor", OperacionErrorType.INVALID_RESPONSE);
                        }
                    } else {
                        String errorMsg = "Error del servidor (código " + code + ")";
                        if (code == 400) {
                            errorMsg = "Solicitud inválida. Verifica los datos ingresados.";
                        } else if (code == 404) {
                            errorMsg = "Servicio no encontrado.";
                        } else if (code >= 500) {
                            errorMsg = "Error interno del servidor. Intenta más tarde.";
                        }
                        return OperacionResult.error(errorMsg, OperacionErrorType.SERVER_ERROR);
                    }
                } else {
                    return OperacionResult.error("Respuesta vacía del servidor", OperacionErrorType.SERVER_ERROR);
                }
            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "Error: No se pudo resolver el host", e);
            return OperacionResult.error("No se puede conectar al servidor. Verifica tu conexión.", OperacionErrorType.CONNECTION_ERROR);
        } catch (ConnectException e) {
            Log.e(TAG, "Error: No se pudo conectar", e);
            return OperacionResult.error("No se pudo conectar al servidor. Verifica que el servidor esté corriendo.", OperacionErrorType.CONNECTION_ERROR);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "Error: Timeout", e);
            return OperacionResult.error("Tiempo de espera agotado. El servidor no responde.", OperacionErrorType.CONNECTION_ERROR);
        } catch (IOException e) {
            Log.e(TAG, "Error de red: " + e.getMessage(), e);
            return OperacionResult.error("Error de conexión: " + e.getMessage(), OperacionErrorType.CONNECTION_ERROR);
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado", e);
            return OperacionResult.error("Error inesperado: " + e.getMessage(), OperacionErrorType.UNKNOWN_ERROR);
        }
    }

    /**
     * Realiza una transferencia
     */
    public OperacionResult realizarTransferencia(String cuentaOrigen, String cuentaDestino, double monto) {
        String url = ApiConfig.BASE_URL + ApiConfig.CUENTA + ApiConfig.TRANSFERENCIA;
        Log.d(TAG, "Realizando transferencia - URL: " + url);
        Log.d(TAG, "Cuenta Origen: " + cuentaOrigen + ", Cuenta Destino: " + cuentaDestino + ", Monto: " + monto);

        try {
            Map<String, Object> request = new HashMap<>();
            request.put("cuenta", cuentaOrigen);
            request.put("cuentaDestino", cuentaDestino);
            request.put("monto", monto);

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
                            OperacionResponse operacionResponse = gson.fromJson(responseBody, OperacionResponse.class);
                            if (operacionResponse != null) {
                                return OperacionResult.success(operacionResponse);
                            } else {
                                return OperacionResult.error("Respuesta inválida del servidor", OperacionErrorType.INVALID_RESPONSE);
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "Error al parsear JSON: " + e.getMessage(), e);
                            return OperacionResult.error("Error al procesar la respuesta del servidor", OperacionErrorType.INVALID_RESPONSE);
                        }
                    } else {
                        String errorMsg = "Error del servidor (código " + code + ")";
                        if (code == 400) {
                            errorMsg = "Solicitud inválida. Verifica los datos ingresados.";
                        } else if (code == 404) {
                            errorMsg = "Servicio no encontrado.";
                        } else if (code >= 500) {
                            errorMsg = "Error interno del servidor. Intenta más tarde.";
                        }
                        return OperacionResult.error(errorMsg, OperacionErrorType.SERVER_ERROR);
                    }
                } else {
                    return OperacionResult.error("Respuesta vacía del servidor", OperacionErrorType.SERVER_ERROR);
                }
            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "Error: No se pudo resolver el host", e);
            return OperacionResult.error("No se puede conectar al servidor. Verifica tu conexión.", OperacionErrorType.CONNECTION_ERROR);
        } catch (ConnectException e) {
            Log.e(TAG, "Error: No se pudo conectar", e);
            return OperacionResult.error("No se pudo conectar al servidor. Verifica que el servidor esté corriendo.", OperacionErrorType.CONNECTION_ERROR);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "Error: Timeout", e);
            return OperacionResult.error("Tiempo de espera agotado. El servidor no responde.", OperacionErrorType.CONNECTION_ERROR);
        } catch (IOException e) {
            Log.e(TAG, "Error de red: " + e.getMessage(), e);
            return OperacionResult.error("Error de conexión: " + e.getMessage(), OperacionErrorType.CONNECTION_ERROR);
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado", e);
            return OperacionResult.error("Error inesperado: " + e.getMessage(), OperacionErrorType.UNKNOWN_ERROR);
        }
    }

    /**
     * Resultado de consulta de cuenta
     */
    public static class CuentaResult {
        public final CuentaModel cuenta;
        public final String errorMessage;
        public final OperacionErrorType errorType;
        public final boolean success;

        public CuentaResult(CuentaModel cuenta, String errorMessage, OperacionErrorType errorType) {
            this.cuenta = cuenta;
            this.errorMessage = errorMessage;
            this.errorType = errorType;
            this.success = cuenta != null;
        }

        public static CuentaResult success(CuentaModel cuenta) {
            return new CuentaResult(cuenta, null, null);
        }

        public static CuentaResult error(String message, OperacionErrorType errorType) {
            return new CuentaResult(null, message, errorType);
        }
    }

    /**
     * Obtiene los datos de una cuenta por número
     */
    public CuentaResult obtenerCuentaPorNumero(String cuenta) {
        String url = ApiConfig.BASE_URL + ApiConfig.CUENTA + cuenta;
        Log.d(TAG, "Consultando cuenta - URL: " + url);
        Log.d(TAG, "Número de cuenta: " + cuenta);

        try {
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .get()
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
                            CuentaModel cuentaModel = gson.fromJson(responseBody, CuentaModel.class);
                            if (cuentaModel != null) {
                                return CuentaResult.success(cuentaModel);
                            } else {
                                return CuentaResult.error("No se encontraron datos para la cuenta", OperacionErrorType.INVALID_RESPONSE);
                            }
                        } catch (JsonSyntaxException e) {
                            Log.e(TAG, "Error al parsear JSON: " + e.getMessage(), e);
                            return CuentaResult.error("Error al procesar la respuesta del servidor", OperacionErrorType.INVALID_RESPONSE);
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
                        return CuentaResult.error(errorMsg, OperacionErrorType.SERVER_ERROR);
                    }
                } else {
                    return CuentaResult.error("Respuesta vacía del servidor", OperacionErrorType.SERVER_ERROR);
                }
            }
        } catch (UnknownHostException e) {
            Log.e(TAG, "Error: No se pudo resolver el host", e);
            return CuentaResult.error("No se puede conectar al servidor. Verifica tu conexión.", OperacionErrorType.CONNECTION_ERROR);
        } catch (ConnectException e) {
            Log.e(TAG, "Error: No se pudo conectar", e);
            return CuentaResult.error("No se pudo conectar al servidor", OperacionErrorType.CONNECTION_ERROR);
        } catch (SocketTimeoutException e) {
            Log.e(TAG, "Error: Timeout", e);
            return CuentaResult.error("Tiempo de espera agotado", OperacionErrorType.CONNECTION_ERROR);
        } catch (IOException e) {
            Log.e(TAG, "Error de red: " + e.getMessage(), e);
            return CuentaResult.error("Error de conexión: " + e.getMessage(), OperacionErrorType.CONNECTION_ERROR);
        } catch (Exception e) {
            Log.e(TAG, "Error inesperado", e);
            return CuentaResult.error("Error inesperado: " + e.getMessage(), OperacionErrorType.UNKNOWN_ERROR);
        }
    }
}

