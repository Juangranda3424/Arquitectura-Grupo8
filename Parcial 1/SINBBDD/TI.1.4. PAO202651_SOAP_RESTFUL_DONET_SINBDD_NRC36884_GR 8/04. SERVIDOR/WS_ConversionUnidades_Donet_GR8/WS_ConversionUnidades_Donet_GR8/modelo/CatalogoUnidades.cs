namespace WS_ConversionUnidades_Donet_GR8.modelo
{
    /// <summary>
    /// Entrada inmutable del catálogo: clave (nombre del enum), etiqueta legible y símbolo.
    /// </summary>
    public sealed record UnidadInfo(string clave, string etiqueta, string simbolo);

    /// <summary>
    /// Catálogo en memoria de las unidades soportadas por el servidor (SINBDD).
    /// Las claves coinciden con los nombres de UnidadLongitud, UnidadMasa y UnidadTemperatura,
    /// de modo que el catálogo publicado y el motor de conversión no puedan divergir.
    /// </summary>
    public static class CatalogoUnidades
    {
        public const string LONGITUD = "LONGITUD";
        public const string MASA = "MASA";
        public const string TEMPERATURA = "TEMPERATURA";

        // Lista ordenada de pares para conservar el orden LONGITUD, MASA, TEMPERATURA al serializar.
        private static readonly IReadOnlyList<KeyValuePair<string, IReadOnlyList<UnidadInfo>>> CATALOGO =
            new List<KeyValuePair<string, IReadOnlyList<UnidadInfo>>>
            {
                new(LONGITUD, new[]
                {
                    new UnidadInfo("CENTIMETRO", "Centímetro", "cm"),
                    new UnidadInfo("METRO", "Metro", "m"),
                    new UnidadInfo("PIE", "Pie", "ft"),
                    new UnidadInfo("YARDA", "Yarda", "yd"),
                    new UnidadInfo("MILLA", "Milla", "mi"),
                }),
                new(MASA, new[]
                {
                    new UnidadInfo("GRAMO", "Gramo", "g"),
                    new UnidadInfo("KILOGRAMO", "Kilogramo", "kg"),
                    new UnidadInfo("ONZA", "Onza", "oz"),
                    new UnidadInfo("LIBRA", "Libra", "lb"),
                    new UnidadInfo("QUINTAL", "Quintal", "qq"),
                }),
                new(TEMPERATURA, new[]
                {
                    new UnidadInfo("CELSIUS", "Celsius", "°C"),
                    new UnidadInfo("FAHRENHEIT", "Fahrenheit", "°F"),
                    new UnidadInfo("KELVIN", "Kelvin", "K"),
                    new UnidadInfo("NEWTON", "Newton", "°N"),
                    new UnidadInfo("REAUMUR", "Réaumur", "°Ré"),
                }),
            }.AsReadOnly();

        /// <summary>
        /// Catálogo completo en el orden LONGITUD, MASA, TEMPERATURA.
        /// </summary>
        public static IReadOnlyList<KeyValuePair<string, IReadOnlyList<UnidadInfo>>> catalogo() => CATALOGO;

        /// <summary>
        /// Unidades de la magnitud (sin distinguir mayúsculas ni espacios); lista vacía si es desconocida.
        /// </summary>
        public static IReadOnlyList<UnidadInfo> unidadesDe(string? magnitud)
        {
            string clave = normalizar(magnitud);
            foreach (var entrada in CATALOGO)
            {
                if (entrada.Key == clave)
                {
                    return entrada.Value;
                }
            }
            return Array.Empty<UnidadInfo>();
        }

        /// <summary>
        /// Entrada del catálogo para la magnitud y clave indicadas, o null si no existe.
        /// </summary>
        public static UnidadInfo? buscar(string? magnitud, string? clave)
        {
            if (clave == null)
            {
                return null;
            }
            string claveNormalizada = normalizar(clave);
            return unidadesDe(magnitud).FirstOrDefault(unidad => unidad.clave == claveNormalizada);
        }

        private static string normalizar(string? valor) => valor == null ? "" : valor.Trim().ToUpperInvariant();
    }
}
