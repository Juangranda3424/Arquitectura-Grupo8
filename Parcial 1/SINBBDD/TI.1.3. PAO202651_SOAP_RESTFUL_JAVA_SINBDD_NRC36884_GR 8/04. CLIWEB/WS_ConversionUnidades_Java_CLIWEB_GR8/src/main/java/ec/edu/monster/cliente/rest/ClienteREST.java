package ec.edu.monster.cliente.rest;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;

/**
 * Cliente REST del servidor de conversión de unidades.
 *
 * <p>Transporte con {@link java.net.http.HttpClient} y JSON con {@code jakarta.json} (JSON-P es
 * parte de {@code jakarta.jakartaee-web-api}, sin dependencias extra). El token viaja en la
 * cabecera {@code Authorization: Bearer <token>}.</p>
 *
 * <p>Todas las respuestas se interpretan como el envelope del servidor
 * {@code {estado, codigo, mensaje, resultado}}. Un envelope de error o un HTTP no-2xx se traduce a
 * {@link RestException} con {@link RestException.Tipo#FALLO_SERVICIO} y el código recibido; un
 * problema de red a {@link RestException.Tipo#SIN_CONEXION}; y un cuerpo no-JSON a
 * {@link RestException.Tipo#RESPUESTA_INVALIDA}.</p>
 */
public final class ClienteREST {

    public static final String TIPO_CONTENIDO_JSON = "application/json;charset=utf-8";
    public static final String MENSAJE_SIN_CONEXION =
            "No se pudo conectar con el servidor REST. Revisa que Payara esté encendido y el servidor desplegado.";

    private static final Duration TIEMPO_CONEXION = Duration.ofSeconds(5);
    private static final Duration TIEMPO_PETICION = Duration.ofSeconds(15);

    private final HttpClient http;
    private final String base;

    public ClienteREST(String apiBase) {
        this.base = quitarBarraFinal(apiBase);
        this.http = HttpClient.newBuilder().connectTimeout(TIEMPO_CONEXION).build();
    }

    /** Base de la API usada (por ejemplo {@code http://localhost:8080/WS_ConversionUnidades_Java_GR8/api}). */
    public String getBase() {
        return base;
    }

    /** Envelope del servidor ya interpretado. */
    public record Respuesta(String estado, int codigo, String mensaje, JsonObject resultado) {

        public boolean esExitosa() {
            return "OK".equalsIgnoreCase(estado);
        }
    }

    // ------------------------------------------------------------------ operaciones

    /** {@code GET /api/estado} (sin token). */
    public Respuesta estado() throws RestException {
        return enviar(construirGet("/estado", null));
    }

    /** {@code POST /api/auth/login}. */
    public Respuesta login(String usuario, String contrasena) throws RestException {
        JsonObject cuerpo = Json.createObjectBuilder()
                .add("username", texto(usuario))
                .add("password", texto(contrasena))
                .build();
        return enviar(construirPost("/auth/login", cuerpo.toString(), null));
    }

    /** {@code POST /api/auth/logout} (requiere token vigente). */
    public Respuesta logout(String token) throws RestException {
        return enviar(construirPost("/auth/logout", null, token));
    }

    /** {@code GET /api/catalogo} (requiere token vigente). */
    public Respuesta catalogo(String token) throws RestException {
        return enviar(construirGet("/catalogo", token));
    }

    /** {@code POST /api/conversiones/{magnitud}} (requiere token vigente). */
    public Respuesta convertir(String token, String magnitud, double valor,
                               String unidadOrigen, String unidadDestino) throws RestException {
        JsonObject cuerpo = Json.createObjectBuilder()
                .add("valorOrigen", valor)
                .add("unidadOrigen", texto(unidadOrigen))
                .add("unidadDestino", texto(unidadDestino))
                .build();
        String magnitudNormalizada = texto(magnitud).toLowerCase(Locale.ROOT);
        return enviar(construirPost("/conversiones/" + magnitudNormalizada, cuerpo.toString(), token));
    }

    // ------------------------------------------------------------------ interno

    private HttpRequest construirGet(String ruta, String token) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(base + ruta))
                .timeout(TIEMPO_PETICION)
                .header("Accept", TIPO_CONTENIDO_JSON)
                .GET();
        agregarToken(builder, token);
        return builder.build();
    }

    private HttpRequest construirPost(String ruta, String cuerpo, String token) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(base + ruta))
                .timeout(TIEMPO_PETICION)
                .header("Accept", TIPO_CONTENIDO_JSON)
                .header("Content-Type", TIPO_CONTENIDO_JSON);

        if (cuerpo == null) {
            builder.POST(HttpRequest.BodyPublishers.noBody());
        } else {
            builder.POST(HttpRequest.BodyPublishers.ofString(cuerpo, StandardCharsets.UTF_8));
        }

        agregarToken(builder, token);
        return builder.build();
    }

    private static void agregarToken(HttpRequest.Builder builder, String token) {
        if (token != null && !token.isBlank()) {
            builder.header("Authorization", "Bearer " + token);
        }
    }

    private Respuesta enviar(HttpRequest peticion) throws RestException {
        HttpResponse<String> respuesta;
        try {
            respuesta = http.send(peticion, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RestException(RestException.Tipo.SIN_CONEXION,
                    MENSAJE_SIN_CONEXION + " (" + peticion.uri() + ")", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RestException(RestException.Tipo.SIN_CONEXION, "Petición interrumpida.", e);
        }

        String cuerpo = respuesta.body() == null ? "" : respuesta.body().trim();
        if (cuerpo.isEmpty()) {
            throw new RestException(RestException.Tipo.RESPUESTA_INVALIDA,
                    "El servidor REST respondió vacío (HTTP " + respuesta.statusCode() + ").");
        }

        JsonObject envelope;
        try (JsonReader lector = Json.createReader(new StringReader(cuerpo))) {
            envelope = lector.readObject();
        } catch (RuntimeException e) {
            throw new RestException(RestException.Tipo.RESPUESTA_INVALIDA,
                    "El servidor REST respondió algo que no es JSON (HTTP " + respuesta.statusCode() + ").", e);
        }

        String estado = envelope.getString("estado", "");
        int codigo = envelope.getInt("codigo", 0);
        String mensaje = envelope.getString("mensaje", "");

        JsonObject resultado = null;
        JsonValue valor = envelope.get("resultado");
        if (valor != null && valor.getValueType() == JsonValue.ValueType.OBJECT) {
            resultado = valor.asJsonObject();
        }

        int codigoHttp = respuesta.statusCode();
        int codigoEfectivo = codigo > 0 ? codigo : codigoHttp;
        if (codigoHttp < 200 || codigoHttp >= 300 || !"OK".equalsIgnoreCase(estado)) {
            String detalle = mensaje.isBlank()
                    ? "Error inesperado del servidor REST (HTTP " + codigoHttp + ")."
                    : mensaje;
            throw new RestException(RestException.Tipo.FALLO_SERVICIO, codigoEfectivo, detalle);
        }

        return new Respuesta(estado, codigoEfectivo, mensaje, resultado);
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
