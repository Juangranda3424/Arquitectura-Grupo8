package ec.edu.monster.modelo;

public enum UnidadMasa {
    GRAMO(1.0),
    KILOGRAMO(1000.0),
    ONZA(28.3495),
    LIBRA(453.592),
    QUINTAL(100000.0); // Quintal (100 kg)

    private final double factorEnGramos;

    UnidadMasa(double factorEnGramos) {
        this.factorEnGramos = factorEnGramos;
    }

    public double convertirA(double valor, UnidadMasa destino) {
        double valorEnGramos = valor * this.factorEnGramos;
        return valorEnGramos / destino.factorEnGramos;
    }
}
