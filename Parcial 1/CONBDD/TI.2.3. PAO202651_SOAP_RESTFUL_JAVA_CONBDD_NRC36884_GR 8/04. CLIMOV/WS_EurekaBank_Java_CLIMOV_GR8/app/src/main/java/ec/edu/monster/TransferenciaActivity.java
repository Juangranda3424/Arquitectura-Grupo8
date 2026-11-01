package ec.edu.monster;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ec.edu.monster.service.CuentaService;

public class TransferenciaActivity extends AppCompatActivity {
    private static final String TAG = "TransferenciaActivity";
    private EditText etCuentaOrigen, etCuentaDestino, etMonto;
    private Button btnRealizar;
    private ProgressBar progressBar;
    private CuentaService cuentaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia);

        try {
            cuentaService = new CuentaService();

            etCuentaOrigen = findViewById(R.id.etCuentaOrigen);
            etCuentaDestino = findViewById(R.id.etCuentaDestino);
            etMonto = findViewById(R.id.etMonto);
            btnRealizar = findViewById(R.id.btnRealizar);
            progressBar = findViewById(R.id.progressBar);

            // Validar que los views no sean null
            if (etCuentaOrigen == null || etCuentaDestino == null || etMonto == null || 
                btnRealizar == null || progressBar == null) {
                Log.e(TAG, "Error: Algunos views son null");
                Toast.makeText(this, "Error al cargar la interfaz", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            btnRealizar.setOnClickListener(v -> {
                try {
                    String cuentaOrigen = etCuentaOrigen.getText().toString().trim();
                    String cuentaDestino = etCuentaDestino.getText().toString().trim();
                    String montoStr = etMonto.getText().toString().trim();

                    if (cuentaOrigen.isEmpty() || cuentaDestino.isEmpty() || montoStr.isEmpty()) {
                        Toast.makeText(TransferenciaActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (cuentaOrigen.equals(cuentaDestino)) {
                        Toast.makeText(TransferenciaActivity.this, "La cuenta origen y destino no pueden ser iguales", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double monto = Double.parseDouble(montoStr);
                        if (monto <= 0) {
                            Toast.makeText(TransferenciaActivity.this, "El monto debe ser mayor a cero", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new TransferenciaTask().execute(cuentaOrigen, cuentaDestino, montoStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(TransferenciaActivity.this, "Ingrese un monto válido", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error en onClickListener", e);
                    Toast.makeText(TransferenciaActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate", e);
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class TransferenciaTask extends AsyncTask<String, Void, CuentaService.OperacionResult> {
        @Override
        protected void onPreExecute() {
            try {
                progressBar.setVisibility(View.VISIBLE);
                btnRealizar.setEnabled(false);
                etCuentaOrigen.setEnabled(false);
                etCuentaDestino.setEnabled(false);
                etMonto.setEnabled(false);
            } catch (Exception e) {
                Log.e(TAG, "Error en onPreExecute", e);
            }
        }

        @Override
        protected CuentaService.OperacionResult doInBackground(String... params) {
            try {
                String cuentaOrigen = params[0];
                String cuentaDestino = params[1];
                double monto = Double.parseDouble(params[2]);
                Log.d(TAG, "Ejecutando transferencia - Origen: " + cuentaOrigen + ", Destino: " + cuentaDestino + ", Monto: " + monto);
                return cuentaService.realizarTransferencia(cuentaOrigen, cuentaDestino, monto);
            } catch (Exception e) {
                Log.e(TAG, "Error en doInBackground", e);
                return CuentaService.OperacionResult.error("Error al procesar la operación: " + e.getMessage(), 
                        CuentaService.OperacionErrorType.UNKNOWN_ERROR);
            }
        }

        @Override
        protected void onPostExecute(CuentaService.OperacionResult result) {
            try {
                progressBar.setVisibility(View.GONE);
                btnRealizar.setEnabled(true);
                etCuentaOrigen.setEnabled(true);
                etCuentaDestino.setEnabled(true);
                etMonto.setEnabled(true);

                if (result == null) {
                    Log.e(TAG, "Result es null");
                    Toast.makeText(TransferenciaActivity.this, "Error: No se recibió respuesta del servidor", Toast.LENGTH_LONG).show();
                    return;
                }

                if (result.success && result.response != null) {
                    // Operación exitosa
                    String mensaje = result.response.getMensaje();
                    if (mensaje == null || mensaje.isEmpty()) {
                        mensaje = "Transferencia realizada exitosamente";
                    }
                    
                    double saldo = result.response.getSaldo();
                    String mensajeCompleto = mensaje + "\nSaldo actual: $" + String.format("%.2f", saldo);
                    
                    Log.d(TAG, "Transferencia exitosa: " + mensajeCompleto);
                    Toast.makeText(TransferenciaActivity.this, mensajeCompleto, Toast.LENGTH_LONG).show();
                    
                    // Limpiar campos
                    etCuentaOrigen.setText("");
                    etCuentaDestino.setText("");
                    etMonto.setText("");
                } else {
                    // Error en la operación
                    String errorMsg = result.errorMessage;
                    if (errorMsg == null || errorMsg.isEmpty()) {
                        errorMsg = "Error al realizar la transferencia";
                    }
                    
                    Log.e(TAG, "Error en transferencia: " + errorMsg);
                    Toast.makeText(TransferenciaActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error en onPostExecute", e);
                Toast.makeText(TransferenciaActivity.this, "Error al procesar la respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}

