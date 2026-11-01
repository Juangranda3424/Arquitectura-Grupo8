package ec.edu.monster.model;

/**
 * Movimiento de una cuenta, ya adaptado para la vista.
 *
 * El importe viaja con signo: positivo para un ingreso y negativo para un egreso,
 * así la tabla y los totales solo suman, sin repetir la clasificación.
 */
public class MovimientoModel {

    private final int numero;
    private final String fecha;
    private final String tipoCodigo;
    private final String tipoDescripcion;
    private final double importe;
    private final double saldo;
    private final String cuentaReferencia;

    public MovimientoModel(int numero, String fecha, String tipoCodigo, String tipoDescripcion,
                           double importe, double saldo) {
        this(numero, fecha, tipoCodigo, tipoDescripcion, importe, saldo, null);
    }

    public MovimientoModel(int numero, String fecha, String tipoCodigo, String tipoDescripcion,
                           double importe, double saldo, String cuentaReferencia) {
        this.numero = numero;
        this.fecha = fecha;
        this.tipoCodigo = tipoCodigo;
        this.tipoDescripcion = tipoDescripcion;
        this.importe = importe;
        this.saldo = saldo;
        this.cuentaReferencia = cuentaReferencia;
    }

    public int getNumero() {
        return numero;
    }

    public String getFecha() {
        return fecha;
    }

    public String getTipoCodigo() {
        return tipoCodigo;
    }

    public String getTipoDescripcion() {
        return tipoDescripcion;
    }

    public double getImporte() {
        return importe;
    }

    public double getImporteAbsoluto() {
        return Math.abs(importe);
    }

    public double getSaldo() {
        return saldo;
    }

    public String getCuentaReferencia() {
        return cuentaReferencia;
    }

    /** Propiedad de bean: las JSP la leen como ${mov.ingreso}. */
    public boolean isIngreso() {
        return importe >= 0;
    }

    /** Propiedad de bean: las JSP la leen como ${mov.egreso}. */
    public boolean isEgreso() {
        return importe < 0;
    }
}
