package ec.edu.monster;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "MainActivity onCreate iniciado");
        
        try {
            // Cargar el layout
            Log.d(TAG, "Cargando layout activity_main");
            setContentView(R.layout.activity_main);
            Log.d(TAG, "Layout cargado exitosamente");
            
            // Obtener el username del intent
            Intent receivedIntent = getIntent();
            if (receivedIntent != null) {
                username = receivedIntent.getStringExtra("username");
                Log.d(TAG, "Username recibido del intent: " + username);
            } else {
                Log.e(TAG, "Intent es null");
            }
            
            // Validar que el username no sea null
            if (username == null || username.isEmpty()) {
                Log.e(TAG, "Username es null o vacío, redirigiendo a login");
                Toast.makeText(this, "Sesión inválida", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
                return;
            }

            Log.d(TAG, "Usuario logueado: " + username);

            // Obtener referencias a los views
            Log.d(TAG, "Buscando referencias de views...");
            TextView tvWelcome = findViewById(R.id.tvWelcome);
            TextView tvUsername = findViewById(R.id.tvUsername);
            CardView cardDeposito = findViewById(R.id.cardDeposito);
            CardView cardRetiro = findViewById(R.id.cardRetiro);
            CardView cardTransferencia = findViewById(R.id.cardTransferencia);
            CardView cardMovimientos = findViewById(R.id.cardMovimientos);
            CardView cardDatosCuenta = findViewById(R.id.cardDatosCuenta);
            Button btnLogout = findViewById(R.id.btnLogout);
            
            Log.d(TAG, "Views encontrados - tvWelcome: " + (tvWelcome != null) + 
                      ", tvUsername: " + (tvUsername != null) + 
                      ", btnLogout: " + (btnLogout != null));

            // Validar que los views no sean null
            if (tvWelcome == null || tvUsername == null || btnLogout == null) {
                Log.e(TAG, "Error: Algunos views son null");
                Toast.makeText(this, "Error al cargar la interfaz", Toast.LENGTH_SHORT).show();
                return;
            }

            // Actualizar los textos con el nombre de usuario
            Log.d(TAG, "Actualizando textos de bienvenida...");
            tvWelcome.setText("Bienvenido, " + username);
            tvUsername.setText(username);
            Log.d(TAG, "Textos actualizados exitosamente");

            // Configurar listeners de los cards
            if (cardDeposito != null) {
                cardDeposito.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, DepositoActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                });
            }

            if (cardRetiro != null) {
                cardRetiro.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, RetiroActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                });
            }

            if (cardTransferencia != null) {
                cardTransferencia.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, TransferenciaActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                });
            }

            if (cardMovimientos != null) {
                cardMovimientos.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, MovimientosActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                });
            }

            if (cardDatosCuenta != null) {
                cardDatosCuenta.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, DatosCuentaActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                });
            }

            // Configurar listener del botón de logout
            btnLogout.setOnClickListener(v -> {
                Log.d(TAG, "Botón logout presionado");
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
            
            Log.d(TAG, "MainActivity onCreate completado exitosamente");

        } catch (Exception e) {
            Log.e(TAG, "Error crítico en onCreate", e);
            e.printStackTrace();
            
            // Intentar mostrar un mensaje de error
            try {
                Toast.makeText(this, "Error al cargar la aplicación: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception toastError) {
                Log.e(TAG, "No se pudo mostrar el Toast", toastError);
            }
            
            // En caso de error, redirigir al login
            try {
                Intent loginIntent = new Intent(this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(loginIntent);
                finish();
            } catch (Exception intentError) {
                Log.e(TAG, "Error al redirigir al login", intentError);
                // Si no se puede redirigir, al menos cerrar la actividad
                finish();
            }
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "MainActivity onStart");
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume");
    }
}

