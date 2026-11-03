package ec.edu.monster.phoneshop.client;

import java.util.List;

import ec.edu.monster.phoneshop.dto.AuthCredentialsDto;
import ec.edu.monster.phoneshop.dto.AuthResponseDto;
import ec.edu.monster.phoneshop.dto.CheckCreditRequestDto;
import ec.edu.monster.phoneshop.dto.CheckCreditResultDto;
import ec.edu.monster.phoneshop.dto.MovementDto;
import ec.edu.monster.phoneshop.dto.ProductDto;
import ec.edu.monster.phoneshop.dto.PurchaseRequestDto;
import ec.edu.monster.phoneshop.dto.PurchaseResultDto;
import ec.edu.monster.phoneshop.dto.TransactionDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PhoneShopServiceClient {
    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("auth/login")
    Call<AuthResponseDto> login(@Body AuthCredentialsDto credentials);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("movements")
    Call<List<MovementDto>> getMovements(@Header("Authorization") String authorization);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("transactions/transfer")
    Call<Void> transfer(@Header("Authorization") String authorization, @Body TransactionDto transaction);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("public/credit/check")
    Call<CheckCreditResultDto> checkCredit(@Body CheckCreditRequestDto request);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @POST("public/purchase")
    Call<PurchaseResultDto> purchase(@Body PurchaseRequestDto request);

    @Headers({"Content-Type: application/json;charset=UTF-8"})
    @GET("public/phones")
    Call<List<ProductDto>> getPhones();
}
