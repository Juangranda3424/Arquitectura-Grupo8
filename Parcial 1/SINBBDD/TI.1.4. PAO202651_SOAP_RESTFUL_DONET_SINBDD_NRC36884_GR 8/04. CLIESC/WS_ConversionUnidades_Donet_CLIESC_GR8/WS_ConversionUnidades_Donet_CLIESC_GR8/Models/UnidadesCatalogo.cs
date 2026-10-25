namespace WS_ConversionUnidades_Donet_CLIESC_GR8.Models
{
    public record Unidad(string Valor, string Etiqueta);

    /// <summary>
    /// Nombres legibles de las magnitudes. Las unidades de cada magnitud ya no están fijas en el cliente:
    /// se obtienen del servicio con GET /catalogo (RestClienteServicio.ObtenerCatalogoAsync).
    /// </summary>
    public static class UnidadesCatalogo
    {
        public static readonly IReadOnlyDictionary<string, string> Magnitudes = new Dictionary<string, string>
        {
            ["LONGITUD"] = "Longitud",
            ["MASA"] = "Masa",
            ["TEMPERATURA"] = "Temperatura"
        };

        /// <summary>Nombre legible de la magnitud; si el servicio publica una nueva, se muestra su clave.</summary>
        public static string EtiquetaMagnitud(string clave) =>
            Magnitudes.TryGetValue(clave, out var etiqueta) ? etiqueta : clave;
    }
}
