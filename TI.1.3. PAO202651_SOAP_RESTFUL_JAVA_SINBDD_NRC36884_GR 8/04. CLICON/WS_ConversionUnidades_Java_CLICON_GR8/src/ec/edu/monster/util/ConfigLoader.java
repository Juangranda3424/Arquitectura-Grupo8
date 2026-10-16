package ec.edu.monster.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Carga la configuración del cliente REST desde {@code config.properties}.
 *
 * <p>Orden de búsqueda del archivo:</p>
 * <ol>
 *   <li>El directorio de trabajo actual ({@code config.properties}), útil al ejecutar desde NetBeans.</li>
 *   <li>El classpath (mismo archivo empaquetado con el JAR).</li>
 *   <li>Valores por defecto de {@code localhost:8080}.</li>
 * </ol>
 *
 * <p>Claves soportadas: {@code SERVIDOR_IP}, {@code SERVIDOR_PUERTO}, {@code SERVIDOR_CONTEXTO},
 * {@code API_BASE} y {@code URL_BASE}. La base de la API se resuelve en el orden
 * {@code URL_BASE} &gt; {@code API_BASE} &gt; {@code http://<IP>:<PUERTO>/<CONTEXTO>/api}.</p>
 */
public class ConfigLoader {

    public static final String ARCHIVO = "config.properties";

    public static final String SERVIDOR_IP_POR_DEFECTO = "localhost";
    public static final String SERVIDOR_PUERTO_POR_DEFECTO = "8080";
    public static final String SERVIDOR_CONTEXTO_POR_DEFECTO = "WS_ConversionUnidades_Java_GR8";

    private static final Properties properties = new Properties();
    private static boolean cargado = false;

    private ConfigLoader() {
    }

    private static synchronized void cargar() {
        if (cargado) {
            return;
        }

        boolean encontrado = cargarDesdeArchivo();
        if (!encontrado) {
            encontrado = cargarDesdeClasspath();
        }
        if (!encontrado) {
            System.out.println("No se encontró " + ARCHIVO
                    + ", usando valores por defecto (" + SERVIDOR_IP_POR_DEFECTO + ").");
        }

        aplicarDefecto("SERVIDOR_IP", SERVIDOR_IP_POR_DEFECTO);
        aplicarDefecto("SERVIDOR_PUERTO", SERVIDOR_PUERTO_POR_DEFECTO);
        aplicarDefecto("SERVIDOR_CONTEXTO", SERVIDOR_CONTEXTO_POR_DEFECTO);
        aplicarDefecto("API_BASE", "");
        aplicarDefecto("URL_BASE", "");

        cargado = true;
    }

    private static boolean cargarDesdeArchivo() {
        try (InputStream input = new FileInputStream(ARCHIVO)) {
            properties.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean cargarDesdeClasspath() {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(ARCHIVO)) {
            if (input == null) {
                return false;
            }
            try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                properties.load(reader);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void aplicarDefecto(String clave, String valor) {
        if (properties.getProperty(clave) == null) {
            properties.setProperty(clave, valor);
        }
    }

    /**
     * @param clave nombre de la propiedad
     * @return el valor configurado (nunca {@code null} para las claves conocidas)
     */
    public static String get(String clave) {
        cargar();
        return properties.getProperty(clave);
    }

    public static String getServidorIp() {
        return get("SERVIDOR_IP");
    }

    public static String getServidorPuerto() {
        return get("SERVIDOR_PUERTO");
    }

    public static String getServidorContexto() {
        return get("SERVIDOR_CONTEXTO");
    }

    /**
     * @return el override {@code URL_BASE} tal como está configurado (cadena vacía si no se definió)
     */
    public static String getUrlBase() {
        return get("URL_BASE");
    }

    /**
     * Base de la API usada por {@link ec.edu.monster.servicio.ClienteREST}.
     *
     * <p>Resolución: {@code URL_BASE} &gt; {@code API_BASE} &gt;
     * {@code http://<SERVIDOR_IP>:<SERVIDOR_PUERTO>/<SERVIDOR_CONTEXTO>/api}. Si un override no
     * termina en {@code /api}, se lo agrega.</p>
     *
     * @return URL base terminada en {@code /api}, sin barra final
     */
    public static String getApiBase() {
        String urlBase = get("URL_BASE");
        if (noVacio(urlBase)) {
            return normalizarBaseApi(urlBase);
        }

        String apiBase = get("API_BASE");
        if (noVacio(apiBase)) {
            return normalizarBaseApi(apiBase);
        }

        return "http://" + getServidorIp() + ":" + getServidorPuerto()
                + "/" + getServidorContexto() + "/api";
    }

    /**
     * Normaliza un override para que siempre apunte a la base de la API: quita barras finales y
     * agrega {@code /api} cuando el valor apunta a la raíz del servidor.
     */
    static String normalizarBaseApi(String valor) {
        String base = valor.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (!base.toLowerCase().endsWith("/api")) {
            base = base + "/api";
        }
        return base;
    }

    private static boolean noVacio(String valor) {
        return valor != null && !valor.isBlank();
    }
}
