package ec.edu.espe.banquito.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "movimiento")
public class Movimiento {

    public static final String DEPOSITO = "DEP";
    public static final String RETIRO = "RET";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_movimiento")
    private Integer codMovimiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "num_cuenta", nullable = false)
    private Cuenta cuenta;

    @Column(name = "tipo", nullable = false, length = 3)
    private String tipo;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    protected Movimiento() {
    }

    public Integer getCodMovimiento() { return codMovimiento; }
    public Cuenta getCuenta() { return cuenta; }
    public String getTipo() { return tipo; }
    public BigDecimal getValor() { return valor; }
    public LocalDate getFecha() { return fecha; }
}
