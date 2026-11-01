package ec.edu.monster.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Helper para configurar OkHttpClient
 */
public class OkHttpClientHelper {

    public static OkHttpClient createUnsafeOkHttpClient() {
        // Crear OkHttpClient con configuración personalizada
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        return builder.build();
    }
}

