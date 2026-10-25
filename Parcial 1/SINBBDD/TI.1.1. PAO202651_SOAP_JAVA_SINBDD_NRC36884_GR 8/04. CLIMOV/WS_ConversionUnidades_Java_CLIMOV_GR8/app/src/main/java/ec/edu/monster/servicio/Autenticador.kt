package ec.edu.monster.servicio

class Autenticador {

    private val soapCliente = SoapCliente()

    suspend fun autenticar(usuario: String, clave: String): String {
        return soapCliente.obtenerToken(usuario, clave)
    }

    suspend fun logout(token: String): String {
        return soapCliente.logout(token);
    }

}