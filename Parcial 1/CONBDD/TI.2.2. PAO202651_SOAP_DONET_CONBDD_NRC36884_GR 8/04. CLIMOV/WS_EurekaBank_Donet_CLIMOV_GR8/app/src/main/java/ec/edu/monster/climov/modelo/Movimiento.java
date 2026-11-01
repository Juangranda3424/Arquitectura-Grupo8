package ec.edu.monster.climov.modelo;

/**
 * Movimiento bancario recibido del servicio SOAP.
 */
public class Movimiento {

    private String cuenta;
    private int nromov;
    private String fecha;
    private String tipo;
    private String accion;
    private double importe;

    public Movimiento() {
    }

    public Movimiento(String cuenta, int nromov, String fecha, String tipo, String accion, double importe) {
        this.cuenta = cuenta;
        this.nromov = nromov;
        this.fecha = fecha;
        this.tipo = tipo;
        this.accion = accion;
        this.importe = importe;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public int getNromov() {
        return nromov;
    }

    public void setNromov(int nromov) {
        this.nromov = nromov;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }
}
