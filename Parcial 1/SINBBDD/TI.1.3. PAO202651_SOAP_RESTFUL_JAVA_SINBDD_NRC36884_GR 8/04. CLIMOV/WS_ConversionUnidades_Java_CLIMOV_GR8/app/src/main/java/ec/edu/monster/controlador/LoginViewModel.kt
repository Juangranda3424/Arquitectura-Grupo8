package ec.edu.monster.controlador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.monster.modelo.constantes.Constantes
import ec.edu.monster.servicio.Autenticador
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val servicioAutenticador = Autenticador()

    var usuarioText by mutableStateOf("")

    var claveText by mutableStateOf("")

    var estaCargando by mutableStateOf(false)
        // permite leer la variable desde fuera de la clase y solo modificarla desde dentro de esa clase
        private set

    var mensajeText by mutableStateOf("")
        private set

    // Manejadores de eventos
    // se ejecuta cada vez que el usuario
    // escribe o borra un caracter en el
    // campo de texto
    fun onUsuarioChange(nuevoValor: String) {
        usuarioText = nuevoValor
        if (mensajeText.isNotEmpty()) mensajeText = ""
    }

    fun onClaveChange(nuevoValor: String) {
        claveText = nuevoValor
        if (mensajeText.isNotEmpty()) mensajeText = ""
    }

    fun login() {

        if (claveText.isBlank() || usuarioText.isBlank()) {
            mensajeText = "Por favor, llene todos los campos"
            return
        }

        viewModelScope.launch {

            estaCargando = true
            mensajeText = "Autenticando..."

            val respuesta = servicioAutenticador.autenticar(usuarioText, claveText)

            if (esTokenValido(respuesta)) {
                Constantes.token = respuesta
                mensajeText = "¡Login exitoso!"
            } else {
                // Muestra el mensaje real del envelope devuelto por el servidor REST
                mensajeText = respuesta
            }

            estaCargando = false
        }
    }

    /**
     * El token del servidor tiene la forma "UUID + usuario" y no contiene espacios; todos los
     * mensajes del envelope (credenciales inválidas, sin conexión, validaciones...) son frases
     * con espacios. Este chequeo reemplaza al heurístico de TI.1.1 y permite mostrar el `mensaje`
     * exacto del servidor.
     */
    private fun esTokenValido(respuesta: String): Boolean {
        val r = respuesta.trim()
        return r.isNotEmpty() && !r.contains(' ')
    }
}
