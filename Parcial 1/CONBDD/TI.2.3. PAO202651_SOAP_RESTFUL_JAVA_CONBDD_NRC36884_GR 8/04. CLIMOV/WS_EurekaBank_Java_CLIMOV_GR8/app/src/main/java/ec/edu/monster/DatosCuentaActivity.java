package ec.edu.monster;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import ec.edu.monster.service.CuentaService;

public class DatosCuentaActivity extends AppCompatActivity {
    private static final String TAG = "DatosCuentaActivity";
    private EditText etCuenta;
    private Button btnConsultar;
    private ProgressBar progressBar;
    private CardView cardDatos;
    private TextView tvSaldo, tvContador;
    private CuentaService cuentaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_cuenta);

        try {
            cuentaService = new CuentaService();

            etCuenta = findViewById(R.id.etCuenta);
            btnConsultar = findViewById(R.id.btnConsultar);
            progressBar = findViewById(R.id.progressBar);
            cardDatos = findViewById(R.id.cardDatos);
            tvSaldo = findViewById(R.id.tvSaldo);
            tvContador = findViewById(R.id.tvContador);

            // Validar que los views no sean null
            if (etCuenta == null || btnConsultar == null || progressBar == null || 
                cardDatos == null || tvSaldo == null || tvContador == null) {
                Log.e(TAG, "Error: Algunos views son null");
                Toast.makeText(this, "Error al cargar la interfaz", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            btnConsultar.setOnClickListener(v -> {
                try {
                    String cuenta = etCuenta.getText().toString().trim();

                    if (cuenta.isEmpty()) {
                        Toast.makeText(DatosCuentaActivity.this, "Ingrese el número de cuenta", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new ConsultarCuentaTask().execute(cuenta);
                } catch (Exception e) {
                    Log.e(TAG, "Error en onClickListener", e);
                    Toast.makeText(DatosCuentaActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate", e);
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class ConsultarCuentaTask extends AsyncTask<String, Void, CuentaService.CuentaResult> {
        @Override
        protected void onPreExecute() {
            try {
                progressBar.setVisibility(View.VISIBLE);
                btnConsultar.setEnabled(false);
                etCuenta.setEnabled(false);
                cardDatos.setVisibility(View.GONE);
            } catch (Exception e) {
                Log.e(TAG, "Error en onPreExecute", e);
            }
        }

        @Override
        protected CuentaService.CuentaResult doInBackground(String... params) {
            try {
                String cuenta = params[0];
                Log.d(TAG, "Consultando cuenta: " + cuenta);
                return cuentaService.obtenerCuentaPorNumero(cuenta);
            } catch (Exception e) {
                Log.e(TAG, "Error en doInBackground", e);
                return CuentaService.CuentaResult.error("Error al procesar la consulta: " + e.getMessage(), 
                        CuentaService.OperacionErrorType.UNKNOWN_ERROR);
            }
        }

        @Override
        protected void onPostExecute(CuentaService.CuentaResult result) {
            try {
                progressBar.setVisibility(View.GONE);
                btnConsultar.setEnabled(true);
                etCuenta.setEnabled(true);

                if (result == null) {
                    Log.e(TAG, "Result es null");
                    Toast.makeText(DatosCuentaActivity.this, "Error: No se recibió respuesta del servidor", Toast.LENGTH_LONG).show();
                    cardDatos.setVisibility(View.GONE);
                    return;
                }

                if (result.success && result.cuenta != null) {
                    // Consulta exitosa
                    try {
                        double saldo = result.cuenta.getDecCuenSaldo();
                        int contador = result.cuenta.getIntCuenContMov();

                        tvSaldo.setText(String.format("$%.2f", saldo));
                        tvContador.setText(String.valueOf(contador));
                        cardDatos.setVisibility(View.VISIBLE);

                        Log.d(TAG, "Datos de cuenta cargados - Saldo: " + saldo + ", Contador: " + contador);
                        Toast.makeText(DatosCuentaActivity.this, "Consulta exitosa", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(TAG, "Error al mostrar datos", e);
                        Toast.makeText(DatosCuentaActivity.this, "Error al mostrar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        cardDatos.setVisibility(View.GONE);
                    }
                } else {
                    // Error en la consulta
                    String errorMsg = result.errorMessage;
                    if (errorMsg == null || errorMsg.isEmpty()) {
                        errorMsg = "No se encontraron datos para la cuenta";
                    }

                    Log.e(TAG, "Error en consulta: " + errorMsg);
                    Toast.makeText(DatosCuentaActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    cardDatos.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error en onPostExecute", e);
                Toast.makeText(DatosCuentaActivity.this, "Error al procesar la respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
                cardDatos.setVisibility(View.GONE);
            }
        }
    }
}

