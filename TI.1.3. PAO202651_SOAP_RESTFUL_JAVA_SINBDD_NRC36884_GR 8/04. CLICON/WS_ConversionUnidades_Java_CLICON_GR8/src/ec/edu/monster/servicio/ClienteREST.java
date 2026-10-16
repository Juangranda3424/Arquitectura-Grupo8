package ec.edu.monster.servicio;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.util.ConfigLoader;

import jakarta.json.Json;
import jakarta.json.JsonObject;

/**
 * Cliente HTTP del servicio REST de conversión de unidades.
 *
 * <p>Usa {@link java.net.http.HttpClient} para el transporte y {@code jakarta.json} (JSON-P) para
 * construir y parsear los cuerpos. El token de sesión viaja en la cabecera
 * {@code Authorization: Bearer <token>} tomado de {@link Constantes#token}.</p>
 *
 * <p>Todas las respuestas (éxito o error HTTP) se interpretan como {@link RespuestaAPI} para poder
 * mostrar el {@code mensaje} del servidor. Los fallos de transporte se convierten en
 * {@link IOException} con un mensaje claro.</p>
 */
public class ClienteREST {

    public static final String MENSAJE_SIN_CONEXION = "No se pudo conectar con el servidor REST: ";
    public static final String TIPO_CONTENIDO_JSON = "application/json;charset=utf-8";

    private static final Duration TIEMPO_CONEXION = Duration.ofSeconds(10);

    private final HttpClient http;
    private final String base;

    public ClienteREST() {
        this(ConfigLoader.getApiBase());
    }

    /** Constructor para pruebas o para fijar explícitamente la base de la API. */
    public ClienteREST(String base) {
        this.base = quitarBarraFinal(base);
        this.http = HttpClient.newBuilder()
                .connectTimeout(TIEMPO_CONEXION)
                .build();
    }

    public String getBase() {
        return base;
    }

    /**
     * {@code POST /api/auth/login}
     *
     * @return envelope con {@code resultado.token} en caso de éxito, o el error del servidor
     */
    public RespuestaAPI login(String usuario, String contrasena) throws IOException {
        JsonObject cuerpo = Json.createObjectBuilder()
                .add("username", texto(usuario))
                .add("password", texto(contrasena))
                .build();
        return enviar(construirPost("/auth/login", cuerpo.toString()));
    }

    /**
     * {@code POST /api/auth/logout} (requiere token vigente).
     */
    public RespuestaAPI logout() throws IOException {
        return enviar(construirPost("/auth/logout", null));
    }

    /**
     * {@code GET /api/catalogo} (requiere token vigente).
     */
    public RespuestaAPI catalogo() throws IOException {
        return enviar(construirGet("/catalogo"));
    }

    /**
     * {@code POST /api/conversiones/{magnitud}} (requiere token vigente).
     */
    public RespuestaAPI convertir(String magnitud, double valor, String unidadOrigen,
                                  String unidadDestino) throws IOException {
        JsonObject cuerpo = Json.createObjectBuilder()
                .add("valorOrigen", valor)
                .add("unidadOrigen", texto(unidadOrigen))
                .add("unidadDestino", texto(unidadDestino))
                .build();
        String magnitudNormalizada = texto(magnitud).toLowerCase(Locale.ROOT);
        return enviar(construirPost("/conversiones/" + magnitudNormalizada, cuerpo.toString()));
    }

    private HttpRequest construirGet(String ruta) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(base + ruta))
                .timeout(TIEMPO_CONEXION)
                .header("Accept", TIPO_CONTENIDO_JSON)
                .GET();
        agregarToken(builder);
        return builder.build();
    }

    private HttpRequest construirPost(String ruta, String cuerpo) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(base + ruta))
                .timeout(TIEMPO_CONEXION)
                .header("Accept", TIPO_CONTENIDO_JSON)
                .header("Content-Type", TIPO_CONTENIDO_JSON);

        if (cuerpo == null) {
            builder.POST(HttpRequest.BodyPublishers.noBody());
        } else {
            builder.POST(HttpRequest.BodyPublishers.ofString(cuerpo, StandardCharsets.UTF_8));
        }

        agregarToken(builder);
        return builder.build();
    }

    private void agregarToken(HttpRequest.Builder builder) {
        String token = Constantes.token;
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }
    }

    private RespuestaAPI enviar(HttpRequest peticion) throws IOException {
        HttpResponse<String> respuesta;
        try {
            respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IOException(MENSAJE_SIN_CONEXION + detalle(e), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException(MENSAJE_SIN_CONEXION + "petición interrumpida.", e);
        }

        String cuerpo = respuesta.body() == null ? "" : respuesta.body().trim();
        if (cuerpo.isEmpty()) {
            throw new IOException(MENSAJE_SIN_CONEXION
                    + "respuesta vacía del servidor (HTTP " + respuesta.statusCode() + ").");
        }

        try {
            return RespuestaAPI.parsear(cuerpo);
        } catch (RuntimeException e) {
            throw new IOException("Respuesta no válida del servidor REST (HTTP "
                    + respuesta.statusCode() + ").", e);
        }
    }

    private static String detalle(Exception e) {
        String mensaje = e.getMessage();
        return (mensaje == null || mensaje.isBlank()) ? e.getClass().getSimpleName() : mensaje;
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private static String quitarBarraFinal(String valor) {
        String base = valor == null ? "" : valor.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base;
    }
}
