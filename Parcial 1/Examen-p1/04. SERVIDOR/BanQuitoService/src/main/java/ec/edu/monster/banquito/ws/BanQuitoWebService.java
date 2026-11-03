package ec.edu.monster.banquito.ws;

import ec.edu.monster.banquito.dto.*;
import ec.edu.monster.banquito.service.AuthService;
import ec.edu.monster.banquito.service.CreditService;
import ec.edu.monster.banquito.service.MovementService;
import ec.edu.monster.phoneshop.dto.PurchaseRequestDto;
import ec.edu.monster.phoneshop.dto.PurchaseResultDto;
import ec.edu.monster.phoneshop.enums.PurchaseMethod;
import ec.edu.monster.phoneshop.service.ProductService;
import ec.edu.monster.phoneshop.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.UUID;


@Endpoint
@RequiredArgsConstructor
public class BanQuitoWebService {
    private static final String NAMESPACE_URI = "http://monster.edu.ec/banquito/ws";
    private final CreditService creditService;
    private final MovementService movementService;
    private final AuthService authService;
    private final ProductService productService;
    private final PurchaseService purchaseService;
    private final ModelMapper modelMapper;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getMovementsByIdentificationRequest")
    @ResponsePayload
    public GetMovementsByIdentificationResponse getMovementsByIdentificationRequest(@RequestPayload GetMovementsByIdentificationRequest request) {
        GetMovementsByIdentificationResponse response = new GetMovementsByIdentificationResponse();
        List<Movement> movements = movementService.getMovementsByIdentificationNumber(request.getIdentification())
                .stream()
                .map(movementDto -> modelMapper.map(movementDto, Movement.class))
                .toList();
        response.getMovements().addAll(movements);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "checkCreditRequest")
    @ResponsePayload
    public CheckCreditResponse checkCreditRequest(@RequestPayload CheckCreditRequest request) {
        CheckCreditResultDto result = creditService.checkCredit(CheckCreditRequestDto.builder()
                .identificationNumber(request.getIdentification())
                .amount(request.getAmount())
                .build());

        CheckCreditResponse response = new CheckCreditResponse();
        response.setEligible(result.isEligible());
        response.setMessage(result.getMessage());
        response.setMaxAmount(result.getMaxAmount());

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "applyForCreditRequest")
    @ResponsePayload
    public ApplyForCreditResponse applyForCreditRequest(@RequestPayload ApplyForCreditRequest request) {
        CreditApplicationResponseDto result = creditService.applyForCredit(CreditApplicationRequestDto.builder()
                .amount(request.getAmount())
                .identificationNumber(request.getIdentification())
                .months(request.getMonths())
                .build());

        ApplyForCreditResponse response = new ApplyForCreditResponse();

        response.setTotalAmount(result.getTotalAmount());
        response.setTotalInstallments(result.getTotalInstallments());
        response.getInstallments().addAll(
                result.getInstallments().stream()
                        .map(installmentDto -> modelMapper.map(installmentDto, CreditInstallment.class))
                        .toList()
        );
        response.setMonthlyInstallment(result.getMonthlyInstallment());
        response.setAnnualInterestRate(result.getAnnualInterestRate());

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "loginRequest")
    @ResponsePayload
    public LoginResponse loginRequest(@RequestPayload LoginRequest request) {
        LoginResponse response = new LoginResponse();
        AuthResponseDto authResponse = authService.login(AuthCredentialsDto.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build());

        response.setToken(authResponse.getToken());
        response.setUser(modelMapper.map(authResponse.getUser(), User.class));
        response.setStatus(ResponseStatus.valueOf(authResponse.getStatus().name()));
        response.setMessage(authResponse.getMessage());

        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getProductsRequest")
    @ResponsePayload
    public GetProductsResponse getProductsRequest(@RequestPayload GetProductsRequest request) {
        GetProductsResponse response = new GetProductsResponse();
        List<Product> products = productService.getProducts()
                .stream()
                .map(productDto -> modelMapper.map(productDto, Product.class))
                .toList();
        response.getProducts().addAll(products);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "purchaseRequest")
    @ResponsePayload
    public PurchaseResponse purchaseRequest(@RequestPayload PurchaseRequest request) {
        PurchaseResultDto result = purchaseService.purchase(PurchaseRequestDto.builder()
                .identificationNumber(request.getIdentificationNumber())
                .method(PurchaseMethod.valueOf(request.getPurchaseMethod().name()))
                .months(request.getMonths())
                .productId(UUID.fromString(request.getProductId()))
                .build());

        PurchaseResponse response = new PurchaseResponse();
        response.setStatus(ResponseStatus.valueOf(result.getStatus().name()));
        response.setCreditResponse(modelMapper.map(result.getCredit(), AppliedCreditResponse.class));
        response.setMessage(result.getMessage());

        return response;
    }
}
