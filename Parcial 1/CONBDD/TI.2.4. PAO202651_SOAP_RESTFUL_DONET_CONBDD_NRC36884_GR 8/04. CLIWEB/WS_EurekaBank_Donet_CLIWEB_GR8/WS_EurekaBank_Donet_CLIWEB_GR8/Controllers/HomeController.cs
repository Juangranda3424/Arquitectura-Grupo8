using Microsoft.AspNetCore.Mvc;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    public class HomeController : ControladorSesion
    {
        [HttpGet]
        public IActionResult Index()
        {
            if (!Autenticado) return RedirectToAction("Index", "Login");

            return View();
        }
    }
}
