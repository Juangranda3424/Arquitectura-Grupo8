using System.ComponentModel.DataAnnotations;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Models
{
    /// <summary>
    /// Formulario de registro de movimientos: depósito (003), retiro (004)
    /// y transferencia (008, que exige cuenta destino).
    /// </summary>
    public class MovimientoViewModel
    {
        [Required(ErrorMessage = "Ingresa la cuenta.")]
        [Display(Name = "Cuenta")]
        public string Cuenta { get; set; } = "";

        [Required(ErrorMessage = "Ingresa un importe.")]
        [Range(0.01, double.MaxValue, ErrorMessage = "El importe debe ser mayor que cero.")]
        [Display(Name = "Importe")]
        public decimal? Importe { get; set; }

        [Display(Name = "Tipo de movimiento")]
        public string TipoMovimiento { get; set; } = TiposMovimiento.Deposito;

        [Display(Name = "Cuenta destino")]
        public string? CuentaDestino { get; set; }

        // Datos de salida (no vienen del formulario)
        public string? Mensaje { get; set; }
        public bool Exito { get; set; }
        public string? Usuario { get; set; }
    }
}
