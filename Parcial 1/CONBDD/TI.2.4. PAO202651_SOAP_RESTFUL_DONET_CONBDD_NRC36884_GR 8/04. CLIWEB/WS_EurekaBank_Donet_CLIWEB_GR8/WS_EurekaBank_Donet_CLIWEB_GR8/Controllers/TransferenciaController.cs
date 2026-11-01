using Microsoft.AspNetCore.Mvc;
using WS_EurekaBank_Donet_CLIWEB_GR8.Models;
using WS_EurekaBank_Donet_CLIWEB_GR8.Services;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    public class TransferenciaController : ControladorSesion
    {
        private readonly RestClienteServicio _rest;

        public TransferenciaController(RestClienteServicio rest)
        {
            _rest = rest;
        }

        [HttpGet]
        public IActionResult Index()
        {
            if (!Autenticado) return RedirectToAction("Index", "Login");

            return View(new TransferenciaViewModel());
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(TransferenciaViewModel modelo)
        {
            if (!Autenticado) return RedirectToAction("Index", "Login");
            if (!ModelState.IsValid) return View(modelo);

            var origen = modelo.CuentaOrigen.Trim();
            var destino = modelo.CuentaDestino.Trim();

            if (origen == destino)
            {
                modelo.Error = "La cuenta origen y destino no pueden ser la misma";
                return View(modelo);
            }

            try
            {
                var respuesta = await _rest.TransferirAsync(origen, modelo.Monto!.Value, destino);
                if (!respuesta.Success)
                {
                    modelo.Error = respuesta.Mensaje
                        ?? "Error al realizar la transferencia. Verifique los datos de las cuentas y el saldo disponible en la cuenta origen";
                    return View(modelo);
                }

                // Operación registrada: se limpia el formulario y se muestra el saldo resultante
                ModelState.Clear();
                modelo.CuentaOrigen = "";
                modelo.CuentaDestino = "";
                modelo.Monto = null;
                modelo.Mensaje = respuesta.Mensaje ?? "Transferencia realizada con éxito";
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
