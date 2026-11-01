using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    /// <summary>
    /// Base de los controladores del cliente. Centraliza la sesión del usuario
    /// (antes Session["Username"] del cliente ASP.NET MVC 5) y publica el usuario en el layout.
    /// </summary>
    public abstract class ControladorSesion : Controller
    {
        public const string ClaveUsuario = "usuario";

        protected string? Usuario => HttpContext.Session.GetString(ClaveUsuario);

        protected bool Autenticado => !string.IsNullOrEmpty(Usuario);

        public override void OnActionExecuting(ActionExecutingContext contexto)
        {
            ViewData["Usuario"] = Usuario;
            base.OnActionExecuting(contexto);
        }
    }
}
