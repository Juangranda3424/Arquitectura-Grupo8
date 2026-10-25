using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using WS_ConversionUnidades_Donet_GR8.dto;
using WS_ConversionUnidades_Donet_GR8.modelo;
using WS_ConversionUnidades_Donet_GR8.seguridad;

namespace WS_ConversionUnidades_Donet_GR8.servicio
{
    public class ConversorServicio
    {
        private readonly ValidadorConversion validador = new ValidadorConversion();

        public ConversionDTO procesarConversion(ConversionDTO dto)
        {
            // Validar Datos
            validador.validarEstructuraEntrada(dto);

            double resultado = 0.0;
            string tipo = dto.TipoMagnitud.Trim().ToUpper();
            string unidadOrigenStr = dto.UnidadOrigen.Trim().ToUpper();
            string unidadDestinoStr = dto.UnidadDestino.Trim().ToUpper();

            // Procesar Datos (usando switch tradicional en C#)
            switch (tipo)
            {
                case "LONGITUD":
                    validador.validarValorNoNegativo(dto.ValorOrigen.Value, tipo);
                    UnidadLongitud origenLongitud = validador.buscarEnumValido<UnidadLongitud>(unidadOrigenStr, tipo);
                    UnidadLongitud destinoLongitud = validador.buscarEnumValido<UnidadLongitud>(unidadDestinoStr, tipo);
                    resultado = origenLongitud.convertirA(dto.ValorOrigen.Value, destinoLongitud);
                    break;

                case "MASA":
                    validador.validarValorNoNegativo(dto.ValorOrigen.Value, tipo);
                    UnidadMasa origenMasa = validador.buscarEnumValido<UnidadMasa>(unidadOrigenStr, tipo);
                    UnidadMasa destinoMasa = validador.buscarEnumValido<UnidadMasa>(unidadDestinoStr, tipo);
                    resultado = origenMasa.convertirA(dto.ValorOrigen.Value, destinoMasa);
                    break;

                case "TEMPERATURA":
                    UnidadTemperatura origenTemp = validador.buscarEnumValido<UnidadTemperatura>(unidadOrigenStr, tipo);
                    UnidadTemperatura destinoTemp = validador.buscarEnumValido<UnidadTemperatura>(unidadDestinoStr, tipo);
                    validador.validarTemperaturaMinima(dto.ValorOrigen.Value, origenTemp);
                    resultado = origenTemp.convertirA(dto.ValorOrigen.Value, destinoTemp);
                    break;

                default:
                    throw new ArgumentException(
                        $"La magnitud '{tipo}' no es válida. Debe ser LONGITUD, MASA o TEMPERATURA."
                    );
            }

            dto.ValorResultado = resultado;
            return dto;
        }
    }

}