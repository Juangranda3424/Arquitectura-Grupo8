    package ec.edu.monster.servicio;

import ec.edu.monster.dto.ConversionDTO;
import ec.edu.monster.modelo.UnidadLongitud;
import ec.edu.monster.modelo.UnidadMasa;
import ec.edu.monster.modelo.UnidadTemperatura;
import ec.edu.monster.seguridad.ValidadorConversion;

public class ConversorServicio {

    private final ValidadorConversion validador = new ValidadorConversion();

    public ConversionDTO procesarConversion(ConversionDTO dto) {
        // Validar Datos
        validador.validarEstructuraEntrada(dto);

        double resultado = 0.0;
        String tipo = dto.getTipoMagnitud().trim().toUpperCase();
        String unidadOrigenStr = dto.getUnidadOrigen().trim().toUpperCase();
        String unidadDestinoStr = dto.getUnidadDestino().trim().toUpperCase();
        
        //Procesar Datos
        switch (tipo) {
            case "LONGITUD" -> {
                validador.validarValorNoNegativo(dto.getValorOrigen(), tipo);
                UnidadLongitud origen = validador.buscarEnumValido(UnidadLongitud.class, unidadOrigenStr, tipo);
                UnidadLongitud destino = validador.buscarEnumValido(UnidadLongitud.class, unidadDestinoStr, tipo);
                resultado = origen.convertirA(dto.getValorOrigen(), destino);
            }
            case "MASA" -> {
                validador.validarValorNoNegativo(dto.getValorOrigen(), tipo);
                UnidadMasa origen = validador.buscarEnumValido(UnidadMasa.class, unidadOrigenStr, tipo);
                UnidadMasa destino = validador.buscarEnumValido(UnidadMasa.class, unidadDestinoStr, tipo);
                resultado = origen.convertirA(dto.getValorOrigen(), destino);
            }
            case "TEMPERATURA" -> {
                UnidadTemperatura origen = validador.buscarEnumValido(UnidadTemperatura.class, unidadOrigenStr, tipo);
                UnidadTemperatura destino = validador.buscarEnumValido(UnidadTemperatura.class, unidadDestinoStr, tipo);
                validador.validarTemperaturaMinima(dto.getValorOrigen(), origen);
                resultado = origen.convertirA(dto.getValorOrigen(), destino);
            }
            default -> throw new IllegalArgumentException(
                "La magnitud '" + tipo + "' no es válida. Debe ser LONGITUD, MASA o TEMPERATURA."
            );
        }

        dto.setValorResultado(resultado);
        return dto;
    }
}