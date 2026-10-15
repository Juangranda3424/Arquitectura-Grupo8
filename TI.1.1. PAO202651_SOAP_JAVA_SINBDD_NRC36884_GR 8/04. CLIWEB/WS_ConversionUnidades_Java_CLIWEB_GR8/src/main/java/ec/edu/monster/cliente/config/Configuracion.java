package ec.edu.monster.cliente.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Lee la configuración del cliente. Orden de prioridad (gana el primero):
 *   1. Propiedad de JVM        (-DWS_HOST=192.168.1.50)
 *   2. Variable de entorno     (WS_HOST=192.168.1.50)
 *   3. Archivo .env            (ver abajo)
 *   4. Valor por defecto del código
 *
 * El archivo .env se busca en:
 *   a) la ruta indicada por -DENV_FILE=... o la variable ENV_FILE (editable sin recompilar)
 *   b) ./.env en el directorio de trabajo de Payara
 *   c) el .env empaquetado dentro del WAR (src/main/resources/.env)
 */
public final class Configuracion {

    private final Map<String, String> env = new HashMap<>();
    private String origenEnv = "ninguno (valores por defecto)";

    public Configuracion() {
        cargarEnv();
    }

    /** De dónde salió el .env, útil para el log de arranque. */
    public String getOrigenEnv() {
        return origenEnv;
    }

    public String valor(String clave, String defecto) {
        String v = System.getProperty(clave);
        if (esVacio(v)) {
            v = System.getenv(clave);
        }
        if (esVacio(v)) {
            v = env.get(clave);
        }
        return esVacio(v) ? defecto : v.trim();
    }

    /** URL raíz del servidor SOAP, sin "/" final. */
    public String urlBase() {
        String completa = valor("WS_BASE_URL", "");
        String base = !completa.isEmpty()
                ? completa
                : "http://" + valor("WS_HOST", "localhost") + ":" + valor("WS_PORT", "8080")
                  + "/" + valor("WS_CONTEXT", "WS_ConversionUnidades_Java_GR8");
        return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
    }

    // ------------------------------------------------------------------ .env

    private void cargarEnv() {
        String ruta = System.getProperty("ENV_FILE");
        if (esVacio(ruta)) {
            ruta = System.getenv("ENV_FILE");
        }
        if (!esVacio(ruta) && leerArchivo(Paths.get(ruta.trim()))) {
            return;
        }
        if (leerArchivo(Paths.get(System.getProperty("user.dir", "."), ".env"))) {
            return;
        }
        try (InputStream in = Configuracion.class.getClassLoader().getResourceAsStream(".env")) {
            if (in != null) {
                parsear(new String(in.readAllBytes(), StandardCharsets.UTF_8));
                origenEnv = ".env empaquetado en el WAR";
            }
        } catch (IOException e) {
            // se siguen usando los valores por defecto
        }
    }

    private boolean leerArchivo(Path archivo) {
        try {
            if (Files.isRegularFile(archivo)) {
                parsear(Files.readString(archivo, StandardCharsets.UTF_8));
                origenEnv = archivo.toAbsolutePath().toString();
                return true;
            }
        } catch (IOException e) {
            // se prueba la siguiente ubicación
        }
        return false;
    }

    private void parsear(String texto) {
        env.clear();
        for (String linea : texto.split("\\R")) {
            String l = linea.trim();
            if (l.isEmpty() || l.startsWith("#")) {
                continue;
            }
            if (l.startsWith("export ")) {
                l = l.substring(7).trim();
            }
            int i = l.indexOf('=');
            if (i <= 0) {
                continue;
            }
            String clave = l.substring(0, i).trim();
            String v = l.substring(i + 1).trim();
            if (v.length() >= 2 && ((v.startsWith("\"") && v.endsWith("\""))
                    || (v.startsWith("'") && v.endsWith("'")))) {
                v = v.substring(1, v.length() - 1);
            }
            env.put(clave, v);
        }
    }

    private static boolean esVacio(String s) {
        return s == null || s.isBlank();
    }
}
