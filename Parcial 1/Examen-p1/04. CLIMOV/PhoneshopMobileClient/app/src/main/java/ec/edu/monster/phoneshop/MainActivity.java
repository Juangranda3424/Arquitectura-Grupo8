package ec.edu.monster.phoneshop;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.TableView;
import com.google.android.material.snackbar.Snackbar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ec.edu.monster.phoneshop.common.ApplicationContext;
import ec.edu.monster.phoneshop.common.MovementsListAdapter;
import ec.edu.monster.phoneshop.dto.MovementDto;
import ec.edu.monster.phoneshop.dto.MovementType;
import ec.edu.monster.phoneshop.dto.ProductDto;
import ec.edu.monster.phoneshop.dto.TransactionDto;
import ec.edu.monster.phoneshop.service.MainService;

public class MainActivity extends AppCompatActivity {
    private ApplicationContext applicationContext = ApplicationContext.getInstance();
    private MainService mainService = new MainService();

    private Spinner movementTypeSpinner;
    private EditText amountField;
    private EditText targetBankAccountField;
    private RecyclerView movementsListView;
    private Button processButton;
    private TableView productsTableView;
    private final MovementsListAdapter movementsListAdapter = new MovementsListAdapter(new ArrayList<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productsTableView = findViewById(R.id.productsTableView);
        movementTypeSpinner = findViewById(R.id.movementTypeSpinner);
        amountField = findViewById(R.id.amountField);
        targetBankAccountField = findViewById(R.id.targetBankAccountField);
        movementTypeSpinner.setAdapter(new ArrayAdapter<MovementType>(this, android.R.layout.simple_spinner_item, MovementType.values()));
        movementsListView = findViewById(R.id.movementsRecyclerView);
        processButton = findViewById(R.id.processButton);
        movementsListView.setLayoutManager(new LinearLayoutManager(this));
        movementsListView.setAdapter(movementsListAdapter);

        loadProducts();

        processButton.setOnClickListener(v -> {
            if (amountField.getText().toString().isEmpty()) {
                amountField.setError("Campo requerido");
                amountField.requestFocus();
                return;
            }

            try {
                Double.parseDouble(amountField.getText().toString());
            } catch (NumberFormatException ex) {
                amountField.setError("Debe ser un número válido");
                amountField.requestFocus();
                return;
            }

            if (Double.parseDouble(amountField.getText().toString()) <= 0) {
                amountField.setError("Debe ser un número positivo");
                amountField.requestFocus();
                return;
            }

            if (targetBankAccountField.getText().toString().isEmpty()) {
                targetBankAccountField.setError("Campo requerido");
                targetBankAccountField.requestFocus();
                return;
            }

            if (movementTypeSpinner.getSelectedItem() == null) {
                Snackbar.make(findViewById(R.id.main), "Campo requerido", Snackbar.LENGTH_LONG).show();
                return;
            }

            makeTransaction();
        });
    }

    private void makeTransaction() {
        MovementType movementType = (MovementType) movementTypeSpinner.getSelectedItem();
        double amount = Double.parseDouble(amountField.getText().toString());
        String targetBankAccount = targetBankAccountField.getText().toString();

        new Thread(() -> {
            try {
                mainService.transfer(TransactionDto.builder()
                        .amount(BigDecimal.valueOf(amount))
                        .bankAccountReference(targetBankAccount)
                        .type(movementType)
                        .build());
                List<MovementDto> movements = mainService.getCurrentUserMovements();

                runOnUiThread(() -> {
                    movementsListAdapter.setMovements(movements);
                    Snackbar.make(findViewById(R.id.main), "Transacción realizada con éxito", Snackbar.LENGTH_LONG).show();

                    amountField.setText("");
                    targetBankAccountField.setText("");
                    movementTypeSpinner.setSelection(0);
                });
            } catch (Exception ex) {
                ex.printStackTrace();

                runOnUiThread(() -> {
                    Snackbar.make(findViewById(R.id.main), "Error al procesar la transacción", Snackbar.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private void loadProducts() {
        new Thread(() -> {
            List<ProductDto> products = mainService.getProducts();

            runOnUiThread(() -> {
            });
        }).start();
    }
}