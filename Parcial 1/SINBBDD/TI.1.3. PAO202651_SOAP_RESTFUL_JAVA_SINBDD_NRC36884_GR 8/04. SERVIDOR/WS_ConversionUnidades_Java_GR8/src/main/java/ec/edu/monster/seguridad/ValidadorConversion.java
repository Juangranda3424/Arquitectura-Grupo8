package ec.edu.monster.seguridad;

import ec.edu.monster.dto.ConversionDTO;
import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.UnidadTemperatura;

public class ValidadorConversion {

    /**
     * Valida que la estructura del DTO y sus campos no sean nulos o vacíos.
     */
    public void validarEstructuraEntrada(ConversionDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Los datos de la conversión no pueden ser nulos.");
        }
        if (dto.getValorOrigen() == null) {
            throw new IllegalArgumentException("El valor numérico de origen es obligatorio y debe ser un número válido.");
        }
        if (dto.getTipoMagnitud() == null || dto.getTipoMagnitud().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de magnitud es obligatorio.");
        }
        if (dto.getUnidadOrigen() == null || dto.getUnidadOrigen().trim().isEmpty()) {
            throw new IllegalArgumentException("La unidad de origen es obligatoria.");
        }
        if (dto.getUnidadDestino() == null || dto.getUnidadDestino().trim().isEmpty()) {
            throw new IllegalArgumentException("La unidad de destino es obligatoria.");
        }
        if (Double.isNaN(dto.getValorOrigen()) || Double.isInfinite(dto.getValorOrigen())) {
            throw new IllegalArgumentException("El valor numérico ingresado no es válido.");
        }
    }

    /**
     * Valida reglas de seguridad física (Valores no negativos para Longitud y Masa).
     */
    public void validarValorNoNegativo(double valor, String magnitud) {
        if (valor < 0) {
            throw new IllegalArgumentException("El valor para la magnitud '" + magnitud + "' no puede ser negativo.");
        }
    }

    /**
     * Valida que la temperatura no esté por debajo del Cero Absoluto definido en Constantes.
     */
    public void validarTemperaturaMinima(double valor, UnidadTemperatura unidadOrigen) {
        double enCelsius = unidadOrigen.aCelsius(valor);
        if (enCelsius < Constantes.CERO_ABSOLUTO_CELSIUS) {
            throw new IllegalArgumentException(
                "La temperatura ingresada (" + valor + " " + unidadOrigen + ") está por debajo del cero absoluto (" 
                + Constantes.CERO_ABSOLUTO_CELSIUS + " °C)."
            );
        }
    }

    /**
     * Verifica la existencia de una unidad dentro de su Enum correspondiente.
     */
    public <E extends Enum<E>> E buscarEnumValido(Class<E> enumClass, String nombreUnidad, String magnitud) {
        try {
            return Enum.valueOf(enumClass, nombreUnidad);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "La unidad '" + nombreUnidad + "' no es válida para la magnitud '" + magnitud + "'."
            );
        }
    }
}
