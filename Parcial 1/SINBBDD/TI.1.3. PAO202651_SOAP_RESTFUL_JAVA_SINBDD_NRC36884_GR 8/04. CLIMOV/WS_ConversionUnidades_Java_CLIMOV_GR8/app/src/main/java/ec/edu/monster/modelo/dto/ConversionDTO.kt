package ec.edu.monster.modelo.dto

/**
 * Datos de una conversión, alineados con el contrato REST.
 *
 * Cuerpo enviado a `POST /api/conversiones/{magnitud}`: `valorOrigen`, `unidadOrigen`,
 * `unidadDestino`. El `resultado` del servidor añade `valorResultado` y `tipoMagnitud`.
 */
data class ConversionDTO(
    val valorOrigen: Double,
    val unidadOrigen: String,
    val unidadDestino: String,
    val valorResultado: Double = 0.0,
    val tipoMagnitud: String // LONGITUD, MASA, TEMPERATURA
)
