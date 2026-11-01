package ec.edu.monster.model;

public class RetiroRequest {
    private String cuenta;
    private double monto;

    public RetiroRequest() {
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }
}


