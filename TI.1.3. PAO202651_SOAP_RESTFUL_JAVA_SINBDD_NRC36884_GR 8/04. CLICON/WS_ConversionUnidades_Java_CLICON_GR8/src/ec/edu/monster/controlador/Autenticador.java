package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.servicio.ClienteREST;
import ec.edu.monster.servicio.RespuestaAPI;

import jakarta.json.JsonObject;

/**
 * Controlador de autenticación contra el servicio REST.
 *
 * <p>Mantiene el contrato de TI.1.1: {@link #login(Usuario)} devuelve el token cuando la
 * autenticación es exitosa o el {@code mensaje} del servidor cuando falla.</p>
 */
public class Autenticador {

    private final ClienteREST cliente;

    public Autenticador() {
        this(new ClienteREST());
    }

    public Autenticador(ClienteREST cliente) {
        this.cliente = cliente;
    }

    /**
     * {@code POST /api/auth/login}
     *
     * @return el token de sesión, o el mensaje de error del servidor
     * @throws Exception si no hay conectividad con el servidor
     */
    public String login(Usuario usuario) throws Exception {
        RespuestaAPI respuesta = cliente.login(usuario.getUsuario(), usuario.getContrasena());

        if (respuesta.esExitosa()) {
            JsonObject resultado = respuesta.getResultado();
            if (resultado == null || !resultado.containsKey("token")) {
                return "";
            }
            return resultado.getString("token", "");
        }

        return respuesta.getMensaje();
    }

    /**
     * {@code POST /api/auth/logout}. Limpia el token local si el servidor confirma la salida.
     *
     * @return {@code true} si la sesión se cerró correctamente
     */
    public boolean logout() throws Exception {
        RespuestaAPI respuesta = cliente.logout();
        if (respuesta.esExitosa()) {
            Constantes.token = "";
            return true;
        }
        return false;
    }
}
