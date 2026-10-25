using Microsoft.AspNetCore.Mvc;
using WS_ConversionUnidades_Donet_CLIWEB_GR8.Models;
using WS_ConversionUnidades_Donet_CLIWEB_GR8.Services;

namespace WS_ConversionUnidades_Donet_CLIWEB_GR8.Controllers
{
    public class AutenticacionController : Controller
    {
        public const string ClaveToken = "token";
        public const string ClaveUsuario = "usuario";

        private readonly SoapClienteServicio _soap;

        public AutenticacionController(SoapClienteServicio soap)
        {
            _soap = soap;
        }

        [HttpGet]
        public IActionResult Login()
        {
            if (!string.IsNullOrEmpty(HttpContext.Session.GetString(ClaveToken)))
            {
                return RedirectToAction("Index", "Conversor");
            }

            return View(new LoginViewModel { Error = TempData["Error"] as string });
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Login(LoginViewModel modelo)
        {
            if (!ModelState.IsValid)
            {
                return View(modelo);
            }

            try
            {
                var token = await _soap.LoginAsync(modelo.Usuario.Trim(), modelo.Contrasena);
                HttpContext.Session.SetString(ClaveToken, token);
                HttpContext.Session.SetString(ClaveUsuario, modelo.Usuario.Trim());
                return RedirectToAction("Index", "Conversor");
            }
            catch (SoapClienteException ex)
            {
                modelo.Error = ex.Message;
                modelo.Contrasena = "";
                return View(modelo);
            }
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Logout()
        {
            var token = HttpContext.Session.GetString(ClaveToken);
            if (!string.IsNullOrEmpty(token))
            {
                try
                {
                    await _soap.LogoutAsync(token);
                }
                catch (SoapClienteException)
                {
                    // Si el servicio no responde o el token ya no existe, igual se cierra la sesión local
                }
            }

            HttpContext.Session.Clear();
            return RedirectToAction(nameof(Login));
        }
    }
}
