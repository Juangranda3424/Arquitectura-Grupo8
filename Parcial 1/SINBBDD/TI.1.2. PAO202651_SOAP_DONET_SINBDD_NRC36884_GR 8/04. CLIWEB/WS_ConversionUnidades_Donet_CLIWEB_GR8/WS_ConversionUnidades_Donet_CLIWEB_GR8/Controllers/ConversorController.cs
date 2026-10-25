using Microsoft.AspNetCore.Mvc;
using WS_ConversionUnidades_Donet_CLIWEB_GR8.Models;
using WS_ConversionUnidades_Donet_CLIWEB_GR8.Services;

namespace WS_ConversionUnidades_Donet_CLIWEB_GR8.Controllers
{
    public class ConversorController : Controller
    {
        private readonly SoapClienteServicio _soap;

        public ConversorController(SoapClienteServicio soap)
        {
            _soap = soap;
        }

        [HttpGet]
        public IActionResult Index()
        {
            if (!HaySesion(out _)) return RedirectToAction("Login", "Autenticacion");

            return View(new ConversionViewModel { Usuario = Usuario() });
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(ConversionViewModel modelo)
        {
            if (!HaySesion(out var token)) return RedirectToAction("Login", "Autenticacion");

            modelo.Usuario = Usuario();
            modelo.Resultado = null;

            if (!ModelState.IsValid || modelo.ValorOrigen == null)
            {
                return View(modelo);
            }

            try
            {
                modelo.Resultado = await _soap.ConvertirAsync(token!, modelo.TipoMagnitud,
                    modelo.UnidadOrigen, modelo.UnidadDestino, modelo.ValorOrigen.Value);
            }
            catch (SoapClienteException ex) when (ex.AccesoDenegado)
            {
                // El token ya no es válido (por ejemplo, el servidor se reinició): hay que iniciar sesión otra vez
                HttpContext.Session.Clear();
                TempData["Error"] = "Tu sesión expiró. Inicia sesión nuevamente.";
                return RedirectToAction("Login", "Autenticacion");
            }
            catch (SoapClienteException ex)
            {
                modelo.Error = ex.Message;
            }

            return View(modelo);
        }

        private bool HaySesion(out string? token)
        {
            token = HttpContext.Session.GetString(AutenticacionController.ClaveToken);
            return !string.IsNullOrEmpty(token);
        }

        private string? Usuario() => HttpContext.Session.GetString(AutenticacionController.ClaveUsuario);
    }
}
