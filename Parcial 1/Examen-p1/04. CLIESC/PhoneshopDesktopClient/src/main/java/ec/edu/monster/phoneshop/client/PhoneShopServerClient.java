package ec.edu.monster.phoneshop.client;

import ec.edu.monster.phoneshop.dto.*;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface PhoneShopServerClient {
    @RequestLine("POST /auth/login")
    @Headers("Content-Type: application/json")
    AuthResponseDto login(AuthCredentialsDto credentials);

    @RequestLine("GET /movements")
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer {token}"
    })
    List<MovementDto> getMovements(@Param("token") String token);

    @RequestLine("POST /transactions/transfer")
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer {token}"
    })
    void transfer(@Param("token") String token, TransactionDto transaction);

    @RequestLine("POST /public/credit/check")
    @Headers("Content-Type: application/json")
    CheckCreditResultDto checkCredit(CheckCreditRequestDto request);

    @RequestLine("GET /public/phones")
    @Headers("Content-Type: application/json")
    List<ProductDto> getPhones();

    @RequestLine("POST /public/purchase")
    @Headers("Content-Type: application/json")
    PurchaseResultDto purchase(PurchaseRequestDto request);
}
