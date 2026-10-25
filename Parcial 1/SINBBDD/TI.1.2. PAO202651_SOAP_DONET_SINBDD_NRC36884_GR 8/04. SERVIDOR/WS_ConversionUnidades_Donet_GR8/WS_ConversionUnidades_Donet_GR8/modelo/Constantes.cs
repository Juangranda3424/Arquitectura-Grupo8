using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace WS_ConversionUnidades_Donet_GR8.modelo
{
    public static class Constantes
    {
        // Cero absoluto expresado en grados Celsius
        public const double CERO_ABSOLUTO_CELSIUS = -273.15;

        // Credenciales
        public const string usuario = "MONSTER";
        public const string contrasena = "MONSTER9";

        // Lista de Tokens Activos (en C# se usa HashSet<string>)
        // Nota: Las colecciones no pueden ser 'const', por lo que se declaran como 'static readonly'
        public static readonly HashSet<string> tokensActivos = new HashSet<string>();

        // El namespace
        public const string NAMESPACE = "http://localhost:8080/";
    }
}