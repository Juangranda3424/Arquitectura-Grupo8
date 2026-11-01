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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ec.edu.monster.adapter.MovimientoAdapter;
import ec.edu.monster.model.MovimientoModel;
import ec.edu.monster.service.CuentaService;
import ec.edu.monster.service.MovimientoService;

import java.util.ArrayList;
import java.util.List;

public class MovimientosActivity extends AppCompatActivity {
    private static final String TAG = "MovimientosActivity";
    private EditText etCuenta;
    private Button btnConsultar;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private TextView tvSaldoActual, tvTotalIngresos, tvTotalEgresos, tvSaldoNeto;
    private MovimientoService movimientoService;
    private CuentaService cuentaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        try {
            movimientoService = new MovimientoService();
            cuentaService = new CuentaService();

            etCuenta = findViewById(R.id.etCuenta);
            btnConsultar = findViewById(R.id.btnConsultar);
            progressBar = findViewById(R.id.progressBar);
            recyclerView = findViewById(R.id.recyclerView);
            tvSaldoActual = findViewById(R.id.tvSaldoActual);
            tvTotalIngresos = findViewById(R.id.tvTotalIngresos);
            tvTotalEgresos = findViewById(R.id.tvTotalEgresos);
            tvSaldoNeto = findViewById(R.id.tvSaldoNeto);

            // Validar que los views no sean null
            if (etCuenta == null || btnConsultar == null || progressBar == null || 
                recyclerView == null || tvSaldoActual == null || tvTotalIngresos == null || 
                tvTotalEgresos == null || tvSaldoNeto == null) {
                Log.e(TAG, "Error: Algunos views son null");
                Toast.makeText(this, "Error al cargar la interfaz", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            btnConsultar.setOnClickListener(v -> {
                try {
                    String cuenta = etCuenta.getText().toString().trim();

                    if (cuenta.isEmpty()) {
                        Toast.makeText(MovimientosActivity.this, "Ingrese el número de cuenta", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new ConsultarMovimientosTask().execute(cuenta);
                } catch (Exception e) {
                    Log.e(TAG, "Error en onClickListener", e);
                    Toast.makeText(MovimientosActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error en onCreate", e);
            Toast.makeText(this, "Error al inicializar: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private class ConsultarMovimientosTask extends AsyncTask<String, Void, Result> {
        @Override
        protected void onPreExecute() {
            try {
                progressBar.setVisibility(View.VISIBLE);
                btnConsultar.setEnabled(false);
                etCuenta.setEnabled(false);
            } catch (Exception e) {
                Log.e(TAG, "Error en onPreExecute", e);
            }
        }

        @Override
        protected Result doInBackground(String... params) {
            try {
                String cuenta = params[0];
                Log.d(TAG, "Consultando movimientos para cuenta: " + cuenta);

                // Obtener movimientos
                MovimientoService.MovimientosResult movimientosResult = movimientoService.obtenerMovimientos(cuenta);
                List<MovimientoModel> movimientos = movimientosResult.success ? movimientosResult.movimientos : new ArrayList<>();

                // Obtener datos de cuenta
                CuentaService.CuentaResult cuentaResult = cuentaService.obtenerCuentaPorNumero(cuenta);
                double saldoActual = cuentaResult.success && cuentaResult.cuenta != null ? 
                        cuentaResult.cuenta.getDecCuenSaldo() : 0;

                // Calcular totales
                double totalIngresos = 0;
                double totalEgresos = 0;

                if (movimientos != null) {
                    for (MovimientoModel mov : movimientos) {
                        try {
                            String tipoDesc = mov.getTipoDescripcion();
                            double importe = mov.getImporteMovimiento();
                            
                            if (tipoDesc != null && !tipoDesc.equals("Retiro") && !tipoDesc.equalsIgnoreCase("retiro")) {
                                totalIngresos += importe;
                            } else {
                                totalEgresos += importe;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error al procesar movimiento", e);
                        }
                    }
                }

                // Si hay error en movimientos, incluir el mensaje
                String errorMessage = null;
                if (!movimientosResult.success) {
                    errorMessage = movimientosResult.errorMessage;
                } else if (!cuentaResult.success) {
                    errorMessage = cuentaResult.errorMessage;
                }

                return new Result(movimientos, saldoActual, totalIngresos, totalEgresos, errorMessage);
            } catch (Exception e) {
                Log.e(TAG, "Error en doInBackground", e);
                return new Result(new ArrayList<>(), 0, 0, 0, "Error al procesar la consulta: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            try {
                progressBar.setVisibility(View.GONE);
                btnConsultar.setEnabled(true);
                etCuenta.setEnabled(true);

                if (result == null) {
                    Log.e(TAG, "Result es null");
                    Toast.makeText(MovimientosActivity.this, "Error: No se recibió respuesta del servidor", Toast.LENGTH_LONG).show();
                    return;
                }

                // Mostrar error si existe
                if (result.errorMessage != null && !result.errorMessage.isEmpty()) {
                    Log.e(TAG, "Error en consulta: " + result.errorMessage);
                    Toast.makeText(MovimientosActivity.this, result.errorMessage, Toast.LENGTH_LONG).show();
                }

                // Actualizar UI con los datos
                try {
                    tvSaldoActual.setText(String.format("$%.2f", result.saldoActual));
                    tvTotalIngresos.setText(String.format("$%.2f", result.totalIngresos));
                    tvTotalEgresos.setText(String.format("$%.2f", result.totalEgresos));
                    // Saldo neto = ingresos - egresos
                    double saldoNeto = result.totalIngresos - result.totalEgresos;
                    tvSaldoNeto.setText(String.format("$%.2f", saldoNeto));

                    // Configurar adapter
                    if (result.movimientos != null && !result.movimientos.isEmpty()) {
                        MovimientoAdapter adapter = new MovimientoAdapter(result.movimientos);
                        recyclerView.setAdapter(adapter);
                        Log.d(TAG, "Movimientos cargados: " + result.movimientos.size());
                    } else {
                        recyclerView.setAdapter(new MovimientoAdapter(new ArrayList<>()));
                        if (result.errorMessage == null || result.errorMessage.isEmpty()) {
                            Toast.makeText(MovimientosActivity.this, "No se encontraron movimientos para esta cuenta", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error al actualizar UI", e);
                    Toast.makeText(MovimientosActivity.this, "Error al mostrar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error en onPostExecute", e);
                Toast.makeText(MovimientosActivity.this, "Error al procesar la respuesta: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private static class Result {
        List<MovimientoModel> movimientos;
        double saldoActual;
        double totalIngresos;
        double totalEgresos;
        String errorMessage;

        Result(List<MovimientoModel> movimientos, double saldoActual, double totalIngresos, double totalEgresos, String errorMessage) {
            this.movimientos = movimientos != null ? movimientos : new ArrayList<>();
            this.saldoActual = saldoActual;
            this.totalIngresos = totalIngresos;
            this.totalEgresos = totalEgresos;
            this.errorMessage = errorMessage;
        }
    }
}

