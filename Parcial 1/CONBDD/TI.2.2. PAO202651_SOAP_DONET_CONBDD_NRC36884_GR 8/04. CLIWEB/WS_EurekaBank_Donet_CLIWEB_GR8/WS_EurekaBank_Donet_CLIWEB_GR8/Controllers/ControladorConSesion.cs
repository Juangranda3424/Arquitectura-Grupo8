using Microsoft.AspNetCore.Mvc;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    /// <summary>
    /// Base de los controladores que exigen una sesión iniciada.
    /// El servicio SOAP del banco no entrega token: la sesión guarda el usuario.
    /// </summary>
    public abstract class ControladorConSesion : Controller
    {
        public const string ClaveUsuario = "usuario";

        protected string? UsuarioActual => HttpContext.Session.GetString(ClaveUsuario);

        protected bool HaySesion => !string.IsNullOrEmpty(UsuarioActual);

        /// <summary>
        /// Devuelve la redirección al login cuando no hay sesión y null cuando la hay,
        /// para usarla como <c>if (ExigirSesion() is { } redireccion) return redireccion;</c>
        /// </summary>
        protected IActionResult? ExigirSesion() =>
            HaySesion ? null : RedirectToAction("Login", "Autenticacion");
    }
}
