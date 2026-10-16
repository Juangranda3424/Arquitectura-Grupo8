package ec.edu.monster.servicio

/**
 * Servicio de autenticación sobre REST.
 *
 * Conserva las firmas públicas de TI.1.1 ([autenticar] y [logout] devuelven `String`)
 * pero delega en [RestCliente].
 */
class Autenticador {

    private val restCliente = RestCliente()

    /**
     * `POST /api/auth/login`.
     *
     * @return el token en caso de éxito, o el `mensaje` del envelope en caso de error.
     */
    suspend fun autenticar(usuario: String, clave: String): String {
        val respuesta = restCliente.login(usuario, clave)
        return if (respuesta.esExitosa()) {
            respuesta.resultado?.optString("token", "").orEmpty()
        } else {
            respuesta.mensaje
        }
    }

    /**
     * `POST /api/auth/logout`.
     *
     * @return el `mensaje` del envelope (éxito o error), como en TI.1.1.
     */
    suspend fun logout(token: String): String {
        return restCliente.logout(token).mensaje
    }
}
