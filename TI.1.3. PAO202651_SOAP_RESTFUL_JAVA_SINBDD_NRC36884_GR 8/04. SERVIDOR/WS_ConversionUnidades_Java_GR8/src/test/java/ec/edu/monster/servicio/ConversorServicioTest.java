package ec.edu.monster.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ec.edu.monster.dto.ConversionDTO;

class ConversorServicioTest {

    private final ConversorServicio servicio = new ConversorServicio();

    @Test
    void convierteLongitud() {
        ConversionDTO dto = servicio.procesarConversion(new ConversionDTO(100.0, null, "CENTIMETRO", "METRO", "LONGITUD"));

        assertEquals(1.0, dto.getValorResultado(), 1e-9);
        assertEquals("LONGITUD", dto.getTipoMagnitud());
        assertEquals(100.0, dto.getValorOrigen());
    }

    @Test
    void convierteMasa() {
        ConversionDTO dto = servicio.procesarConversion(new ConversionDTO(16.0, null, "ONZA", "LIBRA", "MASA"));

        assertEquals(1.0, dto.getValorResultado(), 1e-9);
    }

    @Test
    void convierteTemperatura() {
        ConversionDTO dto = servicio.procesarConversion(new ConversionDTO(100.0, null, "CELSIUS", "FAHRENHEIT", "TEMPERATURA"));

        assertEquals(212.0, dto.getValorResultado(), 1e-9);
    }

    @Test
    void normalizaMagnitudYUnidadesConEspaciosYminusculas() {
        ConversionDTO dto = servicio.procesarConversion(new ConversionDTO(1.0, null, " milla ", " yarda ", " longitud "));

        assertEquals(1759.9956255468067, dto.getValorResultado(), 1e-9);
    }

    @Test
    void devuelveElMismoDtoEnriquecidoConElResultado() {
        ConversionDTO entrada = new ConversionDTO(0.0, null, "CELSIUS", "KELVIN", "TEMPERATURA");

        assertSame(entrada, servicio.procesarConversion(entrada));
        assertEquals(273.15, entrada.getValorResultado(), 1e-9);
    }

    @Test
    void magnitudDesconocidaUsaElMensajeExactoDeTi11() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.procesarConversion(new ConversionDTO(1.0, null, "METRO", "PIE", "VOLUMEN")));

        assertEquals("La magnitud 'VOLUMEN' no es válida. Debe ser LONGITUD, MASA o TEMPERATURA.", error.getMessage());
    }

    @Test
    void laMagnitudDesconocidaSeReportaNormalizadaEnMayusculas() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.procesarConversion(new ConversionDTO(1.0, null, "METRO", "PIE", " volumen ")));

        assertEquals("La magnitud 'VOLUMEN' no es válida. Debe ser LONGITUD, MASA o TEMPERATURA.", error.getMessage());
    }

    @Test
    void longitudNegativaEsRechazada() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.procesarConversion(new ConversionDTO(-1.0, null, "METRO", "PIE", "LONGITUD")));

        assertEquals("El valor para la magnitud 'LONGITUD' no puede ser negativo.", error.getMessage());
    }

    @Test
    void masaNegativaEsRechazada() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.procesarConversion(new ConversionDTO(-0.5, null, "GRAMO", "LIBRA", "MASA")));

        assertEquals("El valor para la magnitud 'MASA' no puede ser negativo.", error.getMessage());
    }

    @Test
    void temperaturaBajoElCeroAbsolutoEsRechazada() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.procesarConversion(new ConversionDTO(-300.0, null, "CELSIUS", "KELVIN", "TEMPERATURA")));

        assertEquals("La temperatura ingresada (-300.0 CELSIUS) está por debajo del cero absoluto (-273.15 °C).",
                error.getMessage());
    }

    @Test
    void kelvinNegativoEsRechazado() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.procesarConversion(new ConversionDTO(-1.0, null, "KELVIN", "CELSIUS", "TEMPERATURA")));

        assertEquals("La temperatura ingresada (-1.0 KELVIN) está por debajo del cero absoluto (-273.15 °C).",
                error.getMessage());
    }

    @Test
    void estructuraInvalidaSeRechazaAntesDeConvertir() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.procesarConversion(new ConversionDTO(null, null, "METRO", "PIE", "LONGITUD")));

        assertEquals("El valor numérico de origen es obligatorio y debe ser un número válido.", error.getMessage());
    }

    @Test
    void unidadInvalidaParaLaMagnitudUsaElMensajeExactoDeTi11() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> servicio.procesarConversion(new ConversionDTO(1.0, null, "METRO", "LIBRA", "MASA")));

        assertEquals("La unidad 'METRO' no es válida para la magnitud 'MASA'.", error.getMessage());
    }
}
