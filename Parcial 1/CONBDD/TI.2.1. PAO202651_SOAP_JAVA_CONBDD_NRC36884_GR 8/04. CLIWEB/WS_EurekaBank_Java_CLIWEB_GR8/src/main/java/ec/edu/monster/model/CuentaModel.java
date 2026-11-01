package ec.edu.monster.model;

/**
 * Datos de una cuenta listos para la vista.
 *
 * La fecha de creación llega del servicio SOAP como un tipo `date` sin contenido
 * (el esquema del servidor no expone el valor), por eso se muestra como texto y
 * queda en "No disponible" cuando no hay dato que mostrar.
 */
public class CuentaModel {

    private final String codigo;
    private final String moneda;
    private final String sucursal;
    private final String cliente;
    private final double saldo;
    private final int contadorMovimientos;
    private final String estado;
    private final String fechaCreacion;

    public CuentaModel(String codigo, String moneda, String sucursal, String cliente, double saldo,
                       int contadorMovimientos, String estado, String fechaCreacion) {
        this.codigo = codigo;
        this.moneda = moneda;
        this.sucursal = sucursal;
        this.cliente = cliente;
        this.saldo = saldo;
        this.contadorMovimientos = contadorMovimientos;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMoneda() {
        return moneda;
    }

    public String getSucursal() {
        return sucursal;
    }

    public String getCliente() {
        return cliente;
    }

    public double getSaldo() {
        return saldo;
    }

    public int getContadorMovimientos() {
        return contadorMovimientos;
    }

    public String getEstado() {
        return estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }
}
