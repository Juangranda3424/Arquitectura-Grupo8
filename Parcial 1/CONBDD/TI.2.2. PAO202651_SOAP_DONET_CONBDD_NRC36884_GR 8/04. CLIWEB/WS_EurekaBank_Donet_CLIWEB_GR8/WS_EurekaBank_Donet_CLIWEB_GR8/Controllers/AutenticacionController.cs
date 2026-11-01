using Microsoft.AspNetCore.Mvc;
using WS_EurekaBank_Donet_CLIWEB_GR8.Models;
using WS_EurekaBank_Donet_CLIWEB_GR8.Services;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    public class AutenticacionController : Controller
    {
        private readonly SoapClienteServicio _soap;

        public AutenticacionController(SoapClienteServicio soap)
        {
            _soap = soap;
        }

        [HttpGet]
        public IActionResult Login()
        {
            if (!string.IsNullOrEmpty(HttpContext.Session.GetString(ControladorConSesion.ClaveUsuario)))
            {
                return RedirectToAction("Index", "Menu");
            }

            return View(new LoginViewModel());
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
                // El servicio responde 1 cuando las credenciales son correctas y -1 cuando no
                var resultado = await _soap.LoginAsync(modelo.Usuario.Trim(), modelo.Contrasena);
                if (resultado != 1)
                {
                    modelo.Error = "Usuario o contraseña incorrectos.";
                    modelo.Contrasena = "";
                    return View(modelo);
                }

                HttpContext.Session.SetString(ControladorConSesion.ClaveUsuario, modelo.Usuario.Trim());
                return RedirectToAction("Index", "Menu");
            }
            catch (SoapClienteException ex)
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
            return RedirectToAction(nameof(Login));
        }
    }
}
