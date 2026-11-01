package ec.edu.monster.model;

public class DepositoRequest {
    private String cuenta;
    private double monto;

    public DepositoRequest() {
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


