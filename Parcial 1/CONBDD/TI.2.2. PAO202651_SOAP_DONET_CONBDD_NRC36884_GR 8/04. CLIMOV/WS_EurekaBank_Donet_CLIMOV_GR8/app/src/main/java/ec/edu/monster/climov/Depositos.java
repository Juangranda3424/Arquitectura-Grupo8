package ec.edu.monster.climov;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import ec.edu.monster.climov.modelo.ConfiguracionSoap;
import ec.edu.monster.climov.modelo.WSEurekabankCliente;

/**
 * Pantalla para registrar depositos, retiros y transferencias.
 */
public class Depositos extends AppCompatActivity {

    private EditText editCuenta;
    private EditText editMonto;
    private EditText editCuentaDestino;
    private LinearLayout grupoCuentaDestino;
    private TextView tvMensaje;
    private Spinner spinnerTipoMovimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depositos);

        editCuenta = findViewById(R.id.editCuenta);
        editMonto = findViewById(R.id.editMonto);
        editCuentaDestino = findViewById(R.id.editCuentaDestino);
        grupoCuentaDestino = findViewById(R.id.grupoCuentaDestino);
        tvMensaje = findViewById(R.id.tvMensajeDeposito);
        spinnerTipoMovimiento = findViewById(R.id.spinnerTipoMovimiento);

        ArrayAdapter<CharSequence> adaptador = ArrayAdapter.createFromResource(
                this, R.array.tipo_movimiento, android.R.layout.simple_spinner_item);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoMovimiento.setAdapter(adaptador);
        spinnerTipoMovimiento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> padre, View vista, int posicion, long id) {
                boolean esTransferencia = posicion == 2;
                grupoCuentaDestino.setVisibility(esTransferencia ? View.VISIBLE : View.GONE);
                if (!esTransferencia) {
                    editCuentaDestino.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> padre) {
                grupoCuentaDestino.setVisibility(View.GONE);
            }
        });

        MaterialButton btnRegistrar = findViewById(R.id.btnRealizarDeposito);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vista) {
                registrarMovimiento();
            }
        });
    }

    private void registrarMovimiento() {
        final String cuenta = editCuenta.getText().toString().trim();
        final String monto = editMonto.getText().toString().trim();
        final String cuentaDestino = editCuentaDestino.getText().toString().trim();
        final int posicion = spinnerTipoMovimiento.getSelectedItemPosition();

        if (cuenta.isEmpty() || monto.isEmpty()) {
            mostrarMensaje("Ingrese la cuenta y el monto.", true);
            return;
        }

        if (posicion == 2 && cuentaDestino.isEmpty()) {
            mostrarMensaje("La transferencia requiere una cuenta destino.", true);
            return;
        }

        final String tipoMovimiento;
        switch (posicion) {
            case 1:
                tipoMovimiento = ConfiguracionSoap.TIPO_RETIRO;
                break;
            case 2:
                tipoMovimiento = ConfiguracionSoap.TIPO_TRANSFERENCIA;
                break;
            case 0:
            default:
                tipoMovimiento = ConfiguracionSoap.TIPO_DEPOSITO;
                break;
        }

        mostrarMensaje("Enviando movimiento...", false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String resultado = new WSEurekabankCliente()
                        .regDeposito(cuenta, monto, tipoMovimiento, cuentaDestino);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if ("1".equals(resultado)) {
                            mostrarMensaje(getString(R.string.movimiento_ok), false);
                            editMonto.setText("");
                            editCuentaDestino.setText("");
                        } else if ("-1".equals(resultado)) {
                            mostrarMensaje(getString(R.string.movimiento_error), true);
                        } else {
                            mostrarMensaje(resultado, true);
                        }
                    }
                });
            }
        }).start();
    }

    private void mostrarMensaje(String texto, boolean esError) {
        tvMensaje.setText(texto);
        tvMensaje.setTextColor(getResources().getColor(
                esError ? R.color.error : R.color.success, getTheme()));
    }
}
