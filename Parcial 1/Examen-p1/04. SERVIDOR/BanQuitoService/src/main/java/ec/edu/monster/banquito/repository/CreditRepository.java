package ec.edu.monster.banquito.repository;

import ec.edu.monster.banquito.entity.Credit;
import ec.edu.monster.banquito.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CreditRepository extends JpaRepository<Credit, UUID> {
    Optional<Credit> findFirstByUserIdentificationNumber(String identificationNumber);
    Optional<Credit> findFirstByUserAndPaidIsFalse(User user);
}
