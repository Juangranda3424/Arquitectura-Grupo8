using System.Text.Json;

namespace WS_ConversionUnidades_Donet_CLICON_GR8.Services
{
    public class ServicioSoapOpciones
    {
        public string UrlAutenticador { get; set; } = "http://localhost:62895/webservicio/WSAutenticador.svc";
        public string UrlConversor { get; set; } = "http://localhost:62895/webservicio/WSConversorUnidades.svc";
        public string NamespaceToken { get; set; } = "http://localhost:8080/";

        /// <summary>
        /// Lee la sección "ServicioSoap" de appsettings.json (junto al .exe).
        /// Si el archivo no existe, usa los valores por defecto.
        /// </summary>
        public static ServicioSoapOpciones Cargar(string rutaArchivo)
        {
            if (!File.Exists(rutaArchivo)) return new ServicioSoapOpciones();

            using var doc = JsonDocument.Parse(File.ReadAllText(rutaArchivo));
            if (!doc.RootElement.TryGetProperty("ServicioSoap", out var seccion)) return new ServicioSoapOpciones();

            var opciones = new ServicioSoapOpciones();
            if (seccion.TryGetProperty("UrlAutenticador", out var a) && a.GetString() is { Length: > 0 } ua) opciones.UrlAutenticador = ua;
            if (seccion.TryGetProperty("UrlConversor", out var c) && c.GetString() is { Length: > 0 } uc) opciones.UrlConversor = uc;
            if (seccion.TryGetProperty("NamespaceToken", out var n) && n.GetString() is { Length: > 0 } un) opciones.NamespaceToken = un;
            return opciones;
        }
    }
}
