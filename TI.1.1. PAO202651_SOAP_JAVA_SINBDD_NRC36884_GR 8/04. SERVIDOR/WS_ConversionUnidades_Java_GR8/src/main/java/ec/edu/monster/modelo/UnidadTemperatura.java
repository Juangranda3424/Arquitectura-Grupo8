package ec.edu.monster.modelo;

public enum UnidadTemperatura {
    CELSIUS,
    FAHRENHEIT,
    KELVIN,
    NEWTON,
    REAUMUR;

    // Convierte el valor de cualquier unidad a Celsius
    public double aCelsius(double valor) {
        return switch (this) {
            case CELSIUS -> valor;
            case FAHRENHEIT -> (valor - 32) * 5.0 / 9.0;
            case KELVIN -> valor - 273.15;
            case NEWTON -> valor * 100.0 / 33.0;
            case REAUMUR -> valor * 5.0 / 4.0;
        };
    }

    // Convierte desde Celsius a la unidad de esta instancia
    public double desdeCelsius(double celsius) {
        return switch (this) {
            case CELSIUS -> celsius;
            case FAHRENHEIT -> (celsius * 9.0 / 5.0) + 32;
            case KELVIN -> celsius + 273.15;
            case NEWTON -> celsius * 33.0 / 100.0;
            case REAUMUR -> celsius * 4.0 / 5.0;
        };
    }

    // Método principal para transformar entre cualquier par de temperaturas
    public double convertirA(double valor, UnidadTemperatura destino) {
        double enCelsius = this.aCelsius(valor);
        return destino.desdeCelsius(enCelsius);
    }
}