using System.Text;
using WS_ConversionUnidades_Donet_CLICON_GR8.Consola;
using WS_ConversionUnidades_Donet_CLICON_GR8.Models;
using WS_ConversionUnidades_Donet_CLICON_GR8.Services;

namespace WS_ConversionUnidades_Donet_CLICON_GR8
{
    internal static class Program
    {
        private static async Task Main()
        {
            Console.OutputEncoding = Encoding.UTF8; // para que se vean tildes y ñ

            ServicioSoapOpciones opciones;
            try
            {
                opciones = ServicioSoapOpciones.Cargar(Path.Combine(AppContext.BaseDirectory, "appsettings.json"));
            }
            catch (Exception ex)
            {
                Entrada.Error("No se pudo leer appsettings.json: " + ex.Message);
                return;
            }

            // Tiempo máximo de espera: si el servicio no responde, la consola no queda colgada
            using var http = new HttpClient { Timeout = TimeSpan.FromSeconds(15) };
            var soap = new SoapClienteServicio(http, opciones);

            Console.WriteLine("=====================================");
            Console.WriteLine("   Conversión de Unidades - GR8");
            Console.WriteLine("=====================================");

            string? token = null;
            string usuario = "";

            while (true)
            {
                // ---- Sin sesión: pedir credenciales ----
                if (token == null)
                {
                    Console.WriteLine();
                    usuario = Entrada.Texto("Usuario (vacío para salir): ");
                    if (usuario.Length == 0) break;

                    var contrasena = Entrada.Contrasena("Contraseña: ");
                    try
                    {
                        token = await soap.LoginAsync(usuario, contrasena);
                        Entrada.Exito($"Sesión iniciada como {usuario}.");
                    }
                    catch (SoapClienteException ex)
                    {
                        Entrada.Error(ex.Message);
                    }
                    continue;
                }

                // ---- Con sesión: menú principal ----
                var opcion = Entrada.Opcion($"MENÚ PRINCIPAL (sesión: {usuario})",
                    new[] { "Convertir unidades", "Cerrar sesión" });

                if (opcion == 0)
                {
                    var sesionVigente = await ConvertirAsync(soap, token);
                    if (!sesionVigente) token = null;
                }
                else if (opcion == 1)
                {
                    await CerrarSesionAsync(soap, token);
                    token = null;
                    Entrada.Exito("Sesión cerrada.");
                }
                else // 0 = Cancelar: sale del programa
                {
                    await CerrarSesionAsync(soap, token);
                    break;
                }
            }

            Console.WriteLine("Hasta luego.");
        }

        /// <summary>Flujo de una conversión. Devuelve false si el servicio indicó que la sesión ya no es válida.</summary>
        private static async Task<bool> ConvertirAsync(SoapClienteServicio soap, string token)
        {
            var magnitudes = UnidadesCatalogo.Magnitudes.ToList();
            var iMagnitud = Entrada.Opcion("Magnitud", magnitudes.Select(m => m.Value).ToList());
            if (iMagnitud < 0) return true;
            var magnitud = magnitudes[iMagnitud].Key;

            var unidades = UnidadesCatalogo.Todas[magnitud];
            var etiquetas = unidades.Select(u => u.Etiqueta).ToList();

            var iOrigen = Entrada.Opcion("Unidad de origen", etiquetas);
            if (iOrigen < 0) return true;

            var iDestino = Entrada.Opcion("Unidad de destino", etiquetas);
            if (iDestino < 0) return true;

            var valor = Entrada.Numero("Valor a convertir: ");
            if (valor == null) return true;

            var origen = unidades[iOrigen].Valor;
            var destino = unidades[iDestino].Valor;

            try
            {
                var resultado = await soap.ConvertirAsync(token, magnitud, origen, destino, valor.Value);
                Entrada.Exito($"{Entrada.Formato(valor.Value)} {origen.ToLowerInvariant()} = " +
                              $"{Entrada.Formato(resultado)} {destino.ToLowerInvariant()}");
            }
            catch (SoapClienteException ex) when (ex.AccesoDenegado)
            {
                Entrada.Error("Tu sesión expiró. Inicia sesión nuevamente.");
                return false;
            }
            catch (SoapClienteException ex)
            {
                Entrada.Error(ex.Message);
            }

            return true;
        }

        private static async Task CerrarSesionAsync(SoapClienteServicio soap, string token)
        {
            try
            {
                await soap.LogoutAsync(token);
            }
            catch (SoapClienteException)
            {
                // Si el servicio no responde, igual se cierra la sesión local
            }
        }
    }
}
