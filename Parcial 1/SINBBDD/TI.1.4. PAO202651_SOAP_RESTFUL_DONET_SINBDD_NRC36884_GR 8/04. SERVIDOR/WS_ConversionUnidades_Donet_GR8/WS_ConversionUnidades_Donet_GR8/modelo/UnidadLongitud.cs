namespace WS_ConversionUnidades_Donet_GR8.modelo
{
    public enum UnidadLongitud
    {
        CENTIMETRO,
        METRO,
        PIE,
        YARDA,
        MILLA
    }

    public static class UnidadLongitudExtensions
    {
        // Método auxiliar privado para emular el factor en metros de cada unidad
        private static double GetFactorEnMetros(this UnidadLongitud unidad)
        {
            switch (unidad)
            {
                case UnidadLongitud.CENTIMETRO:
                    return 0.01;
                case UnidadLongitud.METRO:
                    return 1.0;
                case UnidadLongitud.PIE:
                    return 0.3048;
                case UnidadLongitud.YARDA:
                    return 0.9144;
                case UnidadLongitud.MILLA:
                    return 1609.34;
                default:
                    throw new ArgumentOutOfRangeException(nameof(unidad), "Unidad de longitud no soportada");
            }
        }

        // Método principal de conversión
        public static double convertirA(this UnidadLongitud origen, double valor, UnidadLongitud destino)
        {
            double valorEnMetros = valor * origen.GetFactorEnMetros();
            return valorEnMetros / destino.GetFactorEnMetros();
        }
    }

}