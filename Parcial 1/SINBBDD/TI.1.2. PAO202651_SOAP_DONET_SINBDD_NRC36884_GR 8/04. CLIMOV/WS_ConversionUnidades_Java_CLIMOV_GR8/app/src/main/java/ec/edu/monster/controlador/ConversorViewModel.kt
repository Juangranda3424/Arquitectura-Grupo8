package ec.edu.monster.controlador

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.monster.modelo.constantes.Constantes
import ec.edu.monster.servicio.Autenticador
import ec.edu.monster.servicio.Conversor
import kotlinx.coroutines.launch

class ConversorViewModel : ViewModel() {
    private val servicioConversor = Conversor()

    private val servicioAutenticador = Autenticador()

    // Estados observados por la Vista (Compose)
    var valorOrigenText by mutableStateOf("")
    var tipoSeleccionado by mutableStateOf("LONGITUD") // LONGITUD, MASA, TEMPERATURA
    var unidadOrigen by mutableStateOf("METRO")
    var unidadDestino by mutableStateOf("CENTIMETRO")

    var resultadoText by mutableStateOf("Ingrese los valores y presione Convertir")
    var estaCargando by mutableStateOf(false)
        // permite leer la variable desde fuera de la clase y solo modificarla desde dentro de esa clase
        private set

    fun onValorOrigenChange(nuevoValor: String) {
        valorOrigenText = nuevoValor
    }

    fun onTipoChange(nuevoTipo: String) {
        tipoSeleccionado = nuevoTipo

        //Obtener la lista de unidades asociadas
        val unidadesDisponibles = Constantes.obtenerUnidadesPorMagnitud(nuevoTipo);

        // Se asignan las primeras opciones por defecto
        unidadOrigen = unidadesDisponibles.getOrElse(0) {""}
        unidadDestino = unidadesDisponibles.getOrElse(1) {""}
    }

    fun convertirUnidades() {

        val valor = valorOrigenText.toDoubleOrNull()
        if (valor == null) {
            resultadoText = "Por favor ingrese un número válido"
            return
        }

        viewModelScope.launch {
            estaCargando = true
            resultadoText = "Procesando..."

            // Consumir el WebServicio de ConversionUnidades

            val respuesta = servicioConversor.realizarConversion(
                valor = valor,
                origen = unidadOrigen,
                destino = unidadDestino,
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
                val respuesta = servicioAutenticador.logout(Constantes.token)
                Log.i("LOGOUT", "Respuesta de cierre de sesión: $respuesta")
            } catch (e: Exception) {
                Log.e("LOGOUT", "Error al cerrar sesión: ${e.localizedMessage}")
            } finally {
                Constantes.token = ""
                estaCargando = false
                onTerminado()   // <- navega al login
            }
        }
    }
}