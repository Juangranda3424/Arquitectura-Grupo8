package ec.edu.monster.phoneshop.controller;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ec.edu.monster.phoneshop.common.ApplicationContext;
import ec.edu.monster.phoneshop.common.CommandLine;
import ec.edu.monster.phoneshop.dto.*;
import ec.edu.monster.phoneshop.service.MainService;

public class MainController {
    private final CommandLine commandLine = CommandLine.getInstance();
    private final MainService mainService = new MainService();
    private HashMap<Integer, ProductDto> productsMap = new HashMap<>();

    public void init() {
        int code = 1;
        List<ProductDto> products = mainService.getProducts();

        for (ProductDto product : products) {
            productsMap.put(code++, product);
        }

        while (true) {
            commandLine.print("Menú principal\n\n");
            int selectedOption = commandLine.choose("Selecciona una opcion: ",
                    "Ver telefonos",
                    "Consultar Crédito disponible",
                    "Comprar teléfono",
                    "Salir");

            commandLine.print("\n\n");

            switch (selectedOption) {
                case 1:
                    commandLine.print("Últimos productos");

                    code = 1;
                    for (ProductDto product : products) {
                        commandLine.print(printProduct(product, code++));
                    }
                    break;
                case 2:
                    checkCredit();
                    break;
                case 3:
                    buyPhone();
                    System.exit(0);
                    return;
                case 4:
                    commandLine.print("Saliendo...");
                    System.exit(0);
                    return;
            }
        }
    }

    private String printMovement(MovementDto movement) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("#: %s\nFecha: %s\nMonto: %s\nTipo: %s\nCuenta destino:%s\n\n",
                movement.getReference(),
                formatter.format(movement.getCreatedAt()),
                movement.getAmount().toString(),
                movement.getType().getLabel(),
                movement.getTargetAccount().getReference());
    }

    private String printProduct(ProductDto product, int code) {
        return String.format("Código: %d\nNombre: %s\nPrecio: %s\n\n",
                code,
                product.getName(),
                product.getPrice().toString());
    }

    private void checkCredit() {
        commandLine.print("Consulta de crédito\n");
        Double amount = commandLine.promptDouble("Monto a solicitar: ");
        try {
            String identificationNumber = ApplicationContext.getInstance().getIdentificationNumber();

            CheckCreditResultDto result = mainService.checkCredit(CheckCreditRequestDto.builder()
                    .identificationNumber(identificationNumber)
                    .amount(BigDecimal.valueOf(amount))
                    .build());

            if (result.isEligible()) {
                commandLine.print(String.format("Tienes un crédito disponible de %s\n", result.getMaxAmount().setScale(2, BigDecimal.ROUND_HALF_UP)));
            } else {
                commandLine.print(result.getMessage().isBlank() ? "No eres elegible para un crédito\n" : result.getMessage());
            }
        } catch (Exception e) {
            commandLine.print(e.getMessage());
        }
    }

    private void buyPhone() {
        commandLine.print("Compra de teléfono\n");
        int code = commandLine.promptInteger("Código del teléfono: ");

        if (!productsMap.containsKey(code)) {
            commandLine.print("Código inválido\n");
            buyPhone();
            return;
        }

        ProductDto product = productsMap.get(code);
        int method = commandLine.choose("Método de pago: ", "Efectivo", "Crédito");
        PurchaseMethod purchaseMethod = method == 1 ? PurchaseMethod.CASH : PurchaseMethod.CREDIT;
        int months = 0;

        if (purchaseMethod == PurchaseMethod.CREDIT) {
            months = commandLine.promptInteger("A cuantos meses: ");
        }

        try {
            PurchaseResultDto result = mainService.purchase(PurchaseRequestDto.builder()
                    .method(purchaseMethod)
                    .months(months)
                    .identificationNumber(ApplicationContext.getInstance().getIdentificationNumber())
                    .productId(product.getId())
                    .build());

            if (result.getStatus() == ResponseStatus.ERROR) {
                commandLine.print(result.getMessage().isBlank() ? "Error al realizar la compra\n" : result.getMessage());
                return;
            }

            commandLine.print("Compra realizada con éxito\n");

            if (purchaseMethod == PurchaseMethod.CREDIT) {
                commandLine.print("Tabla de amortizacion:\n\n");

                result.getCredit().getInstallments().forEach(installment -> {
                    commandLine.print(String.format("#: %d\nvalor cuota: %s\nInteres pagado: %s\nCapital pagado: %s\nSaldo: %s\n",
                            installment.getNumber(),
                            installment.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP),
                            installment.getInterest().setScale(2, BigDecimal.ROUND_HALF_UP),
                            installment.getCapital().setScale(2, BigDecimal.ROUND_HALF_UP),
                            installment.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP
                    )));
                });
            }

            int option = commandLine.choose("Desea volver al menu principal?: ", "Si", "No");

            if (option == 1) {
                init();
            } else {
                System.exit(0);
            }
        } catch (Exception e) {
            commandLine.print(e.getMessage());
        }
    }

    private void makeTransaction() {
        commandLine.print("Nueva Transferencia\n");
        String bankAccountReference = commandLine.prompt("Cuenta destino: ");
        Double amount = commandLine.promptDouble("Monto: ");
        int type = commandLine.choose("Tipo de transacción: ", "Depósito", "Retiro");
        MovementType movementType = type == 1 ? MovementType.DEPOSIT : MovementType.WITHDRAWAL;

        try {
            mainService.transfer(TransactionDto.builder()
                    .bankAccountReference(bankAccountReference)
                    .amount(BigDecimal.valueOf(amount))
                    .type(movementType)
                    .build());

            commandLine.print("Transferencia exitosa\n");
        } catch (Exception e) {
            commandLine.print(e.getMessage());
        }
    }
}
