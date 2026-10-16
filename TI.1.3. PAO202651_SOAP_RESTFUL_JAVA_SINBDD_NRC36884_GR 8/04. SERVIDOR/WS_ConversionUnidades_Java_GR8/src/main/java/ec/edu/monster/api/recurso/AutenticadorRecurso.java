package ec.edu.monster.api.recurso;

import java.time.Instant;

import ec.edu.monster.api.soporte.ErrorAPI;
import ec.edu.monster.api.soporte.RespuestaAPI;
import ec.edu.monster.api.soporte.SoportePeticion;
import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.seguridad.ValidadorToken;
import ec.edu.monster.servicio.AutenticadorServicio;
import ec.edu.monster.servicio.TokenServicio;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * {@code POST /api/auth/login} y {@code POST /api/auth/logout}: ciclo de sesión en memoria.
 */
@Path("auth")
public class AutenticadorRecurso {

    /** Mensaje de paridad con el servidor SOAP de TI.1.1. */
    public static final String MENSAJE_USUARIO_INVALIDO = "Usuario invalido";

    private static final String MENSAJE_CAMPOS_OBLIGATORIOS =
            "El nombre de usuario y la contraseña son obligatorios.";
    private static final String MENSAJE_LOGIN_OK = "Autenticación exitosa.";
    private static final String MENSAJE_LOGOUT_OK = "Sesión finalizada.";

    private final AutenticadorServicio autenticador = new AutenticadorServicio();
    private final TokenServicio tokens = new TokenServicio();

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response iniciarSesion(String cuerpo, @HeaderParam("Content-Type") String tipoContenido) {
        SoportePeticion.exigirTipoJSON(tipoContenido);

        JsonObject datos = SoportePeticion.leerObjetoJSON(cuerpo);
        String usuario = SoportePeticion.texto(datos, "username");
        String contrasena = SoportePeticion.texto(datos, "password");

        if (esBlanco(usuario) || esBlanco(contrasena)) {
            throw new ErrorAPI(400, MENSAJE_CAMPOS_OBLIGATORIOS);
        }

        String token = autenticador.autenticadorLogin(new Usuario(usuario, contrasena));
        if (MENSAJE_USUARIO_INVALIDO.equals(token)) {
            throw new ErrorAPI(401, MENSAJE_USUARIO_INVALIDO);
        }

        Instant vencimiento = tokens.vencimientoDe(token);
        JsonObject resultado = Json.createObjectBuilder()
                .add("token", token)
                .add("usuario", Constantes.USUARIO)
                .add("expiraEn", vencimiento == null ? "" : vencimiento.toString())
                .build();

        return RespuestaAPI.respuesta(200, RespuestaAPI.ok(200, MENSAJE_LOGIN_OK, resultado));
    }

    @POST
    @Path("logout")
    public Response cerrarSesion(@HeaderParam("Authorization") String autorizacion) {
        ValidadorToken.exigirTokenValido(autorizacion, tokens);

        autenticador.autenticadorLogout(ValidadorToken.extraerToken(autorizacion));

        return RespuestaAPI.respuesta(200, RespuestaAPI.ok(200, MENSAJE_LOGOUT_OK, JsonValue.TRUE));
    }

    private static boolean esBlanco(String valor) {
        return valor == null || valor.isBlank();
    }
}
