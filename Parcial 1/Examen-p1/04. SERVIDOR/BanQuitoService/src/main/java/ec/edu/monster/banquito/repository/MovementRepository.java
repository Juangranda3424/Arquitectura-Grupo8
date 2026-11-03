package ec.edu.monster.banquito.repository;

import ec.edu.monster.banquito.entity.Movement;
import ec.edu.monster.banquito.entity.MovementType;
import ec.edu.monster.banquito.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MovementRepository extends JpaRepository<Movement, UUID> {
    @Query(value = "SELECT coalesce((SELECT m.number FROM movements m ORDER BY m.number DESC LIMIT 1), 0) + 1", nativeQuery = true)
    BigInteger getNextNumber();

    List<Movement> findAllByTargetAccountUser(User userAccount);
    List<Movement> findAllBySender(User sender);

    List<Movement> findAllBySenderIdentificationNumberAndType(String identificationNumber, MovementType type);

    List<Movement> findAllBySenderIdentificationNumberAndCreatedAtAfterAndType(String identificationNumber, LocalDateTime createdAt, MovementType type);

    List<Movement> findAllBySenderIdentificationNumber(String identificationNumber);
}
