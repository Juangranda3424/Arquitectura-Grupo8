package ec.edu.monster.phoneshop.service.impl;

import ec.edu.monster.banquito.dto.CreditApplicationRequestDto;
import ec.edu.monster.banquito.dto.CreditApplicationResponseDto;
import ec.edu.monster.banquito.dto.ResponseStatus;
import ec.edu.monster.banquito.entity.User;
import ec.edu.monster.banquito.repository.UserRepository;
import ec.edu.monster.banquito.service.CreditService;
import ec.edu.monster.phoneshop.dto.PurchaseRequestDto;
import ec.edu.monster.phoneshop.dto.PurchaseResultDto;
import ec.edu.monster.phoneshop.entity.Product;
import ec.edu.monster.phoneshop.entity.Sale;
import ec.edu.monster.phoneshop.enums.PurchaseMethod;
import ec.edu.monster.phoneshop.repository.ProductRepository;
import ec.edu.monster.phoneshop.repository.SaleRepository;
import ec.edu.monster.phoneshop.service.PurchaseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final CreditService creditService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public PurchaseResultDto purchase(PurchaseRequestDto request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        User user = userRepository.findFirstByIdentificationNumber(request.getIdentificationNumber())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (request.getMethod() == PurchaseMethod.CASH) {
            Sale sale = Sale.builder()
                    .amount(product.getPrice())
                    .purchaseMethod(request.getMethod())
                    .product(product)
                    .user(user)
                    .build();

            saleRepository.save(sale);

            return PurchaseResultDto.builder()
                    .status(ResponseStatus.SUCCESS)
                    .message("Compra realizada con éxito")
                    .build();
        }

        CreditApplicationResponseDto creditApplicationResponse;

        try {
            creditApplicationResponse = creditService.applyForCredit(CreditApplicationRequestDto.builder()
                    .identificationNumber(user.getIdentificationNumber())
                    .amount(product.getPrice())
                    .months(request.getMonths())
                    .build());
        } catch (ResponseStatusException e) {
            return PurchaseResultDto.builder()
                    .status(ResponseStatus.ERROR)
                    .message(e.getMessage())
                    .build();
        }

        Sale sale = Sale.builder()
                .amount(product.getPrice())
                .purchaseMethod(request.getMethod())
                .product(product)
                .user(user)
                .build();

        saleRepository.save(sale);

        return PurchaseResultDto.builder()
                .credit(creditApplicationResponse)
                .status(ResponseStatus.SUCCESS)
                .message("Compra realizada con éxito")
                .product(product)
                .user(modelMapper.map(user, ec.edu.monster.banquito.dto.UserDto.class))
                .build();
    }
}
