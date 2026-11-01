using System.ComponentModel.DataAnnotations;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Models
{
    /// <summary>Formulario y resultado de una operación de saldo (depósito o retiro).</summary>
    public class OperacionViewModel
    {
        [Required(ErrorMessage = "Ingresa el número de cuenta.")]
        public string Cuenta { get; set; } = "";

        [Required(ErrorMessage = "Ingresa el monto.")]
        [Range(0.01, double.MaxValue, ErrorMessage = "El monto debe ser un número positivo válido")]
        public double? Monto { get; set; }

        // Datos de salida (no vienen del formulario)
        public string? Error { get; set; }
        public string? Mensaje { get; set; }
        public double? Saldo { get; set; }
    }
}
