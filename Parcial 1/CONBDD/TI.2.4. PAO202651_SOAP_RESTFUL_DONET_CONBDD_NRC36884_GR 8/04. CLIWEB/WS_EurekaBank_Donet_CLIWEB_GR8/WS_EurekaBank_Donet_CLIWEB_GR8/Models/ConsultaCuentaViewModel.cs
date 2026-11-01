using System.ComponentModel.DataAnnotations;
using Microsoft.AspNetCore.Mvc.ModelBinding;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Models
{
    /// <summary>Consulta de los datos registrados de una cuenta.</summary>
    public class ConsultaCuentaViewModel
    {
        [Required(ErrorMessage = "Ingresa el número de cuenta.")]
        public string Cuenta { get; set; } = "";

        // Datos de salida (no vienen del formulario)
        public string? Error { get; set; }

        [BindNever]
        public CuentaModel? Datos { get; set; }
    }
}
