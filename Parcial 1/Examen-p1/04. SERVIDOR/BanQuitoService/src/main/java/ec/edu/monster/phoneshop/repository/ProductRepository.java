package ec.edu.monster.phoneshop.repository;

import ec.edu.monster.phoneshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findFirstByNameIgnoreCase(String name);
}
