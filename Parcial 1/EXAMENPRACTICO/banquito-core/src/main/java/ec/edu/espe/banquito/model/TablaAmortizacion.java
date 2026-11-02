package ec.edu.espe.banquito.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** One row of an amortization table: # Cuota | Valor Cuota | Interés Pagado | Capital Pagado | Saldo. */
@Entity
@Table(name = "tabla_amortizacion")
public class TablaAmortizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_amortizacion")
    private Integer codAmortizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cod_credito", nullable = false)
    private Credito credito;

    @Column(name = "num_cuota", nullable = false)
    private Integer numCuota;

    @Column(name = "valor_cuota", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCuota;

    @Column(name = "interes_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal interesPagado;

    @Column(name = "capital_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal capitalPagado;

    @Column(name = "saldo", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldo;

    protected TablaAmortizacion() {
    }

    public TablaAmortizacion(int numCuota, BigDecimal valorCuota, BigDecimal interesPagado,
            BigDecimal capitalPagado, BigDecimal saldo) {
        this.numCuota = numCuota;
        this.valorCuota = valorCuota;
        this.interesPagado = interesPagado;
        this.capitalPagado = capitalPagado;
        this.saldo = saldo;
    }

    void setCredito(Credito credito) { this.credito = credito; }

    public Integer getCodAmortizacion() { return codAmortizacion; }
    public Credito getCredito() { return credito; }
    public Integer getNumCuota() { return numCuota; }
    public BigDecimal getValorCuota() { return valorCuota; }
    public BigDecimal getInteresPagado() { return interesPagado; }
    public BigDecimal getCapitalPagado() { return capitalPagado; }
    public BigDecimal getSaldo() { return saldo; }
}
