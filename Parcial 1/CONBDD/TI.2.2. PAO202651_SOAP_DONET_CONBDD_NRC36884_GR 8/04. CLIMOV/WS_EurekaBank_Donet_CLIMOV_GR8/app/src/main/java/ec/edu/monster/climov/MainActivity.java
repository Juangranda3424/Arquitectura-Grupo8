package ec.edu.monster.climov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import ec.edu.monster.climov.modelo.WSLoginClient;

/**
 * Pantalla de inicio de sesion del cliente movil.
 */
public class MainActivity extends AppCompatActivity {

    private EditText etUsuario;
    private EditText etContrasena;
    private TextView tvMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsuario = findViewById(R.id.et_usuario);
        etContrasena = findViewById(R.id.et_contrasena);
        tvMensaje = findViewById(R.id.tvMensaje);

        MaterialButton btnIngresar = findViewById(R.id.btn_ingresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                iniciarSesion();
            }
        });
    }

    private void iniciarSesion() {
        final String usuario = etUsuario.getText().toString().trim();
        final String contrasena = etContrasena.getText().toString();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            Toast.makeText(this, "Ingrese usuario y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        mostrarMensaje(getString(R.string.validando), false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resultado = new WSLoginClient().login(usuario, contrasena);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(resultado)) {
                            Toast.makeText(MainActivity.this,
                                    getString(R.string.bienvenido) + ": " + usuario,
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, Menu.class));
                            finish();
                        } else if ("-1".equals(resultado)) {
                            mostrarMensaje(getString(R.string.login_fallido), true);
                        } else {
                            mostrarMensaje(getString(R.string.login_error) + " " + resultado, true);
                        }
                    }
                });
            }
        }).start();
    }

    private void mostrarMensaje(String texto, boolean esError) {
        tvMensaje.setText(texto);
        tvMensaje.setTextColor(getResources().getColor(
                esError ? R.color.error : R.color.muted, getTheme()));
    }
}
