package ec.edu.monster.phoneshop.controller;

import ec.edu.monster.phoneshop.dto.PurchaseRequestDto;
import ec.edu.monster.phoneshop.dto.PurchaseResultDto;
import ec.edu.monster.phoneshop.entity.Product;
import ec.edu.monster.phoneshop.service.ProductService;
import ec.edu.monster.phoneshop.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/purchase")
@RequiredArgsConstructor
public class PurchaseController {
    private final PurchaseService purchaseService;

    @PostMapping
    public PurchaseResultDto purchase(@RequestBody PurchaseRequestDto request) {
        return purchaseService.purchase(request);
    }
}
