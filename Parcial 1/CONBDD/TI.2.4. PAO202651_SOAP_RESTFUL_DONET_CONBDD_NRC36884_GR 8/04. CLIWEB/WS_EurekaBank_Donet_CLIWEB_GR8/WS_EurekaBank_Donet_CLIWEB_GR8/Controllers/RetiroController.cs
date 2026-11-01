using Microsoft.AspNetCore.Mvc;
using WS_EurekaBank_Donet_CLIWEB_GR8.Models;
using WS_EurekaBank_Donet_CLIWEB_GR8.Services;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    public class RetiroController : ControladorSesion
    {
        private readonly RestClienteServicio _rest;

        public RetiroController(RestClienteServicio rest)
        {
            _rest = rest;
        }

        [HttpGet]
        public IActionResult Index()
        {
            if (!Autenticado) return RedirectToAction("Index", "Login");

            return View(new OperacionViewModel());
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(OperacionViewModel modelo)
        {
            if (!Autenticado) return RedirectToAction("Index", "Login");
            if (!ModelState.IsValid) return View(modelo);

            try
            {
                var respuesta = await _rest.RetirarAsync(modelo.Cuenta.Trim(), modelo.Monto!.Value);
                if (!respuesta.Success)
                {
                    modelo.Error = respuesta.Mensaje
                        ?? "Error al realizar el retiro. Verifique los datos de la cuenta y el saldo disponible";
                    return View(modelo);
                }

                // Operación registrada: se limpia el formulario y se muestra el saldo resultante
                ModelState.Clear();
                modelo.Cuenta = "";
                modelo.Monto = null;
                modelo.Mensaje = respuesta.Mensaje ?? "Retiro realizado con éxito";
                modelo.Saldo = respuesta.Saldo;
            }
            catch (RestClienteException ex)
            {
                modelo.Error = ex.Message;
            }

            return View(modelo);
        }
    }
}
