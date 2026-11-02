package ec.edu.espe.banquito.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ec.edu.espe.banquito.model.Cliente;
import jakarta.persistence.LockModeType;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByCedula(String cedula);

    /** Locks the client row so two concurrent grants cannot both pass the "no active credit" rule. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cliente c where c.codCliente = :codCliente")
    Optional<Cliente> findByIdForUpdate(@Param("codCliente") Integer codCliente);
}
