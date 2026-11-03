package ec.edu.monster.phoneshop.service;

import ec.edu.monster.banquito.dto.ProductDto;
import ec.edu.monster.phoneshop.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> getProducts();
    ProductDto updateProduct(ProductDto product);
}
