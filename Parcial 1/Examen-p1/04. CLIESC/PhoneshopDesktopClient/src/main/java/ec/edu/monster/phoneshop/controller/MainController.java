package ec.edu.monster.phoneshop.controller;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import ec.edu.monster.phoneshop.App;
import ec.edu.monster.phoneshop.common.ApplicationContext;
import ec.edu.monster.phoneshop.dto.*;
import ec.edu.monster.phoneshop.service.MainService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author Andres
 */
public class MainController implements Initializable {
    private final MainService mainService = new MainService();

    @FXML
    private TableView<AmortizationTableItemDto> amortizationTable;

    @FXML
    private TableView<ProductsTableItemDto> productsTable;

    @FXML
    private TextField checkCreditAmountField;

    @FXML
    private TextField productCodeField;

    @FXML
    private Spinner<Integer> totalMonthsSpinner = new Spinner<Integer>();

    @FXML
    private ComboBox<PurchaseMethod> paymentMethodComboBox = new ComboBox<PurchaseMethod>();

    private final HashMap<String, ProductDto> productsMap = new HashMap<>();

    public MainController() {}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Scene scene = App.getScene();

        // change size of the scene
        scene.getWindow().setWidth(1200);
        scene.getWindow().setHeight(650);

        ObservableList observableList = FXCollections.observableArrayList(PurchaseMethod.values());
        paymentMethodComboBox.getItems().setAll(observableList);
        paymentMethodComboBox.getSelectionModel().selectFirst();

        productsTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("code"));
        productsTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        productsTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("price"));

        amortizationTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("number"));
        amortizationTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("installmentValue"));
        amortizationTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("interest"));
        amortizationTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("capital"));
        amortizationTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("balance"));

        totalMonthsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 18, 1));

        loadProducts();
    }

    private void loadProducts() {
        List<ProductDto> products = mainService.getProducts();
        populateProductsTable(products);
    }

    private void populateProductsTable(List<ProductDto> products) {
        productsTable.getItems().clear();
        AtomicInteger code = new AtomicInteger(1);

        products.forEach(product -> {
            String codeStr = String.valueOf(code.getAndIncrement());
            productsMap.put(codeStr, product);
            productsTable.getItems().add(ProductsTableItemDto.builder()
                    .code(codeStr)
                    .name(product.getName())
                    .price(product.getPrice().setScale(2, BigDecimal.ROUND_HALF_UP).toString())
                    .build());
        });
    }

    @FXML
    public void calculateMaxCredit() {
        String text = checkCreditAmountField.getText();
        double value;

        if (text.isEmpty()) {
            showError("Ingresa un valor");
            return;
        }

        try {
            value = parseInputValue(text);
            checkCreditAmountField.setText(String.valueOf(value));
        } catch (Exception ex) {
            showError("El valor ingresado es incorrecto");
            return;
        }

        try {
            CheckCreditResultDto result = mainService.checkCredit(CheckCreditRequestDto.builder()
                    .amount(BigDecimal.valueOf(value))
                    .identificationNumber(ApplicationContext.getInstance().getIdentificationNumber())
                    .build());

            if (result.isEligible()) {
                showInfo("Tienes un crédito disponible de " + result.getMaxAmount().setScale(2, BigDecimal.ROUND_HALF_UP));
            } else {
                showError(result.getMessage().isBlank() ? "No eres elegible para un crédito" : result.getMessage());
            }

        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    @FXML
    public void purchse() {
        String productCode = productCodeField.getText();
        PurchaseMethod paymentMethod = paymentMethodComboBox.getValue();
        int totalMonths = totalMonthsSpinner.getValue();

        if (productCode.isEmpty() || paymentMethod == null) {
            showError("Todos los campos son requeridos");
            return;
        }

        if (!productsMap.containsKey(productCode)) {
            showError("El producto ingresado no existe");
            return;
        }

        ProductDto product = productsMap.get(productCode);

        try {
            PurchaseResultDto result = mainService.purchase(PurchaseRequestDto.builder()
                    .productId(product.getId())
                    .method(paymentMethod)
                    .months(totalMonths)
                    .identificationNumber(ApplicationContext.getInstance().getIdentificationNumber())
                    .build());

            if (result.getStatus() == ResponseStatus.SUCCESS) {
                showInfo("Compra realizada con éxito");

                if (paymentMethod == PurchaseMethod.CREDIT) {
                    populateAmortizationsTable(result.getCredit());
                }
            } else {
                showError(result.getMessage());
            }
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private void populateAmortizationsTable(CreditApplicationResponseDto credit) {
        amortizationTable.getItems().clear();

        credit.getInstallments().forEach(installment -> {
            amortizationTable.getItems().add(AmortizationTableItemDto.builder()
                    .balance(installment.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP).toString())
                    .interest(installment.getInterest().setScale(2, BigDecimal.ROUND_HALF_UP).toString())
                    .capital(installment.getCapital().setScale(2, BigDecimal.ROUND_HALF_UP).toString())
                    .installmentValue(installment.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString())
                    .number(String.format("%d", installment.getNumber()))
                    .build());
        });
    }

    private String normalizeInputValue(String value) {
        return value.replace(',', '.').trim();
    }

    private double parseInputValue(String value) throws Exception {
        try {
            double parsed = Double.parseDouble(value);
            return parsed;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("El valor ingresado es incorrecto");
        }
    }

    private void showError(String message) {
        showAlert(message, "Error", Alert.AlertType.ERROR);
    }

    private void showInfo(String message) {
        showAlert(message, "Información", Alert.AlertType.INFORMATION);
    }

    private void showAlert(String message, String title, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }
}
