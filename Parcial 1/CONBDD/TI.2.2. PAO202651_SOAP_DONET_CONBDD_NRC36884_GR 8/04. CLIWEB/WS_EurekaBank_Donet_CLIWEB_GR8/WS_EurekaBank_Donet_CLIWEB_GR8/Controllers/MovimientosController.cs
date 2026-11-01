using Microsoft.AspNetCore.Mvc;
using WS_EurekaBank_Donet_CLIWEB_GR8.Models;
using WS_EurekaBank_Donet_CLIWEB_GR8.Services;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Controllers
{
    /// <summary>
    /// Consulta de los movimientos de una cuenta, con filtro por tipo.
    /// </summary>
    public class MovimientosController : ControladorConSesion
    {
        private readonly SoapClienteServicio _soap;

        public MovimientosController(SoapClienteServicio soap)
        {
            _soap = soap;
        }

        [HttpGet]
        public IActionResult Index()
        {
            if (ExigirSesion() is { } redireccion) return redireccion;

            return View(new MovimientosViewModel { Usuario = UsuarioActual });
        }

        [HttpPost, ValidateAntiForgeryToken]
        public async Task<IActionResult> Index(MovimientosViewModel modelo)
        {
            if (ExigirSesion() is { } redireccion) return redireccion;

            modelo.Usuario = UsuarioActual;
            modelo.Consulto = false;
            modelo.Movimientos = new List<Movimiento>();

            if (!ModelState.IsValid)
            {
                return View(modelo);
            }

            var cuenta = modelo.Cuenta.Trim();

            try
            {
                var movimientos = await _soap.TraerMovimientosAsync(cuenta);

                // El filtro compara con la descripción del tipo que devuelve el servicio
                modelo.Movimientos = string.IsNullOrEmpty(modelo.FiltroTipo)
                    ? movimientos
                    : movimientos
                        .Where(m => m.Tipo.Contains(modelo.FiltroTipo, StringComparison.OrdinalIgnoreCase))
                        .ToList();
                modelo.Consulto = true;

                modelo.Mensaje = modelo.Movimientos.Count == 0
                    ? $"No se encontraron movimientos para la cuenta {cuenta}."
                    : $"Se encontraron {modelo.Movimientos.Count} movimientos.";
            }
            catch (SoapClienteException ex)
            {
                modelo.Error = ex.Message;
            }

            return View(modelo);
        }
    }
}
