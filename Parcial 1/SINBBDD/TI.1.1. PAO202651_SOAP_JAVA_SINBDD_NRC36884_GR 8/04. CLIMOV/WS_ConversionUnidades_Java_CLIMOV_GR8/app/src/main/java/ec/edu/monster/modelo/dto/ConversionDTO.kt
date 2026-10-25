package ec.edu.monster.modelo.dto

data class ConversionDTO(
    val valorOrigen: Double,
    val unidadOrigen: String,
    val unidadDestino: String,
    val valorDestino: Double = 0.0,
    val tipoUnidad: String // LONGITUD, MASA, TEMPERATURA
)