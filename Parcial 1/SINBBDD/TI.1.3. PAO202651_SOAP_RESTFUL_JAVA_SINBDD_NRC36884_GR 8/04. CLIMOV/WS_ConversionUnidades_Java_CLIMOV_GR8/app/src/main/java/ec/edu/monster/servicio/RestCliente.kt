package ec.edu.monster.servicio

import android.util.Log
import ec.edu.monster.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * Cliente REST del servidor de conversión de unidades.
 *
 * Transporte con [HttpURLConnection] y JSON con [org.json] (ambos en el SDK de Android,
 * sin dependencias de terceros). Reemplaza al cliente legado de TI.1.1.
 *
 * El token viaja en la cabecera `Authorization: Bearer <token>`. El envelope del servidor
 * `{"estado","codigo","mensaje","resultado"}` se interpreta en [Respuesta].
 */
class RestCliente {

    /** Envelope del servidor ya interpretado. */
    data class Respuesta(
        val estado: String,
        val codigo: Int,
        val mensaje: String,
        val resultado: JSONObject?
    ) {
        fun esExitosa(): Boolean = estado.equals("OK", ignoreCase = true)
    }

    companion object {
        private const val TAG = "RestCliente"
        private const val TIEMPO_CONEXION_MS = 15_000
        private const val TIEMPO_LECTURA_MS = 15_000
        private const val JSON = "application/json"

        /** Valor por defecto: loopback del host visto desde el emulador Android (10.0.2.2). */
        const val URL_POR_DEFECTO = "http://10.0.2.2:8080/WS_ConversionUnidades_Java_GR8/api"

        /**
         * Base de la API. Prioridad: `API_BASE` (si no está vacío) &gt; `URL_BASE` &gt; [URL_POR_DEFECTO].
         * Se normaliza quitando la barra final y agregando `/api` si el override apunta a la raíz.
         */
        val BASE: String = normalizar(
            listOf(BuildConfig.API_BASE, BuildConfig.URL_BASE)
                .firstOrNull { it.isNotBlank() } ?: URL_POR_DEFECTO
        )

        private fun normalizar(valor: String): String {
            var base = valor.trim().trimEnd('/')
            if (!base.lowercase().endsWith("/api")) {
                base = "$base/api"
            }
            return base
        }
    }

    /** `POST /api/auth/login` */
    suspend fun login(usuario: String, clave: String): Respuesta = withContext(Dispatchers.IO) {
        val cuerpo = JSONObject()
            .put("username", usuario)
            .put("password", clave)
            .toString()
        ejecutar("POST", "/auth/login", cuerpo, null)
    }

    /** `POST /api/auth/logout` (requiere token vigente) */
    suspend fun logout(token: String): Respuesta = withContext(Dispatchers.IO) {
        ejecutar("POST", "/auth/logout", null, token)
    }

    /** `GET /api/catalogo` (requiere token vigente) */
    suspend fun catalogo(token: String): Respuesta = withContext(Dispatchers.IO) {
        ejecutar("GET", "/catalogo", null, token)
    }

    /** `POST /api/conversiones/{magnitud}` (requiere token vigente) */
    suspend fun convertir(
        token: String,
        valor: Double,
        origen: String,
        destino: String,
        tipo: String
    ): Respuesta = withContext(Dispatchers.IO) {
        val cuerpo = JSONObject()
            .put("valorOrigen", valor)
            .put("unidadOrigen", origen)
            .put("unidadDestino", destino)
            .toString()
        ejecutar("POST", "/conversiones/${tipo.lowercase()}", cuerpo, token)
    }

    private fun ejecutar(metodo: String, ruta: String, cuerpo: String?, token: String?): Respuesta {
        var conexion: HttpURLConnection? = null
        return try {
            conexion = (URL(BASE + ruta).openConnection() as HttpURLConnection).apply {
                requestMethod = metodo
                connectTimeout = TIEMPO_CONEXION_MS
                readTimeout = TIEMPO_LECTURA_MS
                setRequestProperty("Accept", JSON)
                if (cuerpo != null) {
                    doOutput = true
                    setRequestProperty("Content-Type", "$JSON;charset=utf-8")
                }
                if (!token.isNullOrBlank()) {
                    setRequestProperty("Authorization", "Bearer $token")
                }
            }

            if (cuerpo != null) {
                conexion.outputStream.use { salida: OutputStream ->
                    salida.write(cuerpo.toByteArray(StandardCharsets.UTF_8))
                }
            }

            val codigoHttp = conexion.responseCode
            val texto = leer(if (codigoHttp in 200..299) conexion.inputStream else conexion.errorStream)
            parsear(codigoHttp, texto)
        } catch (e: Exception) {
            Log.e(TAG, "Error REST: ${e.localizedMessage}", e)
            Respuesta(
                estado = "ERROR",
                codigo = 0,
                mensaje = "No se pudo conectar con el servidor REST: ${e.localizedMessage ?: e.javaClass.simpleName}",
                resultado = null
            )
        } finally {
            conexion?.disconnect()
        }
    }

    private fun leer(stream: InputStream?): String {
        if (stream == null) return ""
        return BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).use { it.readText() }
    }

    private fun parsear(codigoHttp: Int, texto: String): Respuesta {
        if (texto.isBlank()) {
            return Respuesta("ERROR", codigoHttp, "Respuesta vacía del servidor REST (HTTP $codigoHttp).", null)
        }
        return try {
            val json = JSONObject(texto)
            val estado = json.optString("estado", "")
            val codigo = json.optInt("codigo", codigoHttp).let { if (it > 0) it else codigoHttp }
            val mensaje = json.optString("mensaje", "")
            val resultado = json.optJSONObject("resultado")
            val exito = codigoHttp in 200..299 && estado.equals("OK", ignoreCase = true)
            if (exito) {
                Respuesta(estado, codigo, mensaje, resultado)
            } else {
                Respuesta(
                    estado = "ERROR",
                    codigo = codigo,
                    mensaje = mensaje.ifBlank { "Error del servidor REST (HTTP $codigoHttp)." },
                    resultado = resultado
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Respuesta no JSON (HTTP $codigoHttp)", e)
            Respuesta("ERROR", codigoHttp, "El servidor REST respondió algo que no es JSON (HTTP $codigoHttp).", null)
        }
    }
}
