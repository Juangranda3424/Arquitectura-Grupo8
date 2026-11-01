using System.Net;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.modelo;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controlador
{
    /// <summary>
    /// Controlador de la aplicacion de escritorio: conecta las vistas con el modelo SOAP.
    /// </summary>
    public class EurekaBankController
    {
        private readonly EurekaBankModel modelo = new EurekaBankModel();

        public bool IniciarSesion(string usuario, string contrasena, out string mensaje)
        {
            try
            {
                int resultado = modelo.Login(usuario, contrasena);
                if (resultado == 1)
                {
                    mensaje = "Bienvenido " + usuario + " a EurekaBank.";
                    return true;
                }

                mensaje = "Usuario o contrasena incorrectos.";
                return false;
            }
            catch (WebException ex)
            {
                mensaje = "No se pudo contactar al servidor SOAP: " + ex.Message;
                return false;
            }
        }

        public bool RegistrarMovimiento(string cuenta, string importe, string tipoMovimiento, string cuentaDestino, out string mensaje)
        {
            double importeNumerico;
            if (!double.TryParse(importe, out importeNumerico) || importeNumerico <= 0)
            {
                mensaje = "El importe debe ser un numero mayor a cero.";
                return false;
            }

            if (tipoMovimiento == EurekaBankModel.Transferencia && string.IsNullOrWhiteSpace(cuentaDestino))
            {
                mensaje = "La transferencia requiere una cuenta destino.";
                return false;
            }

            try
            {
                int resultado = modelo.RegistrarMovimiento(
                    cuenta,
                    importeNumerico.ToString(System.Globalization.CultureInfo.InvariantCulture),
                    tipoMovimiento,
                    cuentaDestino);

                if (resultado == 1)
                {
                    mensaje = "Movimiento registrado exitosamente.";
                    return true;
                }

                mensaje = "El servidor rechazo el movimiento (cuenta, saldo o datos invalidos).";
                return false;
            }
            catch (WebException ex)
            {
                mensaje = "No se pudo contactar al servidor SOAP: " + ex.Message;
                return false;
            }
        }

        public List<Movimiento> ObtenerMovimientos(string cuenta, out string mensaje)
        {
            try
            {
                List<Movimiento> movimientos = modelo.ObtenerMovimientos(cuenta);
                mensaje = movimientos.Count == 0
                    ? "No se encontraron movimientos para la cuenta " + cuenta + "."
                    : string.Empty;
                return movimientos;
            }
            catch (WebException ex)
            {
                mensaje = "No se pudo contactar al servidor SOAP: " + ex.Message;
                return new List<Movimiento>();
            }
        }
    }
}
