package ec.edu.espe.banquito.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ec.edu.espe.banquito.model.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Integer> {

    /** True when any account of the client has a movement of the given type on or after {@code desde}. */
    @Query("""
            select count(m) > 0 from Movimiento m
            where m.cuenta.cliente.codCliente = :codCliente and m.tipo = :tipo and m.fecha >= :desde
            """)
    boolean existeMovimientoDesde(@Param("codCliente") Integer codCliente, @Param("tipo") String tipo,
            @Param("desde") LocalDate desde);

    /** Average value of the client's movements of the given type on or after {@code desde}; null when none. */
    @Query("""
            select avg(m.valor) from Movimiento m
            where m.cuenta.cliente.codCliente = :codCliente and m.tipo = :tipo and m.fecha >= :desde
            """)
    Double promedioDesde(@Param("codCliente") Integer codCliente, @Param("tipo") String tipo,
            @Param("desde") LocalDate desde);

    default BigDecimal promedio(Integer codCliente, String tipo, LocalDate desde) {
        Double promedio = promedioDesde(codCliente, tipo, desde);
        return promedio == null ? BigDecimal.ZERO : BigDecimal.valueOf(promedio);
    }
}
