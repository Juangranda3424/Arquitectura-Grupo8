package ec.edu.monster.cliente.api;

import ec.edu.monster.cliente.config.Configuracion;
import ec.edu.monster.cliente.soap.SoapClient;
import ec.edu.monster.cliente.soap.SoapException;
import jakarta.json.Json;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
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
 * API JSON que usa el navegador. Traduce cada llamada a una operación SOAP.
 *
 *   GET  /api/estado     ¿hay sesión iniciada?
 *   POST /api/login      WSAutenticador.login
 *   POST /api/logout     WSAutenticador.logout
 *   POST /api/convertir  WSConversorUnidades.convertir-unidades
 *
 * El token SOAP se guarda en la HttpSession del servidor: nunca llega al navegador.
 */
@WebServlet(name = "ApiServlet", urlPatterns = "/api/*")
public class ApiServlet extends HttpServlet {

    private static final String ATTR_TOKEN = "ws.token";
    private static final String ATTR_USUARIO = "ws.usuario";
    /** Respuesta de WSAutenticador.login cuando las credenciales no son válidas. */
    private static final String LOGIN_INVALIDO = "Usuario invalido";

    private SoapClient soap;
    private String urlAutenticador;
    private String urlConversor;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext ctx = config.getServletContext();

        Configuracion cfg = new Configuracion();
        String base = cfg.urlBase();
        urlAutenticador = base + "/WSAutenticador";
        urlConversor = base + "/WSConversorUnidades";

        soap = new SoapClient(
                cfg.valor("WS_TOKEN_NAMESPACE", "http://localhost:8080/"),
                cfg.valor("WS_NAMESPACE", "http://webservicio.monster.edu.ec/"));

        ctx.log("Configuración leída de: " + cfg.getOrigenEnv());
        ctx.log("Cliente SOAP -> " + urlAutenticador + " | " + urlConversor);
    }

    // ------------------------------------------------------------------ rutas

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if ("/estado".equals(req.getPathInfo())) {
            HttpSession s = req.getSession(false);
            boolean activa = s != null && s.getAttribute(ATTR_TOKEN) != null;
            JsonObject json = Json.createObjectBuilder()
                    .add("autenticado", activa)
                    .add("usuario", activa ? String.valueOf(s.getAttribute(ATTR_USUARIO)) : "")
                    .build();
            responder(resp, 200, json);
        } else {
            error(resp, 404, "Ruta no encontrada.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String ruta = req.getPathInfo() == null ? "" : req.getPathInfo();
        try {
            switch (ruta) {
                case "/login" -> login(req, resp);
                case "/logout" -> logout(req, resp);
                case "/convertir" -> convertir(req, resp);
                default -> error(resp, 404, "Ruta no encontrada.");
            }
        } catch (JsonException | ClassCastException e) {
            error(resp, 400, "El cuerpo de la petición no es un JSON válido.");
        } catch (SoapException e) {
            manejarErrorSoap(req, resp, e);
        }
    }

    // ------------------------------------------------------------------ operaciones

    private void login(HttpServletRequest req, HttpServletResponse resp) throws IOException, SoapException {
        JsonObject in = leerJson(req);
        String usuario = texto(in, "usuario");
        String clave = texto(in, "contrasena");
        if (usuario.isBlank() || clave.isEmpty()) {
            error(resp, 400, "Ingresa usuario y contraseña.");
            return;
        }

        // Usuario (modelo del servidor) se serializa como <username> y <password>.
        String params = "<login>"
                + SoapClient.elemento("username", usuario)
                + SoapClient.elemento("password", clave)
                + "</login>";

        String token = soap.invocar(urlAutenticador, "login", null, params);

        if (token.isBlank() || LOGIN_INVALIDO.equals(token)) {
            error(resp, 401, "Usuario o contraseña incorrectos.");
            return;
        }

        // Evita fijación de sesión: si ya había una sesión, se cambia su id.
        if (req.getSession(false) != null) {
            req.changeSessionId();
        }
        HttpSession sesion = req.getSession(true);
        sesion.setAttribute(ATTR_TOKEN, token);
        sesion.setAttribute(ATTR_USUARIO, usuario);

        responder(resp, 200, Json.createObjectBuilder()
                .add("autenticado", true)
                .add("usuario", usuario)
                .build());
    }

    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession sesion = req.getSession(false);
        if (sesion != null) {
            Object token = sesion.getAttribute(ATTR_TOKEN);
            if (token != null) {
                try {
                    // El handler del servidor exige el token en el header; el body es opcional.
                    soap.invocar(urlAutenticador, "logout", token.toString(),
                            "<logout>" + SoapClient.elemento("token", token.toString()) + "</logout>");
                } catch (SoapException e) {
                    // Aunque el servidor falle (o ya haya olvidado el token), se cierra la sesión local.
                    getServletContext().log("Logout SOAP no completado: " + e.getMessage());
                }
            }
            sesion.invalidate();
        }
        responder(resp, 200, Json.createObjectBuilder().add("autenticado", false).build());
    }

    private void convertir(HttpServletRequest req, HttpServletResponse resp) throws IOException, SoapException {
        HttpSession sesion = req.getSession(false);
        String token = sesion == null ? null : (String) sesion.getAttribute(ATTR_TOKEN);
        if (token == null) {
            JsonObject json = Json.createObjectBuilder()
                    .add("error", "Inicia sesión para convertir.")
                    .add("sesionExpirada", true).build();
            responder(resp, 401, json);
            return;
        }

        JsonObject in = leerJson(req);
        String magnitud = texto(in, "tipoMagnitud");
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

        // Elementos de ConversionDTO; valorResultado lo calcula el servidor.
        String params = "<peticionConversion>"
                + SoapClient.elemento("valorOrigen", Double.toString(valor))
                + SoapClient.elemento("unidadOrigen", origen)
                + SoapClient.elemento("unidadDestino", destino)
                + SoapClient.elemento("tipoMagnitud", magnitud)
                + "</peticionConversion>";

        String retorno = soap.invocar(urlConversor, "convertir-unidades", token, params);

        double resultado;
        try {
            resultado = Double.parseDouble(retorno);
        } catch (NumberFormatException e) {
            error(resp, 502, "El servidor devolvió un resultado no numérico: " + retorno);
            return;
        }
        if (Double.isNaN(resultado) || Double.isInfinite(resultado)) {
            error(resp, 422, "El resultado no es representable. Prueba con un valor más pequeño.");
            return;
        }

        responder(resp, 200, Json.createObjectBuilder()
                .add("valorOrigen", valor)
                .add("unidadOrigen", origen)
                .add("unidadDestino", destino)
                .add("tipoMagnitud", magnitud)
                .add("resultado", resultado)
                .build());
    }

    // ------------------------------------------------------------------ errores

    private void manejarErrorSoap(HttpServletRequest req, HttpServletResponse resp, SoapException e)
            throws IOException {
        switch (e.getTipo()) {
            case SIN_CONEXION -> error(resp, 503, e.getMessage());
            case RESPUESTA_INVALIDA -> error(resp, 502, e.getMessage());
            case FALLO_SERVICIO -> {
                if (e.esErrorDeAutenticacion()) {
                    // Token inválido: p. ej. el servidor se reinició y olvidó los tokens (viven en memoria).
                    HttpSession s = req.getSession(false);
                    if (s != null) {
                        s.invalidate();
                    }
                    responder(resp, 401, Json.createObjectBuilder()
                            .add("error", "Tu sesión terminó. Inicia sesión de nuevo.")
                            .add("sesionExpirada", true).build());
                } else {
                    // Validaciones del servidor (unidad inválida, valor negativo, bajo cero absoluto...).
                    error(resp, 422, e.getMessage());
                }
            }
        }
    }

    // ------------------------------------------------------------------ utilidades

    private static JsonObject leerJson(HttpServletRequest req) throws IOException {
        req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try (Reader r = req.getReader(); var lector = Json.createReader(r)) {
            return lector.readObject();
        }
    }

    private static String texto(JsonObject o, String clave) {
        JsonValue v = o.get(clave);
        if (v instanceof JsonString s) {
            // La contraseña se envía tal cual; el resto de campos se recorta.
            return "contrasena".equals(clave) ? s.getString() : s.getString().trim();
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
        responder(resp, estado, Json.createObjectBuilder().add("error", mensaje).build());
    }

    private static void responder(HttpServletResponse resp, int estado, JsonObject json) throws IOException {
        resp.setStatus(estado);
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.setHeader("Cache-Control", "no-store");
        resp.getWriter().write(json.toString());
    }
}
