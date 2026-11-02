package ec.edu.espe.banquito.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cuenta")
public class Cuenta {

    @Id
    @Column(name = "num_cuenta", length = 8)
    private String numCuenta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cod_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "saldo", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo;

    protected Cuenta() {
    }

    public String getNumCuenta() { return numCuenta; }
    public Cliente getCliente() { return cliente; }
    public BigDecimal getSaldo() { return saldo; }
}
