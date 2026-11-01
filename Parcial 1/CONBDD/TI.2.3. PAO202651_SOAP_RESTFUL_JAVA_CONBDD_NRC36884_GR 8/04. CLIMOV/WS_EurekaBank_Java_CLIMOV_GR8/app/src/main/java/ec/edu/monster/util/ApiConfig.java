package ec.edu.monster.util;

import android.os.Build;

public class ApiConfig {
    // Configuración de URLs
    // Para desarrollo local con emulador Android: http://10.0.2.2:8080/WS_EurekaBank_Java_GR8/resources/
    // Para dispositivo físico: http://IP_DE_TU_PC:8080/WS_EurekaBank_Java_GR8/resources/
    // NOTA: 10.0.2.2 es la IP especial del emulador que apunta a localhost de la PC
    
    // IMPORTANTE: Cambiar esta IP por la IP local de tu PC donde está corriendo el servidor
    // Para obtener tu IP en Windows: ipconfig (buscar IPv4)
    // Para obtener tu IP en Linux/Mac: ifconfig o ip addr
    // Ambos dispositivos (PC y celular) deben estar en la misma red WiFi
    private static final String PC_IP = "10.95.194.5"; // CAMBIAR POR TU IP LOCAL
    
    private static final String SERVER_PORT = "8080";
    private static final String SERVER_CONTEXT = "WS_EurekaBank_Java_GR8/resources/";
    
    // URL base - Se determina automáticamente si es emulador o dispositivo físico
    public static final String BASE_URL = getBaseUrl();
    
    /**
     * Determina la URL base según el tipo de dispositivo
     * @return URL base del servidor
     */
    private static String getBaseUrl() {
        if (isEmulator()) {
            // Emulador: usa 10.0.2.2 que apunta a localhost de la PC
            return "http://10.0.2.2:" + SERVER_PORT + "/" + SERVER_CONTEXT;
        } else {
            // Dispositivo físico: usa la IP de la PC en la red local
            return "http://" + PC_IP + ":" + SERVER_PORT + "/" + SERVER_CONTEXT;
        }
    }
    
    /**
     * Detecta si la aplicación está corriendo en un emulador
     * @return true si es emulador, false si es dispositivo físico
     */
    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
    
    // Endpoints
    public static final String LOGIN = "login";
    public static final String CUENTA = "cuenta/";
    public static final String MOVIMIENTO = "movimiento";

    // Métodos de cuenta
    public static final String DEPOSITO = "deposito";
    public static final String RETIRO = "retiro";
    public static final String TRANSFERENCIA = "transferencia";
    
    /**
     * Obtiene la URL base actual (útil para debugging)
     */
    public static String getCurrentBaseUrl() {
        return BASE_URL;
    }
}

