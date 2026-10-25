package ec.edu.monster.cliente.api;

import ec.edu.monster.cliente.config.Configuracion;
import ec.edu.monster.cliente.rest.ClienteREST;
import ec.edu.monster.cliente.rest.RestException;

import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Puente JSON entre el navegador y el servidor REST.
 *
 * <pre>
 *   Navegador --JSON--> ApiServlet --REST/JSON--> servidor REST
 *   GET  /api/estado     estado del servicio + sesión actual
 *   GET  /api/catalogo   catálogo de unidades (requiere sesión)
 *   POST /api/login      autenticación (guarda el token en la HttpSession)
 *   POST /api/logout     cierra la sesión y la invalida
 *   POST /api/convertir  ejecuta una conversión (requiere sesión)
 * </pre>
 *
 * <p>El token REST vive únicamente en la {@code HttpSession} del servidor (atributos
 * {@code ws.token}/{@code ws.usuario}): nunca se envía al navegador.</p>
 *
 * <p>Forma de respuesta para el navegador: siempre un objeto JSON con {@code mensaje}; en éxito
 * también {@code resultado} (y {@code usuario} en el login). Los errores de sesión añaden
 * {@code sesionExpirada: true}.</p>
 */
@WebServlet(name = "ApiServlet", urlPatterns = "/api/*")
public class ApiServlet extends HttpServlet {

    private static final String ATTR_TOKEN = "ws.token";
    private static final String ATTR_USUARIO = "ws.usuario";

    private ClienteREST cliente;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx = config.getServletContext();

        Configuracion cfg = new Configuracion();
        cliente = new ClienteREST(cfg.apiBase());

        ctx.log("Configuración leída de: " + cfg.getOrigenEnv());
        ctx.log("Cliente REST -> " + cliente.getBase());
    }

    // ------------------------------------------------------------------ rutas

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ruta = req.getPathInfo() == null ? "" : req.getPathInfo();
        switch (ruta) {
            case "/estado" -> estado(req, resp);
            case "/catalogo" -> catalogo(req, resp);
            default -> error(resp, 404, "Ruta no encontrada.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ruta = req.getPathInfo() == null ? "" : req.getPathInfo();
        switch (ruta) {
            case "/login" -> login(req, resp);
            case "/logout" -> logout(req, resp);
            case "/convertir" -> convertir(req, resp);
            default -> error(resp, 404, "Ruta no encontrada.");
        }
    }

    // ------------------------------------------------------------------ operaciones

    private void estado(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            ClienteREST.Respuesta r = cliente.estado();
            HttpSession sesion = req.getSession(false);
            boolean autenticado = sesion != null && sesion.getAttribute(ATTR_TOKEN) != null;

            JsonObjectBuilder json = Json.createObjectBuilder()
                    .add("mensaje", mensajeDe(r, "Servicio disponible."))
                    .add("autenticado", autenticado)
                    .add("usuario", autenticado ? String.valueOf(sesion.getAttribute(ATTR_USUARIO)) : "");
            if (r.resultado() != null) {
                json.add("resultado", r.resultado());
            }
            responder(resp, 200, json.build());
        } catch (RestException e) {
            manejarRest(req, resp, e);
        }
    }

    private void catalogo(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = tokenDeSesion(req);
        if (token == null) {
            sesionExpirada(resp, "Inicia sesión para ver el catálogo.");
            return;
        }
        try {
            ClienteREST.Respuesta r = cliente.catalogo(token);
            responder(resp, 200, exito(r, "Catálogo de unidades."));
        } catch (RestException e) {
            manejarRest(req, resp, e);
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonObject in = leerJson(req, resp);
        if (in == null) {
            return;
        }

        String usuario = texto(in, "username");
        String clave = texto(in, "password");
        if (usuario.isBlank() || clave.isEmpty()) {
            error(resp, 400, "Ingresa usuario y contraseña.");
            return;
        }

        try {
            ClienteREST.Respuesta r = cliente.login(usuario, clave);

            String token = r.resultado() == null ? "" : r.resultado().getString("token", "");
            if (token.isBlank()) {
                error(resp, 502, "El servidor REST no devolvió un token de sesión.");
                return;
            }

            // Evita fijación de sesión: si ya había una, se cambia su id.
            if (req.getSession(false) != null) {
                req.changeSessionId();
            }
            HttpSession sesion = req.getSession(true);
            sesion.setAttribute(ATTR_TOKEN, token);
            sesion.setAttribute(ATTR_USUARIO, usuario);

            responder(resp, 200, Json.createObjectBuilder()
                    .add("usuario", usuario)
                    .add("mensaje", mensajeDe(r, "Sesión iniciada."))
                    .build());
        } catch (RestException e) {
            if (e.getTipo() == RestException.Tipo.FALLO_SERVICIO && e.esErrorDeAutenticacion()) {
                // Mensaje del servidor, por ejemplo "Usuario invalido".
                error(resp, 401, e.getMessage());
            } else {
                manejarRest(req, resp, e);
            }
        }
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String mensaje = "Sesión cerrada.";
        HttpSession sesion = req.getSession(false);
        if (sesion != null) {
            Object token = sesion.getAttribute(ATTR_TOKEN);
            if (token != null) {
                try {
                    ClienteREST.Respuesta r = cliente.logout(token.toString());
                    mensaje = mensajeDe(r, mensaje);
                } catch (RestException e) {
                    // Aunque el servidor falle (o ya haya olvidado el token), se cierra la sesión local.
                    getServletContext().log("Logout REST no completado: " + e.getMessage());
                }
            }
            sesion.invalidate();
        }
        responder(resp, 200, Json.createObjectBuilder().add("mensaje", mensaje).build());
    }

    private void convertir(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String token = tokenDeSesion(req);
        if (token == null) {
            sesionExpirada(resp, "Inicia sesión para convertir.");
            return;
        }

        JsonObject in = leerJson(req, resp);
        if (in == null) {
            return;
        }

        String magnitud = texto(in, "magnitud");
        String origen = texto(in, "unidadOrigen");
        String destino = texto(in, "unidadDestino");
        Double valor = numero(in, "valorOrigen");

        if (magnitud.isBlank() || origen.isBlank() || destino.isBlank()) {
            error(resp, 400, "Elige la magnitud y las unidades de origen y destino.");
            return;
        }
        if (valor == null || valor.isNaN() || valor.isInfinite()) {
            error(resp, 400, "Ingresa un valor numérico válido.");
            return;
        }

        try {
            ClienteREST.Respuesta r = cliente.convertir(token, magnitud, valor, origen, destino);
            responder(resp, 200, exito(r, "Conversión realizada."));
        } catch (RestException e) {
            if (e.getTipo() == RestException.Tipo.FALLO_SERVICIO && e.getCodigo() == 404) {
                // Magnitud desconocida: el mensaje del servidor se pasa tal cual.
                error(resp, 404, e.getMessage());
            } else {
                manejarRest(req, resp, e);
            }
        }
    }

    // ------------------------------------------------------------------ errores

    private void manejarRest(HttpServletRequest req, HttpServletResponse resp, RestException e)
            throws IOException {
        switch (e.getTipo()) {
            case SIN_CONEXION -> error(resp, 503, e.getMessage());
            case RESPUESTA_INVALIDA -> error(resp, 502, e.getMessage());
            case FALLO_SERVICIO -> {
                if (e.esErrorDeAutenticacion()) {
                    HttpSession s = req.getSession(false);
                    if (s != null) {
                        s.invalidate();
                    }
                    sesionExpirada(resp, "Tu sesión terminó. Inicia sesión de nuevo.");
                } else {
                    error(resp, 422, e.getMessage());
                }
            }
        }
    }

    // ------------------------------------------------------------------ utilidades

    private static String tokenDeSesion(HttpServletRequest req) {
        HttpSession sesion = req.getSession(false);
        if (sesion == null) {
            return null;
        }
        Object token = sesion.getAttribute(ATTR_TOKEN);
        return token == null ? null : token.toString();
    }

    private static String mensajeDe(ClienteREST.Respuesta r, String defecto) {
        return r.mensaje() == null || r.mensaje().isBlank() ? defecto : r.mensaje();
    }

    private static JsonObject exito(ClienteREST.Respuesta r, String defecto) {
        JsonObjectBuilder json = Json.createObjectBuilder().add("mensaje", mensajeDe(r, defecto));
        if (r.resultado() != null) {
            json.add("resultado", r.resultado());
        }
        return json.build();
    }

    private static JsonObject leerJson(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (Reader r = req.getReader(); var lector = Json.createReader(r)) {
            return lector.readObject();
        } catch (JsonException | ClassCastException | IllegalStateException e) {
            error(resp, 400, "El cuerpo de la petición no es un JSON válido.");
            return null;
        }
    }

    private static String texto(JsonObject o, String clave) {
        JsonValue v = o.get(clave);
        if (v instanceof JsonString s) {
            // La contraseña se envía tal cual; el resto de campos se recorta.
            return "password".equals(clave) ? s.getString() : s.getString().trim();
        }
        return "";
    }

    private static Double numero(JsonObject o, String clave) {
        JsonValue v = o.get(clave);
        if (v instanceof JsonNumber n) {
            return n.doubleValue();
        }
        if (v instanceof JsonString s) {
            try {
                return Double.valueOf(s.getString().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private static void error(HttpServletResponse resp, int estado, String mensaje) throws IOException {
        responder(resp, estado, Json.createObjectBuilder().add("mensaje", mensaje).build());
    }

    private static void sesionExpirada(HttpServletResponse resp, String mensaje) throws IOException {
        responder(resp, 401, Json.createObjectBuilder()
                .add("mensaje", mensaje)
                .add("sesionExpirada", true)
                .build());
    }

    private static void responder(HttpServletResponse resp, int estado, JsonObject json) throws IOException {
        resp.setStatus(estado);
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setHeader("Cache-Control", "no-store");
        resp.getWriter().write(json.toString());
    }
}
