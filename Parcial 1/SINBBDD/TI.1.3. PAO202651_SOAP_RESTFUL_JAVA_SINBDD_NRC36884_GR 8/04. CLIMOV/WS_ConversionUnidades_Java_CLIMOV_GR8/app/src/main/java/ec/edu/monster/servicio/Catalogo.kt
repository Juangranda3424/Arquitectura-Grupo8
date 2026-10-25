package ec.edu.monster.servicio

/**
 * Servicio del catálogo de unidades.
 *
 * Expone `GET /api/catalogo`, que el [ec.edu.monster.controlador.ConversorViewModel] usa para
 * llenar los chips de categoría y los desplegables Desde/Hacia. El catálogo llega en el
 * `resultado` del envelope.
 */
class Catalogo {

    private val restCliente = RestCliente()

    /** @return el envelope del catálogo (o su error) tal como lo entrega [RestCliente]. */
    suspend fun obtenerCatalogo(token: String): RestCliente.Respuesta {
        return restCliente.catalogo(token)
    }
}
