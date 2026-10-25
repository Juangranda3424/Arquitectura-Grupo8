using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using WS_ConversionUnidades_Donet_GR8.dto;
using WS_ConversionUnidades_Donet_GR8.modelo;

namespace WS_ConversionUnidades_Donet_GR8.seguridad
{
    public class ValidadorConversion
    {
        /// <summary>
        /// Valida que la estructura del DTO y sus campos no sean nulos o vacíos.
        /// </summary>
        public void validarEstructuraEntrada(ConversionDTO dto)
        {
            if (dto == null)
            {
                throw new ArgumentException("Los datos de la conversión no pueden ser nulos.");
            }
            if (dto.ValorOrigen == null)
            {
                throw new ArgumentException("El valor numérico de origen es obligatorio y debe ser un número válido.");
            }
            if (string.IsNullOrWhiteSpace(dto.TipoMagnitud))
            {
                throw new ArgumentException("El tipo de magnitud es obligatorio.");
            }
            if (string.IsNullOrWhiteSpace(dto.UnidadOrigen))
            {
                throw new ArgumentException("La unidad de origen es obligatoria.");
            }
            if (string.IsNullOrWhiteSpace(dto.UnidadDestino))
            {
                throw new ArgumentException("La unidad de destino es obligatoria.");
            }
            if (double.IsNaN(dto.ValorOrigen.Value) || double.IsInfinity(dto.ValorOrigen.Value))
            {
                throw new ArgumentException("El valor numérico ingresado no es válido.");
            }
        }

        /// <summary>
        /// Valida reglas de seguridad física (Valores no negativos para Longitud y Masa).
        /// </summary>
        public void validarValorNoNegativo(double valor, string magnitud)
        {
            if (valor < 0)
            {
                throw new ArgumentException($"El valor para la magnitud '{magnitud}' no puede ser negativo.");
            }
        }

        /// <summary>
        /// Valida que la temperatura no esté por debajo del Cero Absoluto definido en Constantes.
        /// </summary>
        public void validarTemperaturaMinima(double valor, UnidadTemperatura unidadOrigen)
        {
            double enCelsius = unidadOrigen.aCelsius(valor);
            if (enCelsius < Constantes.CERO_ABSOLUTO_CELSIUS)
            {
                throw new ArgumentException(
                    $"La temperatura ingresada ({valor} {unidadOrigen}) está por debajo del cero absoluto ({Constantes.CERO_ABSOLUTO_CELSIUS} °C)."
                );
            }
        }

        /// <summary>
        /// Verifica la existencia de una unidad dentro de su Enum correspondiente.
        /// </summary>
        public T buscarEnumValido<T>(string nombreUnidad, string magnitud) where T : struct, Enum
        {
            if (Enum.TryParse<T>(nombreUnidad, true, out T resultado))
            {
                return resultado;
            }

            throw new ArgumentException($"La unidad '{nombreUnidad}' no es válida para la magnitud '{magnitud}'.");
        }
    }

}