package ec.edu.monster.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties properties = new Properties();
    private static boolean cargado = false;

    private static void cargar() {
        if (!cargado) {
            // Intenta cargar desde la raíz del proyecto / carpeta del JAR
            try (InputStream input = new FileInputStream("config.properties")) {
                properties.load(input);
                cargado = true;
            } catch (Exception e) {
                // Valores por defecto en caso de que no encuentre el archivo
                System.out.println("No se encontró config.properties, usando valores por defecto (localhost).");
                properties.setProperty("SERVIDOR_IP", "localhost");
                properties.setProperty("SERVIDOR_PUERTO", "8080");
                properties.setProperty("ENDPOINT_AUTENTICADOR", "http://localhost:8080/WS_ConversionUnidades_Java_GR8/WSAutenticador");
                properties.setProperty("ENDPOINT_CONVERSOR", "http://localhost:8080/WS_ConversionUnidades_Java_GR8/WSConversorUnidades");
            }
        }
    }

    public static String get(String clave) {
        cargar();
        return properties.getProperty(clave);
    }

    public static String getEndpointAutenticador() {
        return get("ENDPOINT_AUTENTICADOR");
    }

    public static String getEndpointConversor() {
        return get("ENDPOINT_CONVERSOR");
    }
}