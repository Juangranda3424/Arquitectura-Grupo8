package ec.edu.monster.climov.util;

import android.os.Build;

public class ApiConfig {
    // URL base del servidor RESTful
    // Para desarrollo local con emulador Android: http://10.0.2.2:5046/api/
    // Para dispositivo físico: http://192.168.1.25:5046/api/
    // NOTA: 10.0.2.2 es la IP especial del emulador que apunta a localhost de la PC
    private static final String PC_IP = "10.40.73.239";
    public static final String BASE_URL = "http://" + (isEmulator() ? "10.0.2.2" : PC_IP) + ":5046/api/";

    /** Emulador -> 10.0.2.2 (localhost de la PC); dispositivo fisico -> PC_IP. */
    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.FINGERPRINT.contains("sdk_gphone")
                || Build.HARDWARE.contains("ranchu")
                || Build.HARDWARE.contains("goldfish")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk");
    }

    // Endpoints
    public static final String LOGIN = "Login";
    public static final String CUENTA = "Cuenta/";
    public static final String MOVIMIENTO = "Movimiento";

    // Métodos de cuenta
    public static final String DEPOSITO = "deposito";
    public static final String RETIRO = "retiro";
    public static final String TRANSFERENCIA = "transferencia";
}
