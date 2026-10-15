package ec.edu.monster.servicio

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import org.kxml2.kdom.Node
import ec.edu.monster.BuildConfig

class SoapCliente {

    // Es como una variable estatica como Java

    companion object {
        // Leemos los valores inyectados desde el .env a través de BuildConfig
        private val NAMESPACE = BuildConfig.SOAP_NAMESPACE
        private val URL_CONVERSOR = BuildConfig.URL_CONVERSOR
        private val URL_AUTENTICADOR = BuildConfig.URL_AUTENTICADOR
    }

    suspend fun ejecutarOperacionSoap(
        url: String,
        nombreMetodo: String,
        request: SoapObject,
        token: String? = null
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                val soapAction = "$NAMESPACE$nombreMetodo"

                val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
                    dotNet = false
                    setOutputSoapObject(request)

                    // Agregar el Header si existe un token
                    if (!token.isNullOrEmpty()) {
                        val headerElement = org.kxml2.kdom.Element().apply {
                            setName("Token")
                            setNamespace(NAMESPACE)
                            addChild(Node.TEXT, token)
                        }
                        headerOut = arrayOf(headerElement)
                    }
                }

                val transport = HttpTransportSE(url, 15000)
                transport.debug = true
                transport.call(soapAction, envelope)

                when (val result = envelope.response) {
                    is SoapPrimitive -> result.toString()
                    is SoapObject -> result.toString()
                    else -> result?.toString() ?: "Sin respuesta del servidor"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Error en comunicación SOAP: ${e.localizedMessage}"
            }
        }
    }

    suspend fun convertirUnidades(
        valor: Double,
        origen: String,
        destino: String,
        tipo: String,
        token: String
    ): String {

        // Nombre exacto definido en @WebMethod(operationName = "convertir-unidades")
        val nombreMetodo = "convertir-unidades"

        // Crear la petición SOAP raíz

        val request = SoapObject(NAMESPACE, nombreMetodo)

        val dtoObject = SoapObject("", "peticionConversion").apply {
            addProperty("tipoMagnitud", tipo)
            addProperty("unidadDestino", destino)
            addProperty("unidadOrigen", origen)
            addProperty("valorOrigen", valor.toString())
        }

        request.addSoapObject(dtoObject)

        return ejecutarOperacionSoap(URL_CONVERSOR, nombreMetodo, request, token)
    }

    suspend fun obtenerToken(usuario: String, clave: String): String {
        val nombreMetodo = "login"

        Log.i("LOGIN", "Parametros: $usuario - $clave")

        // Petición raíz
        val request = SoapObject(NAMESPACE, nombreMetodo)

        // Objeto anidado <login> como pide el XML
        val loginObject = SoapObject("", "login").apply {
            addProperty("password", clave)
            addProperty("username", usuario)
        }

        // Insertar el objeto anidado dentro de la petición raíz
        request.addSoapObject(loginObject)

        return ejecutarOperacionSoap(URL_AUTENTICADOR, nombreMetodo, request)
    }

    suspend fun logout(token: String): String {
        val nombreMetodo = "logout"

        Log.i("LOGOUT", "Parametros: $token")

        // Petición raíz
        val request = SoapObject(NAMESPACE, nombreMetodo)

        // Objeto anidado <logout> como pide el XML
        val logoutObject = SoapObject("", "logout").apply {
            addProperty("token", token)
        }

        // Insertar el objeto anidado dentro de la petición raíz
        request.addSoapObject(logoutObject)

        return ejecutarOperacionSoap(URL_AUTENTICADOR, nombreMetodo, request, token)
    }


}