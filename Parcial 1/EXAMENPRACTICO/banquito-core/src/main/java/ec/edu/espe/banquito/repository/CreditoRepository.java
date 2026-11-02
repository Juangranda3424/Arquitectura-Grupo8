package ec.edu.espe.banquito.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import ec.edu.espe.banquito.model.Credito;

public interface CreditoRepository extends JpaRepository<Credito, Integer> {

    boolean existsByClienteCodClienteAndEstado(Integer codCliente, String estado);

    @EntityGraph(attributePaths = {"cliente", "tablaAmortizacion"})
    Optional<Credito> findWithTablaByCodCredito(Integer codCredito);
}
