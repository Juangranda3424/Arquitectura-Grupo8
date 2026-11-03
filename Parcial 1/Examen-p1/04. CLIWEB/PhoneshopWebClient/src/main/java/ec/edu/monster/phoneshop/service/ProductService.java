package ec.edu.monster.phoneshop.service;

import ec.edu.monster.phoneshop.dto.ProductDto;

import java.util.List;

public interface ProductService {
    List<ProductDto> getProducts();
}
