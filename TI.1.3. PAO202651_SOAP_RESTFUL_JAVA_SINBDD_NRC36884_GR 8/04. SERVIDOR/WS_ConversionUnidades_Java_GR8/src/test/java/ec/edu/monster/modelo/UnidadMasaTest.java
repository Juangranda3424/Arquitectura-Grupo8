package ec.edu.monster.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Casos de referencia tomados de TI.1.1 (PruebaModelos) con los mismos factores de paridad.
 */
class UnidadMasaTest {

    private static final double DELTA = 1e-9;

    @Test
    void unKilogramoSonMilGramos() {
        assertEquals(1000.0, UnidadMasa.KILOGRAMO.convertirA(1, UnidadMasa.GRAMO), DELTA);
    }

    @Test
    void unQuintalSonDoscientasVeinteLibrasAproximadamente() {
        assertEquals(220.46244201837774, UnidadMasa.QUINTAL.convertirA(1, UnidadMasa.LIBRA), 1e-9);
    }

    @Test
    void dieciseisOnzasSonAproximadamenteUnaLibra() {
        assertEquals(1.0, UnidadMasa.ONZA.convertirA(16, UnidadMasa.LIBRA), 1e-9);
    }

    @Test
    void laConversionAlMismoTipoConservaElValor() {
        assertEquals(7.25, UnidadMasa.GRAMO.convertirA(7.25, UnidadMasa.GRAMO), DELTA);
    }

    @Test
    void laIdaYVueltaEsEquivalente() {
        double onzas = UnidadMasa.LIBRA.convertirA(3.0, UnidadMasa.ONZA);
        assertEquals(3.0, UnidadMasa.ONZA.convertirA(onzas, UnidadMasa.LIBRA), DELTA);
    }
}
