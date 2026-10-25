using WS_ConversionUnidades_Donet_CLIESC_GR8.Forms;
using WS_ConversionUnidades_Donet_CLIESC_GR8.Services;

namespace WS_ConversionUnidades_Donet_CLIESC_GR8
{
    internal static class Program
    {
        [STAThread]
        private static void Main()
        {
            ApplicationConfiguration.Initialize();

            ServicioSoapOpciones opciones;
            try
            {
                opciones = ServicioSoapOpciones.Cargar(Path.Combine(AppContext.BaseDirectory, "appsettings.json"));
            }
            catch (Exception ex)
            {
                MessageBox.Show("No se pudo leer appsettings.json: " + ex.Message, "Error de configuración",
                    MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            // Tiempo máximo de espera: si el servicio no responde, la ventana no queda colgada
            using var http = new HttpClient { Timeout = TimeSpan.FromSeconds(15) };
            var soap = new SoapClienteServicio(http, opciones);

            // Login -> Conversor; al cerrar sesión se vuelve al login
            while (true)
            {
                using var login = new FrmLogin(soap);
                if (login.ShowDialog() != DialogResult.OK) break;

                using var conversor = new FrmConversor(soap, login.Token!, login.Usuario!);
                conversor.ShowDialog();
                if (!conversor.CerrarSesionSolicitada) break;
            }
        }
    }
}
