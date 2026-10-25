package ec.edu.monster.servicio

import java.util.Locale

/**
 * Servicio de conversión sobre REST.
 *
 * Conserva la firma pública de TI.1.2 ([realizarConversion] devuelve `String`)
 * pero delega en [RestCliente].
 */
class Conversor {

    private val restCliente = RestCliente()

    /**
     * `POST /api/conversiones/{tipo}`.
     *
     * @return el resultado formateado a 4 decimales en caso de éxito, o el `mensaje` del
     *         envelope en caso de error.
     */
    suspend fun realizarConversion(
        valor: Double,
        origen: String,
        destino: String,
        tipo: String,
        token: String
    ): String {
        val respuesta = restCliente.convertir(token, valor, origen, destino, tipo)
        if (!respuesta.esExitosa()) {
            return respuesta.mensaje
        }

        val numero = respuesta.resultado?.optDouble("valorResultado", Double.NaN) ?: Double.NaN
        return if (numero.isNaN()) {
            "El servidor no devolvió un resultado de conversión."
        } else {
            String.format(Locale.US, "%.4f", numero)
        }
    }
}
