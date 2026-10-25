package ec.edu.monster.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Casos de referencia tomados de TI.1.1 (PruebaModelos) con los mismos factores de paridad.
 */
class UnidadLongitudTest {

    private static final double DELTA = 1e-9;

    @Test
    void cienCentimetrosSonUnMetro() {
        assertEquals(1.0, UnidadLongitud.CENTIMETRO.convertirA(100, UnidadLongitud.METRO), DELTA);
    }

    @Test
    void unaMillaSonMilSetecientasSesentaYardas() {
        assertEquals(1760.0, UnidadLongitud.MILLA.convertirA(1, UnidadLongitud.YARDA), 0.01);
    }

    @Test
    void diezPiesSonTrescientosCuatroPuntoOchoCentimetros() {
        assertEquals(304.8, UnidadLongitud.PIE.convertirA(10, UnidadLongitud.CENTIMETRO), 1e-6);
    }

    @Test
    void laConversionAlMismoTipoConservaElValor() {
        assertEquals(42.5, UnidadLongitud.METRO.convertirA(42.5, UnidadLongitud.METRO), DELTA);
    }

    @Test
    void laIdaYVueltaEsEquivalente() {
        double yardas = UnidadLongitud.METRO.convertirA(25.0, UnidadLongitud.YARDA);
        assertEquals(25.0, UnidadLongitud.YARDA.convertirA(yardas, UnidadLongitud.METRO), DELTA);
    }
}
