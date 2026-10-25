using System.ComponentModel.DataAnnotations;

namespace WS_ConversionUnidades_Donet_CLIWEB_GR8.Models
{
    public class LoginViewModel
    {
        [Required(ErrorMessage = "Ingresa tu usuario.")]
        public string Usuario { get; set; } = "";

        [Required(ErrorMessage = "Ingresa tu contraseña.")]
        [DataType(DataType.Password)]
        public string Contrasena { get; set; } = "";

        public string? Error { get; set; }
    }
}
