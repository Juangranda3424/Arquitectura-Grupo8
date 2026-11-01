using Microsoft.AspNetCore.Mvc;
using WS_EurekaBank_Donet_CLIWEB_GR8.Models;
using WS_EurekaBank_Donet_CLIWEB_GR8.Services;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    public class CuentaController : ControladorSesion
    {
        private readonly RestClienteServicio _rest;

        public CuentaController(RestClienteServicio rest)
        {
            _rest = rest;
        }

        [HttpGet]
        public IActionResult Index()
        {
            if (!Autenticado) return RedirectToAction("Index", "Login");

            return View(new ConsultaCuentaViewModel());
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(ConsultaCuentaViewModel modelo)
        {
            if (!Autenticado) return RedirectToAction("Index", "Login");
            if (!ModelState.IsValid) return View(modelo);

            var cuenta = modelo.Cuenta.Trim();
            try
            {
                var datos = await _rest.ObtenerCuentaAsync(cuenta);
                if (datos == null)
                {
                    modelo.Error = "No se encontraron datos para la cuenta especificada";
                }
                else
                {
                    modelo.Datos = datos;
                }
            }
            catch (RestClienteException ex)
            {
                modelo.Error = ex.Message;
            }

            return View(modelo);
        }
    }
}
