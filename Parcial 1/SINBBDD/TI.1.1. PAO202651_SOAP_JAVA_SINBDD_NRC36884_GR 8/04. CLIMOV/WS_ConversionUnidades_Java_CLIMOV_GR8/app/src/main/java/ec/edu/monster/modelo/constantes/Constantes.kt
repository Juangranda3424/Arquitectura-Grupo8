package ec.edu.monster.modelo.constantes

object Constantes {

    val MAGNITUD = arrayOf(
        "LONGITUD",
        "MASA",
        "TEMPERATURA"
    )

    val LONGITUD = arrayOf(
        "CENTIMETRO",
        "METRO",
        "PIE",
        "YARDA",
        "MILLA"
    )

    val MASA = arrayOf(
        "GRAMO",
        "KILOGRAMO",
        "ONZA",
        "LIBRA",
        "QUINTAL"
    )

    val TEMPERATURA = arrayOf(
        "CELSIUS",
        "FAHRENHEIT",
        "KELVIN",
        "NEWTON",
        "REAUMUR"
    )

    // Variable global para guardar el token en memoria
    var token: String = ""

    /**
     * Función útil para obtener la lista de unidades según la magnitud seleccionada.
     * Facilita la actualización de los Spinners / Dropdowns en Jetpack Compose.
     */
    fun obtenerUnidadesPorMagnitud(magnitud: String): Array<String> {
        return when (magnitud.uppercase()) {
            "LONGITUD" -> LONGITUD
            "MASA" -> MASA
            "TEMPERATURA" -> TEMPERATURA
            else -> LONGITUD
        }
    }


}