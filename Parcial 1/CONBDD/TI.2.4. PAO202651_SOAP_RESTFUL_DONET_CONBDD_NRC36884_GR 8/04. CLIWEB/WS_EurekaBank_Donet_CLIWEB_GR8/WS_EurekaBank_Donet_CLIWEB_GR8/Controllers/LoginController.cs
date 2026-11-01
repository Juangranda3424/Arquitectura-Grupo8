using Microsoft.AspNetCore.Mvc;
using WS_EurekaBank_Donet_CLIWEB_GR8.Models;
using WS_EurekaBank_Donet_CLIWEB_GR8.Services;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    public class LoginController : ControladorSesion
    {
        private readonly RestClienteServicio _rest;

        public LoginController(RestClienteServicio rest)
        {
            _rest = rest;
        }

        [HttpGet]
        public IActionResult Index()
        {
            // Si ya está autenticado, ir directo al menú
            if (Autenticado) return RedirectToAction("Index", "Home");

            return View(new LoginViewModel { Error = TempData["Error"] as string });
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(LoginViewModel modelo)
        {
            if (!ModelState.IsValid)
            {
                return View(modelo);
            }

            var usuario = modelo.Usuario.Trim();
            try
            {
                var respuesta = await _rest.LoginAsync(usuario, modelo.Contrasena.Trim());
                if (!respuesta.Success)
                {
                    modelo.Error = respuesta.Message ?? "Credenciales incorrectas";
                    modelo.Contrasena = "";
                    return View(modelo);
                }

                HttpContext.Session.SetString(ClaveUsuario, usuario);
                return RedirectToAction("Index", "Home");
            }
            catch (RestClienteException ex)
            {
                modelo.Error = ex.Message;
                modelo.Contrasena = "";
                return View(modelo);
            }
        }

        [HttpPost, ValidateAntiForgeryToken]
        public IActionResult Logout()
        {
            HttpContext.Session.Clear();
            return RedirectToAction(nameof(Index));
        }
    }
}
