package ec.edu.monster.climov;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

/**
 * Menu principal del cliente movil.
 */
public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        MaterialButton btnRegistrar = findViewById(R.id.btn_movimientos);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                startActivity(new Intent(Menu.this, Depositos.class));
            }
        });

        MaterialButton btnConsultar = findViewById(R.id.btn_deposito);
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                startActivity(new Intent(Menu.this, EurekabankControlador.class));
            }
        });
    }
}
