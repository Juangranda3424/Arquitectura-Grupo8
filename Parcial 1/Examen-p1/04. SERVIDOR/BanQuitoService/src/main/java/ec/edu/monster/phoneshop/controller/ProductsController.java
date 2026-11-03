package ec.edu.monster.phoneshop.controller;

import ec.edu.monster.banquito.dto.ProductDto;
import ec.edu.monster.phoneshop.entity.Product;
import ec.edu.monster.phoneshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/phones")
@RequiredArgsConstructor
public class ProductsController {
    private final ProductService productService;

    @GetMapping
    public List<Product> getPhones() {
        return productService.getProducts();
    }

    @PutMapping
    public ProductDto updatePhone(@RequestBody ProductDto product) {
        return productService.updateProduct(product);
    }
}
