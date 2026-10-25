using System.Globalization;
using System.Text;
using System.Xml;
using System.Xml.Linq;

namespace WS_ConversionUnidades_Donet_CLICON_GR8.Services
{
    /// <summary>
    /// Cliente SOAP 1.1 (basicHttpBinding) para los servicios WCF:
    ///   - WSAutenticador.svc      -> login / logout
    ///   - WSConversorUnidades.svc -> convertir-unidades
    /// El token viaja en el encabezado SOAP Token (namespace http://localhost:8080/).
    /// </summary>
    public class SoapClienteServicio
    {
        public const string RespuestaLoginInvalido = "Usuario invalido";

        private static readonly XNamespace Soap = "http://schemas.xmlsoap.org/soap/envelope/";
        private static readonly XNamespace Tempuri = "http://tempuri.org/";
        private static readonly XNamespace DcUsuario = "http://schemas.datacontract.org/2004/07/WS_ConversionUnidades_Donet_GR8.modelo";
        private static readonly XNamespace DcConversion = "http://schemas.datacontract.org/2004/07/WS_ConversionUnidades_Donet_GR8.dto";

        private readonly HttpClient _http;
        private readonly ServicioSoapOpciones _opciones;

        public SoapClienteServicio(HttpClient http, ServicioSoapOpciones opciones)
        {
            _http = http;
            _opciones = opciones;
        }

        // ---------- Operaciones ----------

        public async Task<string> LoginAsync(string usuario, string contrasena)
        {
            // Usuario no tiene [DataContract]: sus miembros van en orden alfabético (contrasena, usuario)
            var cuerpo = new XElement(Tempuri + "login",
                new XElement(Tempuri + "usuario",
                    new XAttribute(XNamespace.Xmlns + "d", DcUsuario),
                    new XElement(DcUsuario + "contrasena", contrasena),
                    new XElement(DcUsuario + "usuario", usuario)));

            var respuesta = await EnviarAsync(_opciones.UrlAutenticador,
                "http://tempuri.org/IWSAutenticador/login", null, cuerpo);

            var token = respuesta.Descendants(Tempuri + "loginResult").FirstOrDefault()?.Value;
            if (string.IsNullOrWhiteSpace(token) || token == RespuestaLoginInvalido)
            {
                throw new SoapClienteException("Usuario o contraseña incorrectos.");
            }
            return token;
        }

        public async Task<bool> LogoutAsync(string token)
        {
            var cuerpo = new XElement(Tempuri + "logout",
                new XElement(Tempuri + "token", token));

            var respuesta = await EnviarAsync(_opciones.UrlAutenticador,
                "http://tempuri.org/IWSAutenticador/logout", token, cuerpo);

            var resultado = respuesta.Descendants(Tempuri + "logoutResult").FirstOrDefault()?.Value;
            return string.Equals(resultado, "true", StringComparison.OrdinalIgnoreCase);
        }

        public async Task<double> ConvertirAsync(string token, string tipoMagnitud,
            string unidadOrigen, string unidadDestino, double valorOrigen)
        {
            // ConversionDTO tampoco tiene [DataContract]: orden alfabético de sus miembros.
            // ValorResultado es nullable y el servidor lo calcula, por eso se omite.
            var cuerpo = new XElement(Tempuri + "convertir-unidades",
                new XElement(Tempuri + "peticionConversion",
                    new XAttribute(XNamespace.Xmlns + "d", DcConversion),
                    new XElement(DcConversion + "TipoMagnitud", tipoMagnitud),
                    new XElement(DcConversion + "UnidadDestino", unidadDestino),
                    new XElement(DcConversion + "UnidadOrigen", unidadOrigen),
                    new XElement(DcConversion + "ValorOrigen", valorOrigen.ToString("R", CultureInfo.InvariantCulture))));

            var respuesta = await EnviarAsync(_opciones.UrlConversor,
                "http://tempuri.org/IWSConversorUnidades/convertir-unidades", token, cuerpo);

            var texto = respuesta.Descendants(Tempuri + "convertir-unidadesResult").FirstOrDefault()?.Value;
            if (!double.TryParse(texto, NumberStyles.Float, CultureInfo.InvariantCulture, out var resultado))
            {
                throw new SoapClienteException("El servicio devolvió una respuesta que no se pudo interpretar.");
            }
            return resultado;
        }

        // ---------- Infraestructura SOAP ----------

        private async Task<XDocument> EnviarAsync(string url, string accion, string? token, XElement cuerpo)
        {
            var sobre = new XElement(Soap + "Envelope",
                new XAttribute(XNamespace.Xmlns + "s", Soap),
                new XElement(Soap + "Header",
                    token == null ? null : new XElement((XNamespace)_opciones.NamespaceToken + "Token", token)),
                new XElement(Soap + "Body", cuerpo));

            using var peticion = new HttpRequestMessage(HttpMethod.Post, url)
            {
                Content = new StringContent(sobre.ToString(SaveOptions.DisableFormatting), Encoding.UTF8, "text/xml")
            };
            peticion.Headers.Add("SOAPAction", $"\"{accion}\"");

            HttpResponseMessage respuestaHttp;
            try
            {
                respuestaHttp = await _http.SendAsync(peticion);
            }
            catch (Exception ex) when (ex is HttpRequestException || ex is TaskCanceledException)
            {
                throw new SoapClienteException(
                    $"No se pudo conectar con el servicio web ({url}). Verifica que el servidor esté en ejecución.");
            }

            var texto = await respuestaHttp.Content.ReadAsStringAsync();

            XDocument doc;
            try
            {
                doc = XDocument.Parse(texto);
            }
            catch (XmlException)
            {
                throw new SoapClienteException(
                    $"El servicio respondió algo inesperado (HTTP {(int)respuestaHttp.StatusCode}).");
            }

            var falla = doc.Descendants(Soap + "Fault").FirstOrDefault();
            if (falla != null)
            {
                var mensaje = ExtraerMensajeFalla(falla);
                throw new SoapClienteException(mensaje, mensaje.StartsWith("Acceso denegado", StringComparison.OrdinalIgnoreCase));
            }

            return doc;
        }

        private static string ExtraerMensajeFalla(XElement falla)
        {
            // Con includeExceptionDetailInFaults=true el mensaje real (por ejemplo, el de ArgumentException)
            // viene en <detail><ExceptionDetail><Message>; si no, se usa <faultstring>.
            var detalle = falla.Descendants().FirstOrDefault(e => e.Name.LocalName == "detail")
                ?.Descendants().FirstOrDefault(e => e.Name.LocalName == "Message")?.Value;
            if (!string.IsNullOrWhiteSpace(detalle)) return detalle;

            var faultString = falla.Descendants().FirstOrDefault(e => e.Name.LocalName == "faultstring")?.Value;
            return string.IsNullOrWhiteSpace(faultString) ? "El servicio devolvió un error." : faultString;
        }
    }
}
