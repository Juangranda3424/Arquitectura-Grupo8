package ec.edu.monster.banquito.repository;

import ec.edu.monster.banquito.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findFirstByUsernameIgnoreCase(String username);
    Optional<User> findFirstByEmailIgnoreCase(String email);

    @Query(nativeQuery = true, value = "SELECT * " +
            "FROM users as u " +
            "WHERE (LOWER(u.email) = LOWER(:credentialId) OR LOWER(u.username) = LOWER(:credentialId)) LIMIT 1")
    Optional<User> findByCredentialId(String credentialId);

    Optional<User> findFirstByIdentificationNumber(String identificationNumber);
}
