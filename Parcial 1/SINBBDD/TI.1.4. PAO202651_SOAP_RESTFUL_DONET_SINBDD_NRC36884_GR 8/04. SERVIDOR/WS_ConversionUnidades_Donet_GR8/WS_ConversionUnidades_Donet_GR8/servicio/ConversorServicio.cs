using WS_ConversionUnidades_Donet_GR8.dto;
using WS_ConversionUnidades_Donet_GR8.modelo;
using WS_ConversionUnidades_Donet_GR8.seguridad;

namespace WS_ConversionUnidades_Donet_GR8.servicio
{
    public class ConversorServicio
    {
        private readonly ValidadorConversion validador = new ValidadorConversion();

        public ConversionDTO procesarConversion(ConversionDTO? dto)
        {
            // Validar datos (garantiza que los campos no son nulos ni vacíos)
            validador.validarEstructuraEntrada(dto);

            double valor = dto!.ValorOrigen!.Value;
            string tipo = dto.TipoMagnitud!.Trim().ToUpperInvariant();
            string unidadOrigenStr = dto.UnidadOrigen!.Trim().ToUpperInvariant();
            string unidadDestinoStr = dto.UnidadDestino!.Trim().ToUpperInvariant();
            double resultado;

            switch (tipo)
            {
                case CatalogoUnidades.LONGITUD:
                    validador.validarValorNoNegativo(valor, tipo);
                    UnidadLongitud origenLongitud = validador.buscarEnumValido<UnidadLongitud>(unidadOrigenStr, tipo);
                    UnidadLongitud destinoLongitud = validador.buscarEnumValido<UnidadLongitud>(unidadDestinoStr, tipo);
                    resultado = origenLongitud.convertirA(valor, destinoLongitud);
                    break;

                case CatalogoUnidades.MASA:
                    validador.validarValorNoNegativo(valor, tipo);
                    UnidadMasa origenMasa = validador.buscarEnumValido<UnidadMasa>(unidadOrigenStr, tipo);
                    UnidadMasa destinoMasa = validador.buscarEnumValido<UnidadMasa>(unidadDestinoStr, tipo);
                    resultado = origenMasa.convertirA(valor, destinoMasa);
                    break;

                case CatalogoUnidades.TEMPERATURA:
                    UnidadTemperatura origenTemp = validador.buscarEnumValido<UnidadTemperatura>(unidadOrigenStr, tipo);
                    UnidadTemperatura destinoTemp = validador.buscarEnumValido<UnidadTemperatura>(unidadDestinoStr, tipo);
                    validador.validarTemperaturaMinima(valor, origenTemp);
                    resultado = origenTemp.convertirA(valor, destinoTemp);
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
