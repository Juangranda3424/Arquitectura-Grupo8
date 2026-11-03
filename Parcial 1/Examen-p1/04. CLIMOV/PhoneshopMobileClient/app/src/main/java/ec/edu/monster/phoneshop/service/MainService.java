package ec.edu.monster.phoneshop.service;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;

import ec.edu.monster.phoneshop.client.PhoneShopServiceClient;
import ec.edu.monster.phoneshop.common.ApplicationContext;
import ec.edu.monster.phoneshop.dto.MovementDto;
import ec.edu.monster.phoneshop.dto.ProductDto;
import ec.edu.monster.phoneshop.dto.TransactionDto;
import retrofit2.Call;
import retrofit2.Response;

public class MainService {
    private final ApplicationContext applicationContext = ApplicationContext.getInstance();

    public List<MovementDto> getCurrentUserMovements() {
        PhoneShopServiceClient phoneshopBankServiceClient = ApplicationContext.getInstance().buildPhoneShopServiceClient();

        try {
            Call<List<MovementDto>> call = phoneshopBankServiceClient.getMovements(String.format("Bearer %s", applicationContext.getToken()));
            Response<List<MovementDto>> rawResponse = call.execute();
            return rawResponse.body();
        } catch (Exception ex) {
            return List.of();
        }
    }

    public void transfer(TransactionDto transaction) {
        PhoneShopServiceClient phoneshopBankServiceClient = ApplicationContext.getInstance().buildPhoneShopServiceClient();
        Call<Void> call = phoneshopBankServiceClient.transfer(String.format("Bearer %s", applicationContext.getToken()), transaction);

        try {
            call.execute();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<ProductDto> getProducts() {
        PhoneShopServiceClient phoneshopBankServiceClient = ApplicationContext.getInstance().buildPhoneShopServiceClient();

        try {
            Call<List<ProductDto>> call = phoneshopBankServiceClient.getPhones();
            Response<List<ProductDto>> rawResponse = call.execute();
            return rawResponse.body();
        } catch (Exception ex) {
            return List.of();
        }
    }
}