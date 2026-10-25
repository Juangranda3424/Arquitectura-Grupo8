namespace WS_ConversionUnidades_Donet_GR8.modelo
{
    public enum UnidadMasa
    {
        GRAMO,
        KILOGRAMO,
        ONZA,
        LIBRA,
        QUINTAL
    }

    public static class UnidadMasaExtensions
    {
        // Método auxiliar privado para emular la variable interna 'factorEnGramos'
        private static double GetFactorEnGramos(this UnidadMasa unidad)
        {
            switch (unidad)
            {
                case UnidadMasa.GRAMO:
                    return 1.0;
                case UnidadMasa.KILOGRAMO:
                    return 1000.0;
                case UnidadMasa.ONZA:
                    return 28.3495;
                case UnidadMasa.LIBRA:
                    return 453.592;
                case UnidadMasa.QUINTAL:
                    return 100000.0;
                default:
                    throw new ArgumentOutOfRangeException(nameof(unidad), "Unidad de masa no soportada");
            }
        }

        // Método principal de conversión
        public static double convertirA(this UnidadMasa origen, double valor, UnidadMasa destino)
        {
            double valorEnGramos = valor * origen.GetFactorEnGramos();
            return valorEnGramos / destino.GetFactorEnGramos();
        }
    }
}