using Microsoft.AspNetCore.Mvc;
using WS_EurekaBank_Donet_CLIWEB_GR8.Models;
using WS_EurekaBank_Donet_CLIWEB_GR8.Services;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    public class MovimientoController : ControladorSesion
    {
        // Códigos de tipo de movimiento que restan saldo (004 = retiro)
        private static readonly HashSet<string> CodigosEgreso = new() { "004" };

        // Códigos que suman saldo (001 apertura, 003 depósito, 009 transferencia)
        private static readonly HashSet<string> CodigosIngreso = new() { "001", "003", "009" };

        private readonly RestClienteServicio _rest;

        public MovimientoController(RestClienteServicio rest)
        {
            _rest = rest;
        }

        [HttpGet]
        public IActionResult Index()
        {
            if (!Autenticado) return RedirectToAction("Index", "Login");

            return View(new MovimientoViewModel());
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(MovimientoViewModel modelo)
        {
            if (!Autenticado) return RedirectToAction("Index", "Login");
            if (!ModelState.IsValid) return View(modelo);

            var cuenta = modelo.Cuenta.Trim();
            try
            {
                var movimientos = await _rest.ObtenerMovimientosAsync(cuenta);
                var datos = await _rest.ObtenerCuentaAsync(cuenta);

                modelo.Movimientos = movimientos;
                modelo.SaldoActual = datos?.DecCuenSaldo ?? 0;

                foreach (var movimiento in movimientos)
                {
                    var importe = movimiento.ImporteMovimiento;
                    var codigo = movimiento.CodigoTipoMovimiento ?? "";
                    var descripcion = (movimiento.TipoDescripcion ?? "").ToLowerInvariant().Trim();

                    if (CodigosEgreso.Contains(codigo) || descripcion == "retiro")
                    {
                        modelo.TotalEgresos += importe;
                    }
                    else if (CodigosIngreso.Contains(codigo)
                             || descripcion is "deposito" or "depósito" or "transferencia" or "apertura de cuenta")
                    {
                        modelo.TotalIngresos += importe;
                    }
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
