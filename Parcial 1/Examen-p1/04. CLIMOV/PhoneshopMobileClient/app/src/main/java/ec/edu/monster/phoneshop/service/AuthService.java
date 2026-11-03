package ec.edu.monster.phoneshop.service;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ec.edu.monster.phoneshop.client.PhoneShopServiceClient;
import ec.edu.monster.phoneshop.common.ApplicationContext;
import ec.edu.monster.phoneshop.dto.AuthCredentialsDto;
import ec.edu.monster.phoneshop.dto.AuthResponseDto;
import ec.edu.monster.phoneshop.dto.ResponseStatus;
import retrofit2.Call;
import retrofit2.Response;

public class AuthService {
    private final ApplicationContext applicationContext = ApplicationContext.getInstance();

    public AuthResponseDto login(AuthCredentialsDto credentials) {
        PhoneShopServiceClient phoneshopBankServiceClient = ApplicationContext.getInstance().buildPhoneShopServiceClient();

        try {
            Call<AuthResponseDto> call = phoneshopBankServiceClient.login(credentials);
            Response<AuthResponseDto> response = call.execute();

            if (response.isSuccessful()) {
                AuthResponseDto result = response.body();
                applicationContext.setToken(result.getToken());
                return result;
            } else {
                String message = "Error interno del servidor";

                try {
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(response.errorBody().string(), JsonObject.class);
                    message = jsonObject.get("detail").getAsString();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                return AuthResponseDto.builder().status(ResponseStatus.ERROR).message(message).build();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return AuthResponseDto.builder()
                    .status(ResponseStatus.ERROR)
                    .message(ex.getMessage())
                    .build();
        }
    }
}
