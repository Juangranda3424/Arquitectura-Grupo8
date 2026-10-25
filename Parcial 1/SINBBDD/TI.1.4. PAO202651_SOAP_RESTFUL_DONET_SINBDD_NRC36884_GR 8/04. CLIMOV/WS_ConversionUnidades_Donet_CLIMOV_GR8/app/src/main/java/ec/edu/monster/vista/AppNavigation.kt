package ec.edu.monster.vista

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Definimos los nombres de las pantallas como constantes
object Rutas {
    const val LOGIN = "login"
    const val CONVERSOR = "conversor"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN // Pantalla con la que arranca la app
    ) {
        // Pantalla 1: Login
        composable(Rutas.LOGIN) {
            LoginVista(
                onLoginExitoso = {
                    // Navega a conversor y borra Login del historial
                    navController.navigate(Rutas.CONVERSOR) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla 2: Conversor
        composable(Rutas.CONVERSOR) {
            ConversorVista(
                onLogout = {
                    // Vuelve a Login y ELIMINA todas las pantallas previas del historial
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true } // <--- Borra todo el historial hasta la raíz
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}