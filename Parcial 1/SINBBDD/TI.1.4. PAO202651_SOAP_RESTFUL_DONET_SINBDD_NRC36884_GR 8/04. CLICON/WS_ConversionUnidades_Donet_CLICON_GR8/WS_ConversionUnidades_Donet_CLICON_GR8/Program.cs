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

            ServicioRestOpciones opciones;
            try
            {
                opciones = ServicioRestOpciones.Cargar(Path.Combine(AppContext.BaseDirectory, "appsettings.json"));
            }
            catch (Exception ex)
            {
                Entrada.Error("No se pudo leer appsettings.json: " + ex.Message);
                return;
            }

            // Tiempo máximo de espera: si el servicio no responde, la consola no queda colgada
            using var http = new HttpClient { Timeout = TimeSpan.FromSeconds(opciones.TiempoEsperaSegundos) };
            var rest = new RestClienteServicio(http, opciones);

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
                        token = await rest.LoginAsync(usuario, contrasena);
                        Entrada.Exito($"Sesión iniciada como {usuario}.");
                    }
                    catch (RestClienteException ex)
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
                    var sesionVigente = await ConvertirAsync(rest, token);
                    if (!sesionVigente) token = null;
                }
                else if (opcion == 1)
                {
                    await CerrarSesionAsync(rest, token);
                    token = null;
                    Entrada.Exito("Sesión cerrada.");
                }
                else // 0 = Cancelar: sale del programa
                {
                    await CerrarSesionAsync(rest, token);
                    break;
                }
            }

            Console.WriteLine("Hasta luego.");
        }

        /// <summary>Flujo de una conversión. Devuelve false si el servicio indicó que la sesión ya no es válida.</summary>
        private static async Task<bool> ConvertirAsync(RestClienteServicio rest, string token)
        {
            try
            {
                // Las unidades se piden al servicio (GET /catalogo) en cada conversión
                var catalogo = await rest.ObtenerCatalogoAsync(token);

                var magnitudes = catalogo.Keys.ToList();
                var iMagnitud = Entrada.Opcion("Magnitud", magnitudes.Select(UnidadesCatalogo.EtiquetaMagnitud).ToList());
                if (iMagnitud < 0) return true;
                var magnitud = magnitudes[iMagnitud];

                var unidades = catalogo[magnitud];
                var etiquetas = unidades.Select(u => u.Etiqueta).ToList();

                var iOrigen = Entrada.Opcion("Unidad de origen", etiquetas);
                if (iOrigen < 0) return true;

                var iDestino = Entrada.Opcion("Unidad de destino", etiquetas);
                if (iDestino < 0) return true;

                var valor = Entrada.Numero("Valor a convertir: ");
                if (valor == null) return true;

                var origen = unidades[iOrigen].Valor;
                var destino = unidades[iDestino].Valor;

                var resultado = await rest.ConvertirAsync(token, magnitud, origen, destino, valor.Value);
                Entrada.Exito($"{Entrada.Formato(valor.Value)} {origen.ToLowerInvariant()} = " +
                              $"{Entrada.Formato(resultado)} {destino.ToLowerInvariant()}");
            }
            catch (RestClienteException ex) when (ex.AccesoDenegado)
            {
                Entrada.Error("Tu sesión expiró. Inicia sesión nuevamente.");
                return false;
            }
            catch (RestClienteException ex)
            {
                Entrada.Error(ex.Message);
            }

            return true;
        }

        private static async Task CerrarSesionAsync(RestClienteServicio rest, string token)
        {
            try
            {
                await rest.LogoutAsync(token);
            }
            catch (RestClienteException)
            {
                // Si el servicio no responde, igual se cierra la sesión local
            }
        }
    }
}
