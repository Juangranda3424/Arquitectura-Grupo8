package ec.edu.monster.servicio

class Conversor {
    private val soapCliente = SoapCliente()

    suspend fun realizarConversion(
        valor: Double,
        origen: String,
        destino: String,
        tipo: String,
        token: String
    ): String {
        return soapCliente.convertirUnidades(valor, origen, destino, tipo, token)
    }

}