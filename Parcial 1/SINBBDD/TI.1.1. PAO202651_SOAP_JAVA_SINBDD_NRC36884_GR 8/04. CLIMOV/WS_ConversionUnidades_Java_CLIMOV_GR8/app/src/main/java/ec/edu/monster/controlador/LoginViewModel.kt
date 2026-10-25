package ec.edu.monster.controlador

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.monster.modelo.constantes.Constantes
import ec.edu.monster.servicio.Autenticador
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(){

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

            //Log.i("LOGIN", "Respuesta recibida: $respuesta")

            // Se verifica que no sea un error de red/SOAP ni de credenciales
            if (!respuesta.startsWith("Error") && !respuesta.lowercase().contains("invalido")) {
                Constantes.token = respuesta
                mensajeText = "¡Login exitoso!"
            } else {
                // Muestra el error real retornado por el servidor o la excepción
                mensajeText = if (respuesta.startsWith("Error")) {
                    respuesta
                } else {
                    "Usuario o contraseña incorrectos"
                }
            }

            estaCargando = false
        }
    }


}