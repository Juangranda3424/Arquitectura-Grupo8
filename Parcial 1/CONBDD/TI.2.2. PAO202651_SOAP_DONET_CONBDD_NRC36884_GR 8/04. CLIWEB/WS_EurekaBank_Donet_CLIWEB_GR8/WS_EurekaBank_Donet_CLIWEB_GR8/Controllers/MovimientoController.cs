using Microsoft.AspNetCore.Mvc;
using WS_EurekaBank_Donet_CLIWEB_GR8.Models;
using WS_EurekaBank_Donet_CLIWEB_GR8.Services;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    /// <summary>
    /// Registro de movimientos: depósito (003), retiro (004) y transferencia (008).
    /// </summary>
    public class MovimientoController : ControladorConSesion
    {
        private readonly SoapClienteServicio _soap;

        public MovimientoController(SoapClienteServicio soap)
        {
            _soap = soap;
        }

        [HttpGet]
        public IActionResult Index()
        {
            if (ExigirSesion() is { } redireccion) return redireccion;

            return View(new MovimientoViewModel { Usuario = UsuarioActual });
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(MovimientoViewModel modelo)
        {
            if (ExigirSesion() is { } redireccion) return redireccion;

            modelo.Usuario = UsuarioActual;

            if (!TiposMovimiento.EsValido(modelo.TipoMovimiento))
            {
                ModelState.AddModelError(nameof(modelo.TipoMovimiento), "Selecciona un tipo de movimiento válido.");
            }
            else if (modelo.TipoMovimiento == TiposMovimiento.Transferencia
                && string.IsNullOrWhiteSpace(modelo.CuentaDestino))
            {
                // Igual que el cliente anterior: la cuenta destino solo se exige en transferencias
                ModelState.AddModelError(nameof(modelo.CuentaDestino), "La transferencia requiere una cuenta destino.");
            }

            if (!ModelState.IsValid || modelo.Importe == null)
            {
                return View(modelo);
            }

            try
            {
                var resultado = await _soap.RegistrarMovimientoAsync(
                    modelo.Cuenta.Trim(), modelo.Importe.Value, modelo.TipoMovimiento, modelo.CuentaDestino?.Trim());

                if (resultado == 1)
                {
                    // Se limpia el formulario y se muestra la confirmación
                    ModelState.Clear();
                    return View(new MovimientoViewModel
                    {
                        Usuario = UsuarioActual,
                        Exito = true,
                        Mensaje = "Movimiento registrado con éxito."
                    });
                }

                modelo.Mensaje = "El servidor rechazó el movimiento (cuenta, saldo o datos inválidos).";
            }
            catch (SoapClienteException ex)
            {
                modelo.Mensaje = ex.Message;
            }

            return View(modelo);
        }
    }
}
