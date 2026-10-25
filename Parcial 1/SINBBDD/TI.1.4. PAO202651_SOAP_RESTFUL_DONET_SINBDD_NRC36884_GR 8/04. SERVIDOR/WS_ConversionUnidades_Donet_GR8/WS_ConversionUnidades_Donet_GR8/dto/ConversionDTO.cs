namespace WS_ConversionUnidades_Donet_GR8.dto
{
    public class ConversionDTO
    {
        public double? ValorOrigen { get; set; }
        public double? ValorResultado { get; set; }
        public string? UnidadOrigen { get; set; }
        public string? UnidadDestino { get; set; }
        public string? TipoMagnitud { get; set; }

        public ConversionDTO()
        {
        }

        public ConversionDTO(double? valorOrigen, double? valorResultado, string? unidadOrigen, string? unidadDestino, string? tipoMagnitud)
        {
            ValorOrigen = valorOrigen;
            ValorResultado = valorResultado;
            UnidadOrigen = unidadOrigen;
            UnidadDestino = unidadDestino;
            TipoMagnitud = tipoMagnitud;
        }
    }
}
