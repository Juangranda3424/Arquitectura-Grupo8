package ec.edu.monster.phoneshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import ec.edu.monster.phoneshop.common.ApplicationContext;
import ec.edu.monster.phoneshop.dto.AuthCredentialsDto;
import ec.edu.monster.phoneshop.dto.AuthResponseDto;
import ec.edu.monster.phoneshop.dto.ResponseStatus;
import ec.edu.monster.phoneshop.service.AuthService;

public class LoginActivity extends AppCompatActivity {
    private final ApplicationContext applicationContext = ApplicationContext.getInstance();
    private final AuthService authService = new AuthService();
    private EditText usernameField;
    private EditText passwordField;
    private EditText serverIpField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        serverIpField = findViewById(R.id.serverIpField);
        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();
            String serverIp = serverIpField.getText().toString();

            if (username.isEmpty()) {
                usernameField.setError("Campo requerido");
                usernameField.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                passwordField.setError("Campo requerido");
                passwordField.requestFocus();
                return;
            }

            if (serverIp.isEmpty()) {
                serverIpField.setError("Campo requerido");
                serverIpField.requestFocus();
                return;
            }

            login(username, password, serverIp);
        });
    }

    private void login(String username, String password, String serverIp) {
        String serverPort = "9001";

        if (serverIp.contains(":")) {
            String[] parts = serverIp.split(":");
            serverIp = parts[0];
            serverPort = parts[1];
        }

        applicationContext.setServerIp(serverIp);
        applicationContext.setServerPort(serverPort);

        Thread thread = new Thread(() -> {
            try {
                AuthResponseDto response = authService.login(AuthCredentialsDto.builder()
                        .username(username).password(password).build());

                if (response.getStatus() == ResponseStatus.SUCCESS) {
                    runOnUiThread(() -> {
                        applicationContext.setIdentificationNumber(response.getUser().getIdentificationNumber());
                        Snackbar.make(findViewById(R.id.main), "Logueado correctamente", Snackbar.LENGTH_LONG).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    });
                } else {
                    runOnUiThread(() -> {
                        Snackbar.make(findViewById(R.id.main), response.getMessage(), Snackbar.LENGTH_LONG).show();
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                runOnUiThread(() -> {
                    Snackbar.make(findViewById(R.id.main), "Error al intentar loguearse", Snackbar.LENGTH_LONG).show();
                });
            }
        });

        thread.start();
    }
}