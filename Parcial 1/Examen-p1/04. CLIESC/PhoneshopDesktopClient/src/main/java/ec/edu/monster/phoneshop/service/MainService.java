package ec.edu.monster.phoneshop.service;

import ec.edu.monster.phoneshop.client.PhoneShopServerClient;
import ec.edu.monster.phoneshop.common.ApplicationContext;
import ec.edu.monster.phoneshop.dto.*;

import java.math.BigDecimal;
import java.util.List;

public class MainService {
    private final ApplicationContext applicationContext = ApplicationContext.getInstance();
    private final PhoneShopServerClient client;

    public MainService() {
        client = applicationContext.buildFeignClient(PhoneShopServerClient.class);
    }

    public List<MovementDto> getCurrentUserMovements() {
        return client.getMovements(applicationContext.getToken());
    }

    public void transfer(TransactionDto transaction) {
        client.transfer(applicationContext.getToken(), transaction);
    }

    public List<ProductDto> getProducts() {
        return client.getPhones();
    }

    public CheckCreditResultDto checkCredit(CheckCreditRequestDto request) {
        return client.checkCredit(request);
    }

    public PurchaseResultDto purchase(PurchaseRequestDto request) {
        return client.purchase(request);
    }
}
