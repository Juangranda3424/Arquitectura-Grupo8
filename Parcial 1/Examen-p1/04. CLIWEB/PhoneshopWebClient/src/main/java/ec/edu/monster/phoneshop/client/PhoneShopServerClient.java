package ec.edu.monster.phoneshop.client;

import ec.edu.monster.phoneshop.dto.*;
import feign.Headers;
import feign.RequestLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.net.URI;
import java.util.List;

@FeignClient(name = "phoneshop-server", url = "${feign.url.phoneshop-server}")
public interface PhoneShopServerClient {
    @PostMapping("/auth/login")
    AuthResponseDto login(URI baseUrl, AuthCredentialsDto credentials);

    @GetMapping("/auth/me")
    UserDto getCurrentUser(URI baseUrl);

    @GetMapping("/movements")
    List<MovementDto> getMovements(URI baseUrl);

    @PostMapping("/transactions/transfer")
    void transfer(URI baseUrl, TransactionDto transaction);

    @PostMapping("/public/credit/check")
    CheckCreditResultDto checkCredit(URI baseUrl, CheckCreditRequestDto request);

    @GetMapping("/public/phones")
    List<ProductDto> getPhones(URI baseUrl);

    @PostMapping("/public/purchase")
    PurchaseResultDto purchase(URI baseUrl, PurchaseRequestDto request);

    @PutMapping("/public/phones")
    ProductDto updatePhone(URI baseUrl, ProductDto phone);
}
