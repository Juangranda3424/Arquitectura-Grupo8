using System.Text.Json;

namespace WS_ConversionUnidades_Donet_CLIESC_GR8.Services
{
    public class ServicioRestOpciones
    {
        // URL base de la API REST (sin la barra final): los recursos cuelgan de aquí (/auth/login, /catalogo, ...)
        public string UrlBase { get; set; } = "http://localhost:5080/api";

        // Tiempo máximo de espera por petición, en segundos
        public int TiempoEsperaSegundos { get; set; } = 15;

        /// <summary>
        /// Lee la sección "ServicioRest" de appsettings.json (junto al .exe).
        /// Si el archivo no existe, usa los valores por defecto.
        /// </summary>
        public static ServicioRestOpciones Cargar(string rutaArchivo)
        {
            if (!File.Exists(rutaArchivo)) return new ServicioRestOpciones();

            using var doc = JsonDocument.Parse(File.ReadAllText(rutaArchivo));
            if (!doc.RootElement.TryGetProperty("ServicioRest", out var seccion)) return new ServicioRestOpciones();

            var opciones = new ServicioRestOpciones();
            if (seccion.TryGetProperty("UrlBase", out var u) && u.GetString() is { Length: > 0 } url) opciones.UrlBase = url;
            if (seccion.TryGetProperty("TiempoEsperaSegundos", out var t) && t.TryGetInt32(out var segundos) && segundos > 0)
            {
                opciones.TiempoEsperaSegundos = segundos;
            }
            return opciones;
        }
    }
}
