using System.ComponentModel.DataAnnotations;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Models
{
    /// <summary>Formulario y resultado de una transferencia entre cuentas.</summary>
    public class TransferenciaViewModel
    {
        [Required(ErrorMessage = "Ingresa la cuenta origen.")]
        public string CuentaOrigen { get; set; } = "";

        [Required(ErrorMessage = "Ingresa la cuenta destino.")]
        public string CuentaDestino { get; set; } = "";

        [Required(ErrorMessage = "Ingresa el monto.")]
        [Range(0.01, double.MaxValue, ErrorMessage = "El monto debe ser un número positivo válido")]
        public double? Monto { get; set; }

        // Datos de salida (no vienen del formulario)
        public string? Error { get; set; }
        public string? Mensaje { get; set; }
        public double? Saldo { get; set; }
    }
}
