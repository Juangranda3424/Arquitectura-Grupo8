package ec.edu.monster.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CuentaModel {
    @JsonProperty("chrClieCodigo")
    private String chrClieCodigo;

    @JsonProperty("chrCuenClave")
    private String chrCuenClave;

    @JsonProperty("chrCuenCodigo")
    private String chrCuenCodigo;

    @JsonProperty("chrEmplCreaCuenta")
    private String chrEmplCreaCuenta;

    @JsonProperty("chrMoneCodigo")
    private String chrMoneCodigo;

    @JsonProperty("chrSucucodigo")
    private String chrSucucodigo;

    @JsonProperty("decCuenSaldo")
    private double decCuenSaldo;

    @JsonProperty("dttCuenFechaCreacion")
    private String dttCuenFechaCreacion; // Cambiado a String, sin deserializador

    @JsonProperty("intCuenContMov")
    private int intCuenContMov;

    @JsonProperty("vchCuenEstado")
    private String vchCuenEstado;

    // Getters y setters
    public String getChrClieCodigo() { return chrClieCodigo; }
    public void setChrClieCodigo(String chrClieCodigo) { this.chrClieCodigo = chrClieCodigo; }
    public String getChrCuenClave() { return chrCuenClave; }
    public void setChrCuenClave(String chrCuenClave) { this.chrCuenClave = chrCuenClave; }
    public String getChrCuenCodigo() { return chrCuenCodigo; }
    public void setChrCuenCodigo(String chrCuenCodigo) { this.chrCuenCodigo = chrCuenCodigo; }
    public String getChrEmplCreaCuenta() { return chrEmplCreaCuenta; }
    public void setChrEmplCreaCuenta(String chrEmplCreaCuenta) { this.chrEmplCreaCuenta = chrEmplCreaCuenta; }
    public String getChrMoneCodigo() { return chrMoneCodigo; }
    public void setChrMoneCodigo(String chrMoneCodigo) { this.chrMoneCodigo = chrMoneCodigo; }
    public String getChrSucucodigo() { return chrSucucodigo; }
    public void setChrSucucodigo(String chrSucucodigo) { this.chrSucucodigo = chrSucucodigo; }
    public double getDecCuenSaldo() { return decCuenSaldo; }
    public void setDecCuenSaldo(double decCuenSaldo) { this.decCuenSaldo = decCuenSaldo; }
    public String getDttCuenFechaCreacion() { return dttCuenFechaCreacion; } // Cambiado a String
    public void setDttCuenFechaCreacion(String dttCuenFechaCreacion) { this.dttCuenFechaCreacion = dttCuenFechaCreacion; } // Cambiado a String
    public int getIntCuenContMov() { return intCuenContMov; }
    public void setIntCuenContMov(int intCuenContMov) { this.intCuenContMov = intCuenContMov; }
    public String getVchCuenEstado() { return vchCuenEstado; }
    public void setVchCuenEstado(String vchCuenEstado) { this.vchCuenEstado = vchCuenEstado; }
}