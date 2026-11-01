package ec.edu.monster.climov.service;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

import ec.edu.monster.climov.model.OperacionResponse;
import ec.edu.monster.climov.model.CuentaModel;
import ec.edu.monster.climov.util.ApiConfig;
import ec.edu.monster.climov.util.OkHttpClientHelper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;


public class CuentaService {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;
    private final Gson gson;

    public CuentaService() {
        // Usar OkHttpClient configurado para HTTPS con certificados autofirmados
        this.client = OkHttpClientHelper.createUnsafeOkHttpClient();
        this.gson = new Gson();
    }

    public OperacionResponse realizarDeposito(String cuenta, double monto) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("cuenta", cuenta);
            request.put("monto", monto);

            String json = gson.toJson(request);
            RequestBody body = RequestBody.create(json, JSON);
            Request httpRequest = new Request.Builder()
                    .url(ApiConfig.BASE_URL + ApiConfig.CUENTA + ApiConfig.DEPOSITO)
                    .post(body)
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return gson.fromJson(responseBody, OperacionResponse.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OperacionResponse realizarRetiro(String cuenta, double monto) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("cuenta", cuenta);
            request.put("monto", monto);

            String json = gson.toJson(request);
            RequestBody body = RequestBody.create(json, JSON);
            Request httpRequest = new Request.Builder()
                    .url(ApiConfig.BASE_URL + ApiConfig.CUENTA + ApiConfig.RETIRO)
                    .post(body)
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return gson.fromJson(responseBody, OperacionResponse.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OperacionResponse realizarTransferencia(String cuenta, String cuentaDestino, double monto) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("cuenta", cuenta);
            request.put("cuentaDestino", cuentaDestino);
            request.put("monto", monto);

            String json = gson.toJson(request);
            RequestBody body = RequestBody.create(json, JSON);
            Request httpRequest = new Request.Builder()
                    .url(ApiConfig.BASE_URL + ApiConfig.CUENTA + ApiConfig.TRANSFERENCIA)
                    .post(body)
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return gson.fromJson(responseBody, OperacionResponse.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CuentaModel obtenerCuentaPorNumero(String cuenta) {
        try {
            Request httpRequest = new Request.Builder()
                    .url(ApiConfig.BASE_URL + ApiConfig.CUENTA + cuenta)
                    .get()
                    .build();

            try (Response response = client.newCall(httpRequest).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return gson.fromJson(responseBody, CuentaModel.class);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
