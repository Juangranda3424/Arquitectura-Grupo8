using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace WS_ConversionUnidades_Donet_GR8.modelo
{
    public enum UnidadTemperatura
    {
        CELSIUS,
        FAHRENHEIT,
        KELVIN,
        NEWTON,
        REAUMUR
    }

    // Clase estática para extender la funcionalidad del enum
    public static class UnidadTemperaturaExtensions
    {
        // Convierte el valor de cualquier unidad a Celsius
        // La palabra reservada 'this' hace que el método actúe sobre el enum
        public static double aCelsius(this UnidadTemperatura origen, double valor)
        {
            switch (origen)
            {
                case UnidadTemperatura.CELSIUS:
                    return valor;
                case UnidadTemperatura.FAHRENHEIT:
                    return (valor - 32) * 5.0 / 9.0;
                case UnidadTemperatura.KELVIN:
                    return valor - 273.15;
                case UnidadTemperatura.NEWTON:
                    return valor * 100.0 / 33.0;
                case UnidadTemperatura.REAUMUR:
                    return valor * 5.0 / 4.0;
                default:
                    throw new ArgumentOutOfRangeException(nameof(origen), "Unidad no soportada");
            }
        }

        // Convierte desde Celsius a la unidad de esta instancia
        public static double desdeCelsius(this UnidadTemperatura destino, double celsius)
        {
            switch (destino)
            {
                case UnidadTemperatura.CELSIUS:
                    return celsius;
                case UnidadTemperatura.FAHRENHEIT:
                    return (celsius * 9.0 / 5.0) + 32;
                case UnidadTemperatura.KELVIN:
                    return celsius + 273.15;
                case UnidadTemperatura.NEWTON:
                    return celsius * 33.0 / 100.0;
                case UnidadTemperatura.REAUMUR:
                    return celsius * 4.0 / 5.0;
                default:
                    throw new ArgumentOutOfRangeException(nameof(destino), "Unidad no soportada");
            }
        }

        // Método principal para transformar entre cualquier par de temperaturas
        public static double convertirA(this UnidadTemperatura origen, double valor, UnidadTemperatura destino)
        {
            double enCelsius = origen.aCelsius(valor);
            return destino.desdeCelsius(enCelsius);
        }
    }

}