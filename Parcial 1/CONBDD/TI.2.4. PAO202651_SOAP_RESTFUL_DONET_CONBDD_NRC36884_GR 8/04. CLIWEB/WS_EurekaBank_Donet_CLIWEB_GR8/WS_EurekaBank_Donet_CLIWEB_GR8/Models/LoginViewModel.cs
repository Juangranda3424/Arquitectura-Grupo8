using System.ComponentModel.DataAnnotations;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Models
{
    public class LoginViewModel
    {
        [Required(ErrorMessage = "Ingresa tu usuario.")]
        public string Usuario { get; set; } = "";

        [Required(ErrorMessage = "Ingresa tu contraseña.")]
        [DataType(DataType.Password)]
        public string Contrasena { get; set; } = "";

        // Mensaje de error devuelto por el servicio o por la sesión anterior
        public string? Error { get; set; }
    }
}
