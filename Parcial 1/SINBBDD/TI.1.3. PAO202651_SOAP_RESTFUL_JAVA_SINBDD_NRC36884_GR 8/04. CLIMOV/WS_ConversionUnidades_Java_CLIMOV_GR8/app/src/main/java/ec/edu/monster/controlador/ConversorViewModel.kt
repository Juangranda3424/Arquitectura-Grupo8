package ec.edu.monster.controlador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.monster.modelo.constantes.Constantes
import ec.edu.monster.servicio.Autenticador
import ec.edu.monster.servicio.Catalogo
import ec.edu.monster.servicio.Conversor
import kotlinx.coroutines.launch

class ConversorViewModel : ViewModel() {

    private val servicioConversor = Conversor()
    private val servicioAutenticador = Autenticador()
    private val servicioCatalogo = Catalogo()

    // Estados observados por la Vista (Compose)
    var valorOrigenText by mutableStateOf("")
    var tipoSeleccionado by mutableStateOf(Constantes.MAGNITUD.first())
    var unidadOrigen by mutableStateOf("")
    var unidadDestino by mutableStateOf("")

    var resultadoText by mutableStateOf("Ingrese los valores y presione Convertir")
    var estaCargando by mutableStateOf(false)
        // permite leer la variable desde fuera de la clase y solo modificarla desde dentro de esa clase
        private set

    // Catálogo cargado desde GET /api/catalogo (o respaldo local si falla)
    var magnitudes by mutableStateOf(Constantes.MAGNITUD.toList())
        private set
    var unidadesDisponibles by mutableStateOf(Constantes.LONGITUD.toList())
        private set
    var mensajeCatalogo by mutableStateOf("")
        private set

    /** magnitud -> etiquetas mostradas en los desplegables (`CLAVE (Etiqueta, símbolo)`). */
    private var etiquetasPorMagnitud: Map<String, List<String>> = emptyMap()

    /** magnitud -> (etiqueta mostrada -> clave enviada a la API). */
    private var clavesPorEtiqueta: Map<String, Map<String, String>> = emptyMap()

    init {
        cargarCatalogo()
    }

    fun onValorOrigenChange(nuevoValor: String) {
        valorOrigenText = nuevoValor
    }

    fun onTipoChange(nuevoTipo: String) {
        tipoSeleccionado = nuevoTipo
        aplicarUnidades(nuevoTipo)
    }

    fun convertirUnidades() {

        val valor = valorOrigenText.replace(',', '.').toDoubleOrNull()
        if (valor == null) {
            resultadoText = "Por favor ingrese un número válido"
            return
        }

        viewModelScope.launch {
            estaCargando = true
            resultadoText = "Procesando..."

            val respuesta = servicioConversor.realizarConversion(
                valor = valor,
                origen = claveDe(tipoSeleccionado, unidadOrigen),
                destino = claveDe(tipoSeleccionado, unidadDestino),
                tipo = tipoSeleccionado,
                token = Constantes.token
            )

            resultadoText = respuesta
            estaCargando = false
        }
    }

    fun logout(onTerminado: () -> Unit) {
        viewModelScope.launch {
            estaCargando = true

            try {
                servicioAutenticador.logout(Constantes.token)
            } catch (_: Exception) {
                // La sesión local se cierra igual aunque el servidor falle
            } finally {
                Constantes.token = ""
                estaCargando = false
                onTerminado()   // <- navega al login
            }
        }
    }

    // ---------------------------------------------------------------- catálogo

    private fun cargarCatalogo() {
        viewModelScope.launch {
            val respuesta = servicioCatalogo.obtenerCatalogo(Constantes.token)
            val datos = respuesta.resultado

            if (!respuesta.esExitosa() || datos == null) {
                mensajeCatalogo = respuesta.mensaje.ifBlank { "No se pudo cargar el catálogo del servidor." }
                aplicarRespaldo()
                return@launch
            }

            val etiquetas = linkedMapOf<String, List<String>>()
            val claves = linkedMapOf<String, Map<String, String>>()

            val iterador = datos.keys()
            while (iterador.hasNext()) {
                val magnitud = iterador.next()
                val arreglo = datos.optJSONArray(magnitud) ?: continue
                val listaEtiquetas = mutableListOf<String>()
                val mapaClaves = mutableMapOf<String, String>()

                for (i in 0 until arreglo.length()) {
                    val unidad = arreglo.optJSONObject(i) ?: continue
                    val clave = unidad.optString("clave", "")
                    if (clave.isBlank()) continue
                    val etiqueta = unidad.optString("etiqueta", "")
                    val simbolo = unidad.optString("simbolo", "")
                    val texto = if (etiqueta.isBlank()) clave else "$clave ($etiqueta, $simbolo)"
                    listaEtiquetas.add(texto)
                    mapaClaves[texto] = clave
                }

                if (listaEtiquetas.isNotEmpty()) {
                    etiquetas[magnitud] = listaEtiquetas
                    claves[magnitud] = mapaClaves
                }
            }

            if (etiquetas.isEmpty()) {
                mensajeCatalogo = "El catálogo del servidor llegó vacío."
                aplicarRespaldo()
                return@launch
            }

            // Respeta el orden de las magnitudes conocidas y agrega cualquier extra al final
            val orden = Constantes.MAGNITUD.toList()
            magnitudes = orden.filter { etiquetas.containsKey(it) } +
                    etiquetas.keys.filter { it !in orden }

            etiquetasPorMagnitud = etiquetas
            clavesPorEtiqueta = claves
            mensajeCatalogo = ""
            tipoSeleccionado = magnitudes.firstOrNull() ?: tipoSeleccionado
            aplicarUnidades(tipoSeleccionado)
        }
    }

    private fun aplicarRespaldo() {
        val etiquetas = Constantes.MAGNITUD.associateWith {
            Constantes.obtenerUnidadesPorMagnitud(it).toList()
        }
        magnitudes = Constantes.MAGNITUD.toList()
        etiquetasPorMagnitud = etiquetas
        clavesPorEtiqueta = etiquetas.mapValues { (_, lista) -> lista.associateWith { it } }
        tipoSeleccionado = magnitudes.firstOrNull() ?: tipoSeleccionado
        aplicarUnidades(tipoSeleccionado)
    }

    private fun aplicarUnidades(magnitud: String) {
        val etiquetas = etiquetasPorMagnitud[magnitud]
            ?: Constantes.obtenerUnidadesPorMagnitud(magnitud).toList()

        unidadesDisponibles = etiquetas
        unidadOrigen = etiquetas.getOrElse(0) { "" }
        unidadDestino = etiquetas.getOrElse(1) { "" }
    }

    /** Traduce la etiqueta mostrada en el desplegable a la clave que espera la API. */
    private fun claveDe(magnitud: String, etiqueta: String): String {
        return clavesPorEtiqueta[magnitud]?.get(etiqueta) ?: etiqueta
    }
}
