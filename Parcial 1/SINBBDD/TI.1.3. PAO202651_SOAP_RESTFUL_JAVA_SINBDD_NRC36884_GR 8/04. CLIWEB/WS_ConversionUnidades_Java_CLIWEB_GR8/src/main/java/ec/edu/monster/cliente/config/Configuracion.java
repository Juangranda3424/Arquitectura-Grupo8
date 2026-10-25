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
 * Lee la configuración del cliente web. Orden de prioridad (gana el primero):
 * <ol>
 *   <li>Propiedad de JVM        ({@code -DSERVIDOR_IP=192.168.1.50})</li>
 *   <li>Variable de entorno     ({@code SERVIDOR_IP=192.168.1.50})</li>
 *   <li>Archivo {@code .env}    (ver abajo)</li>
 *   <li>Valor por defecto del código</li>
 * </ol>
 *
 * El archivo {@code .env} se busca en:
 * <ol type="a">
 *   <li>la ruta indicada por {@code -DENV_FILE=...} o la variable {@code ENV_FILE}</li>
 *   <li>{@code ./.env} en el directorio de trabajo de Payara</li>
 *   <li>el {@code .env} empaquetado dentro del WAR ({@code src/main/resources/.env})</li>
 * </ol>
 *
 * <p>Claves unificadas del cliente REST: {@code SERVIDOR_IP}, {@code SERVIDOR_PUERTO},
 * {@code SERVIDOR_CONTEXTO}, {@code API_BASE} y {@code URL_BASE}.</p>
 */
public final class Configuracion {

    public static final String SERVIDOR_IP_POR_DEFECTO = "localhost";
    public static final String SERVIDOR_PUERTO_POR_DEFECTO = "8080";
    public static final String SERVIDOR_CONTEXTO_POR_DEFECTO = "WS_ConversionUnidades_Java_GR8";

    private final Map<String, String> env = new HashMap<>();
    private String origenEnv = "ninguno (valores por defecto)";

    public Configuracion() {
        cargarEnv();
    }

    /** De dónde salió el {@code .env}, útil para el log de arranque. */
    public String getOrigenEnv() {
        return origenEnv;
    }

    /**
     * Resuelve el valor de una clave: propiedad de JVM &gt; variable de entorno &gt; {@code .env}
     * &gt; valor por defecto.
     */
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

    /**
     * Base de la API REST, sin barra final.
     *
     * <p>Resolución: {@code URL_BASE} &gt; {@code API_BASE} &gt;
     * {@code http://<SERVIDOR_IP>:<SERVIDOR_PUERTO>/<SERVIDOR_CONTEXTO>/api}. Si un override no
     * termina en {@code /api}, se lo agrega.</p>
     */
    public String apiBase() {
        String urlBase = valor("URL_BASE", "");
        if (!urlBase.isEmpty()) {
            return normalizar(urlBase);
        }

        String apiBase = valor("API_BASE", "");
        if (!apiBase.isEmpty()) {
            return normalizar(apiBase);
        }

        return "http://" + valor("SERVIDOR_IP", SERVIDOR_IP_POR_DEFECTO)
                + ":" + valor("SERVIDOR_PUERTO", SERVIDOR_PUERTO_POR_DEFECTO)
                + "/" + valor("SERVIDOR_CONTEXTO", SERVIDOR_CONTEXTO_POR_DEFECTO)
                + "/api";
    }

    /**
     * Normaliza un override para que siempre apunte a la base de la API: quita barras finales y
     * agrega {@code /api} cuando el valor apunta a la raíz del servidor.
     */
    static String normalizar(String valor) {
        String base = valor.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (!base.toLowerCase().endsWith("/api")) {
            base = base + "/api";
        }
        return base;
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
