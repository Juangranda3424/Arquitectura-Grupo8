package ec.edu.espe.banquito.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
public class Cliente {

    public static final String GENERO_MASCULINO = "M";

    @Id
    @Column(name = "cod_cliente")
    private Integer codCliente;

    @Column(name = "cedula", nullable = false, length = 100)
    private String cedula;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "genero", nullable = false, length = 1)
    private String genero;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    protected Cliente() {
    }

    public Cliente(Integer codCliente, String cedula, String nombre, String genero, LocalDate fechaNacimiento) {
        this.codCliente = codCliente;
        this.cedula = cedula;
        this.nombre = nombre;
        this.genero = genero;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Integer getCodCliente() { return codCliente; }
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getGenero() { return genero; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
}
