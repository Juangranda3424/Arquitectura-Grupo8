using System.ComponentModel.DataAnnotations;
using Microsoft.AspNetCore.Mvc.ModelBinding;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Models
{
    /// <summary>Consulta de movimientos con los totales de la cuenta.</summary>
    public class MovimientoViewModel
    {
        [Required(ErrorMessage = "Ingresa el número de cuenta.")]
        public string Cuenta { get; set; } = "";

        // Datos de salida (no vienen del formulario)
        public string? Error { get; set; }

        [BindNever]
        public IReadOnlyList<MovimientoModel>? Movimientos { get; set; }

        [BindNever]
        public double SaldoActual { get; set; }

        [BindNever]
        public double TotalIngresos { get; set; }

        [BindNever]
        public double TotalEgresos { get; set; }

        public double SaldoNeto => TotalIngresos - TotalEgresos;
    }
}
