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

public class DepositoActivity extends AppCompatActivity {
    private static final String TAG = "DepositoActivity";
    private EditText etCuenta, etMonto;
    private Button btnRealizar;
    private ProgressBar progressBar;
    private CuentaService cuentaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposito);

        try {
            cuentaService = new CuentaService();

            etCuenta = findViewById(R.id.etCuenta);
            etMonto = findViewById(R.id.etMonto);
            btnRealizar = findViewById(R.id.btnRealizar);
            progressBar = findViewById(R.id.progressBar);

            // Validar que los views no sean null
            if (etCuenta == null || etMonto == null || btnRealizar == null || progressBar == null) {
                Log.e(TAG, "Error: Algunos views son null");
                Toast.makeText(this, "Error al cargar la interfaz", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            btnRealizar.setOnClickListener(v -> {
                try {
                    String cuenta = etCuenta.getText().toString().trim();
                    String montoStr = etMonto.getText().toString().trim();

                    if (cuenta.isEmpty() || montoStr.isEmpty()) {
                        Toast.makeText(DepositoActivity.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        double monto = Double.parseDouble(montoStr);
                        if (monto <= 0) {
                            Toast.makeText(DepositoActivity.this, "El monto debe ser mayor a cero", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new DepositoTask().execute(cuenta, montoStr);
                    } catch (NumberFormatException e) {
                        Toast.makeText(DepositoActivity.this, "Ingrese un monto válido", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error en onClickListener", e);
                    Toast.makeText(DepositoActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate", e);
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class DepositoTask extends AsyncTask<String, Void, CuentaService.OperacionResult> {
        @Override
        protected void onPreExecute() {
            try {
                progressBar.setVisibility(View.VISIBLE);
                btnRealizar.setEnabled(false);
                etCuenta.setEnabled(false);
                etMonto.setEnabled(false);
            } catch (Exception e) {
                Log.e(TAG, "Error en onPreExecute", e);
            }
        }

        @Override
        protected CuentaService.OperacionResult doInBackground(String... params) {
            try {
                String cuenta = params[0];
                double monto = Double.parseDouble(params[1]);
                Log.d(TAG, "Ejecutando depósito - Cuenta: " + cuenta + ", Monto: " + monto);
                return cuentaService.realizarDeposito(cuenta, monto);
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
                etCuenta.setEnabled(true);
                etMonto.setEnabled(true);

                if (result == null) {
                    Log.e(TAG, "Result es null");
                    Toast.makeText(DepositoActivity.this, "Error: No se recibió respuesta del servidor", Toast.LENGTH_LONG).show();
                    return;
                }

                if (result.success && result.response != null) {
                    // Operación exitosa
                    String mensaje = result.response.getMensaje();
                    if (mensaje == null || mensaje.isEmpty()) {
                        mensaje = "Depósito realizado exitosamente";
                    }
                    
                    double saldo = result.response.getSaldo();
                    String mensajeCompleto = mensaje + "\nSaldo actual: $" + String.format("%.2f", saldo);
                    
                    Log.d(TAG, "Depósito exitoso: " + mensajeCompleto);
                    Toast.makeText(DepositoActivity.this, mensajeCompleto, Toast.LENGTH_LONG).show();
                    
                    // Limpiar campos
                    etCuenta.setText("");
                    etMonto.setText("");
                } else {
                    // Error en la operación
                    String errorMsg = result.errorMessage;
                    if (errorMsg == null || errorMsg.isEmpty()) {
                        errorMsg = "Error al realizar el depósito";
                    }
                    
                    Log.e(TAG, "Error en depósito: " + errorMsg);
                    Toast.makeText(DepositoActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error en onPostExecute", e);
                Toast.makeText(DepositoActivity.this, "Error al procesar la respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}

