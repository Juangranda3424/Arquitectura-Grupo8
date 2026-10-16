package ec.edu.monster.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Casos de referencia tomados de TI.1.1 (PruebaModelos) con las mismas formulas de paridad.
 */
class UnidadTemperaturaTest {

    private static final double DELTA = 1e-9;

    @Test
    void cienCelsiusSonDoscientosDoceFahrenheit() {
        assertEquals(212.0, UnidadTemperatura.CELSIUS.convertirA(100, UnidadTemperatura.FAHRENHEIT), DELTA);
    }

    @Test
    void ceroCelsiusSonDoscientosSetentaYTresPuntoQuinceKelvin() {
        assertEquals(273.15, UnidadTemperatura.CELSIUS.convertirA(0, UnidadTemperatura.KELVIN), DELTA);
    }

    @Test
    void cienCelsiusSonTreintaYTresNewton() {
        assertEquals(33.0, UnidadTemperatura.CELSIUS.convertirA(100, UnidadTemperatura.NEWTON), DELTA);
    }

    @Test
    void ochentaReaumurSonCienCelsius() {
        assertEquals(100.0, UnidadTemperatura.REAUMUR.convertirA(80, UnidadTemperatura.CELSIUS), DELTA);
    }

    @Test
    void treintaYDosFahrenheitSonCeroCelsius() {
        assertEquals(0.0, UnidadTemperatura.FAHRENHEIT.convertirA(32, UnidadTemperatura.CELSIUS), DELTA);
    }

    @Test
    void elCeroAbsolutoKelvinEsMenosDoscientosSetentaYTresPuntoQuinceCelsius() {
        assertEquals(-273.15, UnidadTemperatura.KELVIN.convertirA(0, UnidadTemperatura.CELSIUS), DELTA);
    }

    @Test
    void laIdaYVueltaEsEquivalente() {
        double fahrenheit = UnidadTemperatura.CELSIUS.convertirA(37.5, UnidadTemperatura.FAHRENHEIT);
        assertEquals(37.5, UnidadTemperatura.FAHRENHEIT.convertirA(fahrenheit, UnidadTemperatura.CELSIUS), DELTA);
    }
}
