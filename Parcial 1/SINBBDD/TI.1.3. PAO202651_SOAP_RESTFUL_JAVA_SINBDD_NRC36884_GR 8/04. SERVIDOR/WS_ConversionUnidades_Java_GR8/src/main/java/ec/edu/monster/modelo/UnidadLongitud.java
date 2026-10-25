package ec.edu.monster.modelo;

public enum UnidadLongitud {
    CENTIMETRO(0.01),
    METRO(1.0),
    PIE(0.3048),
    YARDA(0.9144),
    MILLA(1609.34);

    private final double factorEnMetros;

    UnidadLongitud(double factorEnMetros) {
        this.factorEnMetros = factorEnMetros;
    }

    public double convertirA(double valor, UnidadLongitud destino) {
        double valorEnMetros = valor * this.factorEnMetros;
        return valorEnMetros / destino.factorEnMetros;
    }
}
