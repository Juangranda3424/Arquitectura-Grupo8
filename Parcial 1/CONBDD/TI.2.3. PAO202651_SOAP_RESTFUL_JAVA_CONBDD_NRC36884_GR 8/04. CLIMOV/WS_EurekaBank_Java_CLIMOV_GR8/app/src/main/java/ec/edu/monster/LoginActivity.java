package ec.edu.monster;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import ec.edu.monster.service.LoginService;
import ec.edu.monster.util.ApiConfig;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private CardView cardView;
    private LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginService = new LoginService();
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        cardView = findViewById(R.id.cardView);

        // Mostrar información de conexión en logs
        Log.d(TAG, "URL del servidor: " + ApiConfig.getCurrentBaseUrl());
        Log.d(TAG, "Dispositivo: " + (isEmulator() ? "Emulador" : "Dispositivo físico"));

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Verificar conexión a Internet antes de intentar login
            if (!isNetworkAvailable()) {
                showErrorDialog("Sin conexión a Internet", 
                    "No hay conexión a Internet disponible. Por favor, verifica tu conexión WiFi o datos móviles.");
                return;
            }

            new LoginTask().execute(username, password);
        });
    }

    /**
     * Verifica si hay conexión a Internet disponible
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    /**
     * Detecta si está corriendo en emulador
     */
    private boolean isEmulator() {
        String fingerprint = android.os.Build.FINGERPRINT;
        return fingerprint.startsWith("generic")
                || fingerprint.startsWith("unknown")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86");
    }

    /**
     * Muestra un diálogo de error con detalles
     */
    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private class LoginTask extends AsyncTask<String, Void, LoginService.AuthResult> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            etUsername.setEnabled(false);
            etPassword.setEnabled(false);
        }

        @Override
        protected LoginService.AuthResult doInBackground(String... params) {
            String username = params[0];
            String password = params[1];

            return loginService.auth(username, password);
        }

        @Override
        protected void onPostExecute(LoginService.AuthResult result) {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            etUsername.setEnabled(true);
            etPassword.setEnabled(true);

            if (result.success) {
                try {
                    String username = etUsername.getText().toString();
                    Log.d(TAG, "Login exitoso para usuario: " + username);
                    Log.d(TAG, "Iniciando MainActivity...");
                    
                    // Crear intent y pasar el username
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    
                    // Iniciar MainActivity
                    startActivity(intent);
                    Log.d(TAG, "MainActivity iniciada, cerrando LoginActivity");
                    
                    // Cerrar LoginActivity
                    finish();
                    
                } catch (Exception e) {
                    Log.e(TAG, "Error crítico al iniciar MainActivity", e);
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error al iniciar la aplicación. Por favor, intente nuevamente.", Toast.LENGTH_LONG).show();
                }
            } else {
                // Mostrar error específico según el tipo
                switch (result.errorType) {
                    case INVALID_CREDENTIALS:
                        Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_LONG).show();
                        break;
                    case CONNECTION_ERROR:
                    case SERVER_ERROR:
                    case UNKNOWN_ERROR:
                        // Mostrar diálogo con detalles del error de conexión
                        showErrorDialog("Error de Conexión", result.errorMessage);
                        break;
                }
            }
        }
    }
}

