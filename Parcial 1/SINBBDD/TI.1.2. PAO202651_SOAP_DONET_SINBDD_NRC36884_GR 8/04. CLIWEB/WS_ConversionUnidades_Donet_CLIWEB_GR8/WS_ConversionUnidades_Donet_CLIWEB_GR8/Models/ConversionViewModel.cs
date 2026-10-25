using System.ComponentModel.DataAnnotations;

namespace WS_ConversionUnidades_Donet_CLIWEB_GR8.Models
{
    public class ConversionViewModel
    {
        public string TipoMagnitud { get; set; } = "LONGITUD";

        [Required(ErrorMessage = "Ingresa un valor numérico.")]
        public double? ValorOrigen { get; set; }

        public string UnidadOrigen { get; set; } = "CENTIMETRO";
        public string UnidadDestino { get; set; } = "METRO";

        // Datos de salida (no vienen del formulario)
        public double? Resultado { get; set; }
        public string? Error { get; set; }
        public string? Usuario { get; set; }
    }
}
