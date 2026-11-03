package ec.edu.monster.phoneshop.service.impl;

import ec.edu.monster.banquito.dto.ProductDto;
import ec.edu.monster.phoneshop.entity.Product;
import ec.edu.monster.phoneshop.repository.ProductRepository;
import ec.edu.monster.phoneshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    @Override
    public ProductDto updateProduct(ProductDto product) {
        Product current = productRepository.findById(product.getId()).orElse(null);

        if (current != null) {
            current.setName(product.getName() != null ? product.getName() : current.getName());
            current.setPrice(product.getPrice() != null ? product.getPrice() : current.getPrice());
            current.setImage(product.getImage() != null ? product.getImage() : current.getImage());
            productRepository.save(current);
            return product;
        }

        return modelMapper.map(product, ProductDto.class);
    }
}
