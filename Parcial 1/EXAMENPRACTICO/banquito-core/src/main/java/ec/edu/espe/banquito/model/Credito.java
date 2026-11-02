package ec.edu.espe.banquito.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name = "credito")
public class Credito {

    public static final String ACTIVO = "ACT";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_credito")
    private Integer codCredito;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cod_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "num_cuotas", nullable = false)
    private Integer numCuotas;

    @Column(name = "tasa_anual", nullable = false, precision = 5, scale = 2)
    private BigDecimal tasaAnual;

    @Column(name = "valor_cuota", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCuota;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "estado", nullable = false, length = 3)
    private String estado;

    @OneToMany(mappedBy = "credito", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("numCuota ASC")
    private List<TablaAmortizacion> tablaAmortizacion = new ArrayList<>();

    protected Credito() {
    }

    public Credito(Cliente cliente, BigDecimal monto, int numCuotas, BigDecimal tasaAnual,
            BigDecimal valorCuota, LocalDate fecha) {
        this.cliente = cliente;
        this.monto = monto;
        this.numCuotas = numCuotas;
        this.tasaAnual = tasaAnual;
        this.valorCuota = valorCuota;
        this.fecha = fecha;
        this.estado = ACTIVO;
    }

    public void agregarCuota(TablaAmortizacion cuota) {
        cuota.setCredito(this);
        tablaAmortizacion.add(cuota);
    }

    public Integer getCodCredito() { return codCredito; }
    public Cliente getCliente() { return cliente; }
    public BigDecimal getMonto() { return monto; }
    public Integer getNumCuotas() { return numCuotas; }
    public BigDecimal getTasaAnual() { return tasaAnual; }
    public BigDecimal getValorCuota() { return valorCuota; }
    public LocalDate getFecha() { return fecha; }
    public String getEstado() { return estado; }
    public List<TablaAmortizacion> getTablaAmortizacion() { return tablaAmortizacion; }
}
