using Microsoft.AspNetCore.Mvc;
using WS_ConversionUnidades_Donet_CLIWEB_GR8.Models;
using WS_ConversionUnidades_Donet_CLIWEB_GR8.Services;

namespace WS_ConversionUnidades_Donet_CLIWEB_GR8.Controllers
{
    public class ConversorController : Controller
    {
        private const string MensajeSesionExpirada = "Tu sesión expiró. Inicia sesión nuevamente.";

        private readonly RestClienteServicio _rest;

        public ConversorController(RestClienteServicio rest)
        {
            _rest = rest;
        }

        [HttpGet]
        public async Task<IActionResult> Index()
        {
            if (!HaySesion(out var token)) return RedirectToAction("Login", "Autenticacion");

            var modelo = new ConversionViewModel { Usuario = Usuario() };
            try
            {
                modelo.Catalogo = await _rest.ObtenerCatalogoAsync(token!);
            }
            catch (RestClienteException ex) when (ex.AccesoDenegado)
            {
                return SesionExpirada();
            }
            catch (RestClienteException ex)
            {
                modelo.Error = ex.Message;
            }

            return View(modelo);
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(ConversionViewModel modelo)
        {
            if (!HaySesion(out var token)) return RedirectToAction("Login", "Autenticacion");

            modelo.Usuario = Usuario();
            modelo.Resultado = null;

            try
            {
                // El catálogo se vuelve a pedir para pintar los combos con lo que publica el servicio
                modelo.Catalogo = await _rest.ObtenerCatalogoAsync(token!);

                if (!ModelState.IsValid || modelo.ValorOrigen == null)
                {
                    return View(modelo);
                }

                modelo.Resultado = await _rest.ConvertirAsync(token!, modelo.TipoMagnitud,
                    modelo.UnidadOrigen, modelo.UnidadDestino, modelo.ValorOrigen.Value);
            }
            catch (RestClienteException ex) when (ex.AccesoDenegado)
            {
                // El token ya no es válido (por ejemplo, el servidor se reinició): hay que iniciar sesión otra vez
                return SesionExpirada();
            }
            catch (RestClienteException ex)
            {
                modelo.Error = ex.Message;
            }

            return View(modelo);
        }

        private IActionResult SesionExpirada()
        {
            HttpContext.Session.Clear();
            TempData["Error"] = MensajeSesionExpirada;
            return RedirectToAction("Login", "Autenticacion");
        }

        private bool HaySesion(out string? token)
        {
            token = HttpContext.Session.GetString(AutenticacionController.ClaveToken);
            return !string.IsNullOrEmpty(token);
        }

        private string? Usuario() => HttpContext.Session.GetString(AutenticacionController.ClaveUsuario);
    }
}
