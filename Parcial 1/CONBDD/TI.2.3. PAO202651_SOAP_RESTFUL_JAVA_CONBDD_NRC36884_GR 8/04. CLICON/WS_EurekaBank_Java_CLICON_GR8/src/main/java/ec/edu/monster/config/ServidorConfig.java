package ec.edu.monster.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Single place that resolves the server base URL used by this client.
 *
 * Precedence (first one found wins):
 * 1. JVM system property {@code servidor.url} (-Dservidor.url=...)
 * 2. External file {@code config.properties} in the current working directory
 * 3. {@code config.properties} on the classpath (src/main/resources)
 * 4. Built-in default ({@value #DEFAULT_URL})
 *
 * A trailing slash is always removed.
 */
public final class ServidorConfig {

    public static final String KEY = "servidor.url";
    public static final String DEFAULT_URL = "http://localhost:8080/WS_EurekaBank_Java_GR8/resources";
    private static final String FILE = "config.properties";

    private static final String URL = resolve();

    private ServidorConfig() {
    }

    /** Server base URL, without trailing slash. */
    public static String url() {
        return URL;
    }

    private static String resolve() {
        String value = clean(System.getProperty(KEY));
        if (value == null) {
            value = clean(fromFile());
        }
        if (value == null) {
            value = clean(fromClasspath());
        }
        return value != null ? value : clean(DEFAULT_URL);
    }

    private static String fromFile() {
        Path path = Paths.get(FILE);
        if (Files.isRegularFile(path)) {
            try (InputStream in = Files.newInputStream(path)) {
                return read(in);
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

    private static String fromClasspath() {
        try (InputStream in = ServidorConfig.class.getClassLoader().getResourceAsStream(FILE)) {
            return in == null ? null : read(in);
        } catch (IOException e) {
            return null;
        }
    }

    private static String read(InputStream in) throws IOException {
        Properties props = new Properties();
        props.load(in);
        return props.getProperty(KEY);
    }

    private static String clean(String value) {
        if (value == null) {
            return null;
        }
        String v = value.trim();
        while (v.endsWith("/")) {
            v = v.substring(0, v.length() - 1);
        }
        return v.isEmpty() ? null : v;
    }
}
