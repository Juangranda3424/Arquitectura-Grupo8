package ec.edu.monster.climov;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Locale;

import ec.edu.monster.climov.modelo.Movimiento;
import ec.edu.monster.climov.modelo.WSEurekabankCliente;

/**
 * Pantalla de consulta de movimientos de una cuenta.
 */
public class EurekabankControlador extends AppCompatActivity {

    private static final String[] ENCABEZADOS = {"Nro.", "Fecha", "Tipo", "Accion", "Importe"};

    private EditText editTextNumeroCuenta;
    private TableLayout tableLayoutMovimientos;
    private TextView tvMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eurekabank_controlador);

        editTextNumeroCuenta = findViewById(R.id.editTextNumeroCuenta);
        tableLayoutMovimientos = findViewById(R.id.tableLayoutMovimientos);
        tvMensaje = findViewById(R.id.tvMensajeMovimientos);

        MaterialButton btnBuscar = findViewById(R.id.btnBuscarMovimientos);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                buscarMovimientos();
            }
        });
    }

    private void buscarMovimientos() {
        final String cuenta = editTextNumeroCuenta.getText().toString().trim();
        if (cuenta.isEmpty()) {
            mostrarMensaje("Ingrese un número de cuenta.", true);
            return;
        }

        mostrarMensaje(getString(R.string.consultando), false);
        tableLayoutMovimientos.removeAllViews();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Movimiento> movimientos = new WSEurekabankCliente().traerMovimientos(cuenta);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (movimientos == null) {
                            mostrarMensaje(getString(R.string.login_error), true);
                            return;
                        }

                        if (movimientos.isEmpty()) {
                            mostrarMensaje(getString(R.string.sin_movimientos), false);
                            return;
                        }

                        dibujarTabla(movimientos);
                        mostrarMensaje("Se encontraron " + movimientos.size() + " movimientos.", false);
                    }
                });
            }
        }).start();
    }

    private void dibujarTabla(List<Movimiento> movimientos) {
        tableLayoutMovimientos.removeAllViews();

        TableRow cabecera = new TableRow(this);
        cabecera.setBackgroundColor(getResources().getColor(R.color.primary_navy, getTheme()));
        for (String titulo : ENCABEZADOS) {
            cabecera.addView(crearCelda(titulo, true));
        }
        tableLayoutMovimientos.addView(cabecera);

        for (Movimiento movimiento : movimientos) {
            TableRow fila = new TableRow(this);
            fila.setBackgroundColor(getResources().getColor(
                    movimientos.indexOf(movimiento) % 2 == 0 ? R.color.surface : R.color.background, getTheme()));
            fila.addView(crearCelda(String.valueOf(movimiento.getNromov()), false));
            fila.addView(crearCelda(movimiento.getFecha(), false));
            fila.addView(crearCelda(movimiento.getTipo(), false));
            fila.addView(crearCelda(movimiento.getAccion(), false));
            fila.addView(crearCelda(String.format(Locale.US, "%.2f", movimiento.getImporte()), false));
            tableLayoutMovimientos.addView(fila);
        }
    }

    private TextView crearCelda(String texto, boolean esCabecera) {
        TextView celda = new TextView(this);
        celda.setText(texto);
        celda.setTextSize(12f);
        celda.setPadding(8, 10, 8, 10);
        celda.setGravity(Gravity.START);

        if (esCabecera) {
            celda.setTextColor(getResources().getColor(R.color.white, getTheme()));
            celda.setTypeface(celda.getTypeface(), android.graphics.Typeface.BOLD);
        } else {
            celda.setTextColor(getResources().getColor(R.color.text, getTheme()));
        }

        return celda;
    }

    private void mostrarMensaje(String texto, boolean esError) {
        tvMensaje.setText(texto);
        tvMensaje.setTextColor(getResources().getColor(
                esError ? R.color.error : R.color.muted, getTheme()));
    }
}
