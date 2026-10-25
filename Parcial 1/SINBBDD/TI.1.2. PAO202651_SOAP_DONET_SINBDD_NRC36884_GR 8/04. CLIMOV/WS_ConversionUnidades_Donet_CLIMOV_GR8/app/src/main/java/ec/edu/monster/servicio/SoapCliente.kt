package ec.edu.monster.servicio

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapPrimitive
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import org.kxml2.kdom.Element
import org.kxml2.kdom.Node
import ec.edu.monster.BuildConfig

/**
 * Cliente SOAP para el servidor WCF (.NET) WS_ConversionUnidades_Donet_GR8.
 *
 * Contratos del servidor:
 *   IWSAutenticador:
 *     string login(Usuario usuario)            -> Usuario { usuario, contrasena }   (namespace ...GR8.modelo)
 *     bool   logout(string token)
 *   IWSConversorUnidades:
 *     double convertir-unidades(ConversionDTO peticionConversion)
 *            ConversionDTO { TipoMagnitud, UnidadDestino, UnidadOrigen, ValorOrigen, ValorResultado }
 *            (namespace ...GR8.dto, PascalCase y en ORDEN ALFABETICO)
 *
 * El token viaja en el SOAP Header <Token> con el namespace de Constantes.NAMESPACE
 * del servidor ("http://localhost:8080/").
 */
class SoapCliente {

    companion object {
        // URLs desde el .env / BuildConfig
        private val URL_CONVERSOR = BuildConfig.URL_CONVERSOR
        private val URL_AUTENTICADOR = BuildConfig.URL_AUTENTICADOR

        // Namespace de las operaciones WCF (<tem:login>, <tem:usuario>, ...)
        private const val NS_TEMPURI = "http://tempuri.org/"

        // DataContract de la clase Usuario (modelo/Usuario.cs)
        private const val NS_MODELO =
            "http://schemas.datacontract.org/2004/07/WS_ConversionUnidades_Donet_GR8.modelo"

        // DataContract de la clase ConversionDTO (dto/ConversionDTO.cs)
        private const val NS_DTO =
            "http://schemas.datacontract.org/2004/07/WS_ConversionUnidades_Donet_GR8.dto"

        // Namespace del header <Token>: debe ser IGUAL a Constantes.NAMESPACE del servidor
        // (ValidadorTokenInspector busca FindHeader("Token", Constantes.NAMESPACE))
        private const val NS_TOKEN_HEADER = "http://localhost:8080/"

        // SOAPAction (tomados del WSDL)
        private const val ACTION_LOGIN = "http://tempuri.org/IWSAutenticador/login"
        private const val ACTION_LOGOUT = "http://tempuri.org/IWSAutenticador/logout"
        private const val ACTION_CONVERTIR =
            "http://tempuri.org/IWSConversorUnidades/convertir-unidades"
    }

    suspend fun ejecutarOperacionSoap(
        url: String,
        soapAction: String,
        request: SoapObject,
        token: String? = null
    ): String {
        return withContext(Dispatchers.IO) {
            val transport = HttpTransportSE(url, 15000)
            transport.debug = true
            try {
                val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11).apply {
                    dotNet = true            // WCF / .NET
                    implicitTypes = true     // sin xsi:type
                    setAddAdornments(false)  // sin id="o0" c:root="1"
                    setOutputSoapObject(request)

                    // Header <Token> (el servidor lo exige en convertir-unidades y logout)
                    if (!token.isNullOrEmpty()) {
                        val headerElement = Element().apply {
                            setName("Token")
                            setNamespace(NS_TOKEN_HEADER)
                            addChild(Node.TEXT, token)
                        }
                        headerOut = arrayOf(headerElement)
                    }
                }

                transport.call(soapAction, envelope)

                Log.d("SOAP_REQ", transport.requestDump ?: "")
                Log.d("SOAP_RES", transport.responseDump ?: "")

                when (val result = envelope.response) {
                    is SoapPrimitive -> result.toString()
                    is SoapObject -> result.toString()
                    else -> result?.toString() ?: "Sin respuesta del servidor"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SOAP_REQ", transport.requestDump ?: "")
                Log.e("SOAP_RES", transport.responseDump ?: "")
                // Si es un SoapFault, localizedMessage trae el mensaje del servidor
                // (ej: "Acceso denegado: Token inválido o sesión expirada.")
                "Error en comunicación SOAP: ${e.localizedMessage}"
            }
        }
    }

    /**
     * <tem:login>
     *   <tem:usuario>
     *     <ws:contrasena>..</ws:contrasena>
     *     <ws:usuario>..</ws:usuario>
     *   </tem:usuario>
     * </tem:login>
     *
     * Devuelve el token, o "Usuario invalido" si las credenciales no son correctas.
     */
    suspend fun obtenerToken(usuario: String, clave: String): String {
        Log.i("LOGIN", "Parametros: $usuario")

        val request = SoapObject(NS_TEMPURI, "login")

        val usuarioObject = SoapObject(NS_TEMPURI, "usuario").apply {
            // Orden alfabético del DataContract: contrasena, usuario
            addProperty(NS_MODELO, "contrasena", clave)
            addProperty(NS_MODELO, "usuario", usuario)
        }
        request.addSoapObject(usuarioObject)

        return ejecutarOperacionSoap(URL_AUTENTICADOR, ACTION_LOGIN, request)
    }

    /**
     * <tem:convertir-unidades>
     *   <tem:peticionConversion>
     *     <ws:TipoMagnitud>..</ws:TipoMagnitud>
     *     <ws:UnidadDestino>..</ws:UnidadDestino>
     *     <ws:UnidadOrigen>..</ws:UnidadOrigen>
     *     <ws:ValorOrigen>..</ws:ValorOrigen>
     *   </tem:peticionConversion>
     * </tem:convertir-unidades>
     *
     * tipo: LONGITUD | MASA | TEMPERATURA (el servidor ignora mayúsculas/minúsculas)
     * Devuelve el resultado numérico como texto.
     */
    suspend fun convertirUnidades(
        valor: Double,
        origen: String,
        destino: String,
        tipo: String,
        token: String
    ): String {
        val request = SoapObject(NS_TEMPURI, "convertir-unidades")

        val peticionObject = SoapObject(NS_TEMPURI, "peticionConversion").apply {
            // PascalCase y orden alfabético: si el orden cambia, WCF ignora los campos
            addProperty(NS_DTO, "TipoMagnitud", tipo)
            addProperty(NS_DTO, "UnidadDestino", destino)
            addProperty(NS_DTO, "UnidadOrigen", origen)
            addProperty(NS_DTO, "ValorOrigen", valor.toString())
        }
        request.addSoapObject(peticionObject)

        return ejecutarOperacionSoap(URL_CONVERSOR, ACTION_CONVERTIR, request, token)
    }

    /**
     * <tem:logout>
     *   <tem:token>..</tem:token>
     * </tem:logout>
     * + header <Token> (lo exige el inspector del servidor).
     * Devuelve "true" si el token existía y fue eliminado.
     */
    suspend fun logout(token: String): String {
        Log.i("LOGOUT", "Parametros: $token")

        val request = SoapObject(NS_TEMPURI, "logout")
        request.addProperty(NS_TEMPURI, "token", token)

        return ejecutarOperacionSoap(URL_AUTENTICADOR, ACTION_LOGOUT, request, token)
    }
}