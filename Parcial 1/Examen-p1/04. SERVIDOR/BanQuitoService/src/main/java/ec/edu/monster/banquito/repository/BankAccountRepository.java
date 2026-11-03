package ec.edu.monster.banquito.repository;

import ec.edu.monster.banquito.entity.BankAccount;
import ec.edu.monster.banquito.entity.BankAccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    Optional<BankAccount> findFirstByReference(String reference);

    @Query(value = "SELECT coalesce((SELECT b.number FROM bank_accounts b ORDER BY b.number DESC LIMIT 1), 0) + 1", nativeQuery = true)
    BigInteger getNextNumber();

    List<BankAccount> findAllByUserId(UUID userId);

    Optional<BankAccount> findFirstByUserIdAndType(UUID userId, BankAccountType type);

    Optional<BankAccount> findFirstByUserIdentificationNumber(String identificationNumber);
}
