package ec.edu.monster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ec.edu.monster.ui.theme.WS_ConversionUnidades_Java_CLIMOV_GR8Theme
import ec.edu.monster.vista.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WS_ConversionUnidades_Java_CLIMOV_GR8Theme {
                AppNavigation()
            }
        }
    }
}