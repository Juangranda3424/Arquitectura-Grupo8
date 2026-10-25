namespace WS_ConversionUnidades_Donet_CLICON_GR8.Models
{
    public record Unidad(string Valor, string Etiqueta);

    /// <summary>
    /// Magnitudes y unidades que acepta el servicio (enums UnidadLongitud, UnidadMasa y UnidadTemperatura).
    /// </summary>
    public static class UnidadesCatalogo
    {
        public static readonly IReadOnlyDictionary<string, Unidad[]> Todas = new Dictionary<string, Unidad[]>
        {
            ["LONGITUD"] = new[]
            {
                new Unidad("CENTIMETRO", "Centímetro (cm)"),
                new Unidad("METRO", "Metro (m)"),
                new Unidad("PIE", "Pie (ft)"),
                new Unidad("YARDA", "Yarda (yd)"),
                new Unidad("MILLA", "Milla (mi)")
            },
            ["MASA"] = new[]
            {
                new Unidad("GRAMO", "Gramo (g)"),
                new Unidad("KILOGRAMO", "Kilogramo (kg)"),
                new Unidad("ONZA", "Onza (oz)"),
                new Unidad("LIBRA", "Libra (lb)"),
                new Unidad("QUINTAL", "Quintal (qq)")
            },
            ["TEMPERATURA"] = new[]
            {
                new Unidad("CELSIUS", "Celsius (°C)"),
                new Unidad("FAHRENHEIT", "Fahrenheit (°F)"),
                new Unidad("KELVIN", "Kelvin (K)"),
                new Unidad("NEWTON", "Newton (°N)"),
                new Unidad("REAUMUR", "Réaumur (°Ré)")
            }
        };

        public static readonly IReadOnlyDictionary<string, string> Magnitudes = new Dictionary<string, string>
        {
            ["LONGITUD"] = "Longitud",
            ["MASA"] = "Masa",
            ["TEMPERATURA"] = "Temperatura"
        };
    }
}
