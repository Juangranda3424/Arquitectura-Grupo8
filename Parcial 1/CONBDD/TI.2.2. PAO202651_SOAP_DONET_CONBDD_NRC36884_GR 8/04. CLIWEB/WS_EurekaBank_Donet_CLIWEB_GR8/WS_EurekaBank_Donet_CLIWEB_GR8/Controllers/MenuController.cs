using Microsoft.AspNetCore.Mvc;
using WS_EurekaBank_Donet_CLIWEB_GR8.Models;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    public class MenuController : ControladorConSesion
    {
        [HttpGet]
        public IActionResult Index()
        {
            if (ExigirSesion() is { } redireccion) return redireccion;

            return View(new MenuViewModel { Usuario = UsuarioActual });
        }
    }
}
