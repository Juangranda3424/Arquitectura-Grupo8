package ec.edu.monster.seguridad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ec.edu.monster.dto.ConversionDTO;
import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.UnidadLongitud;
import ec.edu.monster.modelo.UnidadTemperatura;

/**
 * Los textos de los mensajes deben ser idénticos a los de TI.1.1 (ValidadorConversion).
 */
class ValidadorConversionTest {

    private final ValidadorConversion validador = new ValidadorConversion();

    @Test
    void dtoNuloEsRechazado() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> validador.validarEstructuraEntrada(null));
        assertEquals("Los datos de la conversión no pueden ser nulos.", error.getMessage());
    }

    @Test
    void valorOrigenNuloEsRechazado() {
        ConversionDTO dto = new ConversionDTO();
        dto.setUnidadOrigen("METRO");
        dto.setUnidadDestino("PIE");
        dto.setTipoMagnitud("LONGITUD");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> validador.validarEstructuraEntrada(dto));
        assertEquals("El valor numérico de origen es obligatorio y debe ser un número válido.", error.getMessage());
    }

    @Test
    void tipoMagnitudNuloOVacioEsRechazado() {
        ConversionDTO sinTipo = dtoBase();
        sinTipo.setTipoMagnitud(null);
        IllegalArgumentException nulo = assertThrows(IllegalArgumentException.class,
                () -> validador.validarEstructuraEntrada(sinTipo));
        assertEquals("El tipo de magnitud es obligatorio.", nulo.getMessage());

        ConversionDTO enBlanco = dtoBase();
        enBlanco.setTipoMagnitud("   ");
        IllegalArgumentException blanco = assertThrows(IllegalArgumentException.class,
                () -> validador.validarEstructuraEntrada(enBlanco));
        assertEquals("El tipo de magnitud es obligatorio.", blanco.getMessage());
    }

    @Test
    void unidadOrigenNulaOVaciaEsRechazada() {
        ConversionDTO sinUnidad = dtoBase();
        sinUnidad.setUnidadOrigen(null);
        IllegalArgumentException nulo = assertThrows(IllegalArgumentException.class,
                () -> validador.validarEstructuraEntrada(sinUnidad));
        assertEquals("La unidad de origen es obligatoria.", nulo.getMessage());

        ConversionDTO enBlanco = dtoBase();
        enBlanco.setUnidadOrigen(" ");
        IllegalArgumentException blanco = assertThrows(IllegalArgumentException.class,
                () -> validador.validarEstructuraEntrada(enBlanco));
        assertEquals("La unidad de origen es obligatoria.", blanco.getMessage());
    }

    @Test
    void unidadDestinoNulaOVaciaEsRechazada() {
        ConversionDTO sinUnidad = dtoBase();
        sinUnidad.setUnidadDestino(null);
        IllegalArgumentException nulo = assertThrows(IllegalArgumentException.class,
                () -> validador.validarEstructuraEntrada(sinUnidad));
        assertEquals("La unidad de destino es obligatoria.", nulo.getMessage());

        ConversionDTO enBlanco = dtoBase();
        enBlanco.setUnidadDestino("   ");
        IllegalArgumentException blanco = assertThrows(IllegalArgumentException.class,
                () -> validador.validarEstructuraEntrada(enBlanco));
        assertEquals("La unidad de destino es obligatoria.", blanco.getMessage());
    }

    @Test
    void valorNoNumericoEsRechazado() {
        ConversionDTO nan = dtoBase();
        nan.setValorOrigen(Double.NaN);
        IllegalArgumentException errorNaN = assertThrows(IllegalArgumentException.class,
                () -> validador.validarEstructuraEntrada(nan));
        assertEquals("El valor numérico ingresado no es válido.", errorNaN.getMessage());

        ConversionDTO infinito = dtoBase();
        infinito.setValorOrigen(Double.POSITIVE_INFINITY);
        IllegalArgumentException errorInfinito = assertThrows(IllegalArgumentException.class,
                () -> validador.validarEstructuraEntrada(infinito));
        assertEquals("El valor numérico ingresado no es válido.", errorInfinito.getMessage());
    }

    @Test
    void elOrdenDeValidacionEsElDeTi11() {
        ConversionDTO dto = new ConversionDTO();
        // valorOrigen nulo se reporta antes que el tipo de magnitud ausente
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> validador.validarEstructuraEntrada(dto));
        assertEquals("El valor numérico de origen es obligatorio y debe ser un número válido.", error.getMessage());
    }

    @Test
    void unDtoCompletoEsAceptado() {
        ConversionDTO dto = dtoBase();
        validador.validarEstructuraEntrada(dto);
        assertNotNull(dto.getValorOrigen());
    }

    @Test
    void valorNegativoEsRechazadoConElMensajeDeLaMagnitud() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> validador.validarValorNoNegativo(-1.0, "LONGITUD"));
        assertEquals("El valor para la magnitud 'LONGITUD' no puede ser negativo.", error.getMessage());
    }

    @Test
    void valorCeroEsValidoParaLongitudYmasa() {
        validador.validarValorNoNegativo(0.0, "LONGITUD");
        validador.validarValorNoNegativo(0.0, "MASA");
    }

    @Test
    void temperaturaBajoElCeroAbsolutoEsRechazada() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> validador.validarTemperaturaMinima(-300.0, UnidadTemperatura.CELSIUS));
        assertEquals("La temperatura ingresada (-300.0 CELSIUS) está por debajo del cero absoluto ("
                + Constantes.CERO_ABSOLUTO_CELSIUS + " °C).", error.getMessage());
    }

    @Test
    void temperaturaEnElCeroAbsolutoEsAceptada() {
        validador.validarTemperaturaMinima(Constantes.CERO_ABSOLUTO_CELSIUS, UnidadTemperatura.CELSIUS);
        validador.validarTemperaturaMinima(0.0, UnidadTemperatura.KELVIN);
    }

    @Test
    void unidadDesconocidaEsRechazadaConElMensajeDeTi11() {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> validador.buscarEnumValido(UnidadLongitud.class, "XXX", "LONGITUD"));
        assertEquals("La unidad 'XXX' no es válida para la magnitud 'LONGITUD'.", error.getMessage());
    }

    @Test
    void buscarEnumValidoExigeElNombreExactoDelEnum() {
        assertEquals(UnidadLongitud.METRO, validador.buscarEnumValido(UnidadLongitud.class, "METRO", "LONGITUD"));

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> validador.buscarEnumValido(UnidadLongitud.class, "metro", "LONGITUD"));
        assertEquals("La unidad 'metro' no es válida para la magnitud 'LONGITUD'.", error.getMessage());
    }

    private static ConversionDTO dtoBase() {
        return new ConversionDTO(1.0, null, "METRO", "PIE", "LONGITUD");
    }
}
