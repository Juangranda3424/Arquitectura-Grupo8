using System.Globalization;
using System.Text;
using System.Xml;
using System.Xml.Linq;
using Microsoft.Extensions.Options;
using WS_EurekaBank_Donet_CLIWEB_GR8.Models;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Services
{
    /// <summary>
    /// Cliente SOAP 1.1 (basicHttpBinding) para el servicio WCF WS_EurekaBank_Donet_GR8:
    ///   - login             -> 1 si las credenciales son correctas, -1 si no
    ///   - regDeposito       -> 1 si el movimiento se registró, -1 si el servidor lo rechazó
    ///   - traerMovimientos  -> lista de movimientos de la cuenta
    /// El contrato usa elementFormDefault="qualified": el envoltorio y sus parámetros
    /// viajan en el namespace http://ws.monster.edu.ec/.
    /// </summary>
    public class SoapClienteServicio
    {
        private static readonly XNamespace Soap = "http://schemas.xmlsoap.org/soap/envelope/";

        private readonly HttpClient _http;
        private readonly ServicioSoapOpciones _opciones;

        public SoapClienteServicio(HttpClient http, IOptions<ServicioSoapOpciones> opciones)
        {
            _http = http;
            _opciones = opciones.Value;
        }

        private XNamespace Ws => _opciones.Namespace;

        // ---------- Operaciones ----------

        public async Task<int> LoginAsync(string usuario, string contrasena)
        {
            var cuerpo = Operacion("login",
                new XElement(Ws + "usuario", usuario),
                new XElement(Ws + "contrasena", contrasena));

            var respuesta = await EnviarAsync("login", cuerpo);
            return LeerEntero(respuesta, "loginResult");
        }

        /// <summary>
        /// tipoMovimiento: 003 depósito, 004 retiro y 008 transferencia (que exige cuentaDestino).
        /// El servicio espera el importe como texto con punto decimal.
        /// </summary>
        public async Task<int> RegistrarMovimientoAsync(string cuenta, decimal importe, string tipoMovimiento, string? cuentaDestino)
        {
            var cuerpo = Operacion("regDeposito",
                new XElement(Ws + "cuenta", cuenta),
                new XElement(Ws + "importe", importe.ToString("0.00", CultureInfo.InvariantCulture)),
                new XElement(Ws + "tipoMovimiento", tipoMovimiento),
                new XElement(Ws + "cuentaDestino", cuentaDestino ?? ""));

            var respuesta = await EnviarAsync("regDeposito", cuerpo);
            return LeerEntero(respuesta, "regDepositoResult");
        }

        public async Task<List<Movimiento>> TraerMovimientosAsync(string cuenta)
        {
            var cuerpo = Operacion("traerMovimientos", new XElement(Ws + "cuenta", cuenta));

            var respuesta = await EnviarAsync("traerMovimientos", cuerpo);
            return LeerMovimientos(respuesta);
        }

        // ---------- Infraestructura SOAP ----------

        private XElement Operacion(string operacion, params XElement[] parametros)
        {
            // El namespace se declara en el envoltorio de la operación, así los
            // parámetros quedan calificados igual que en el cliente de referencia.
            return new XElement(Ws + operacion, parametros);
        }

        private async Task<XDocument> EnviarAsync(string operacion, XElement cuerpo)
        {
            var sobre = new XElement(Soap + "Envelope",
                new XAttribute(XNamespace.Xmlns + "soapenv", Soap.NamespaceName),
                new XElement(Soap + "Header"),
                new XElement(Soap + "Body", cuerpo));

            using var peticion = new HttpRequestMessage(HttpMethod.Post, _opciones.Url)
            {
                Content = new StringContent(sobre.ToString(SaveOptions.DisableFormatting), Encoding.UTF8, "text/xml")
            };
            peticion.Headers.Add("SOAPAction", $"\"{_opciones.NamespaceAccion}{operacion}\"");

            HttpResponseMessage respuestaHttp;
            try
            {
                respuestaHttp = await _http.SendAsync(peticion);
            }
            catch (Exception ex) when (ex is HttpRequestException || ex is TaskCanceledException)
            {
                throw new SoapClienteException(
                    $"No se pudo conectar con el servicio web ({_opciones.Url}). Verifica que el servidor esté en ejecución.");
            }

            var texto = await respuestaHttp.Content.ReadAsStringAsync();

            XDocument documento;
            try
            {
                documento = XDocument.Parse(texto);
            }
            catch (XmlException)
            {
                throw new SoapClienteException(
                    $"El servicio respondió algo inesperado (HTTP {(int)respuestaHttp.StatusCode}).");
            }

            var falla = documento.Descendants().FirstOrDefault(e => e.Name.LocalName == "Fault");
            if (falla != null)
            {
                throw new SoapClienteException(ExtraerMensajeFalla(falla));
            }

            return documento;
        }

        private static string ExtraerMensajeFalla(XElement falla)
        {
            // Con includeExceptionDetailInFaults=true el mensaje real viene en
            // <detail><ExceptionDetail><Message>; si no, se usa <faultstring>.
            var detalle = falla.Descendants().FirstOrDefault(e => e.Name.LocalName == "detail")
                ?.Descendants().FirstOrDefault(e => e.Name.LocalName == "Message")?.Value;
            if (!string.IsNullOrWhiteSpace(detalle)) return detalle;

            var faultString = falla.Descendants().FirstOrDefault(e => e.Name.LocalName == "faultstring")?.Value;
            return string.IsNullOrWhiteSpace(faultString) ? "El servicio devolvió un error." : faultString;
        }

        // ---------- Parseo ----------

        private static int LeerEntero(XDocument documento, string etiqueta)
        {
            var nodo = documento.Descendants().FirstOrDefault(e => e.Name.LocalName == etiqueta);
            int valor;
            if (nodo != null && int.TryParse(nodo.Value, NumberStyles.Integer, CultureInfo.InvariantCulture, out valor))
            {
                return valor;
            }

            return -1;
        }

        private static List<Movimiento> LeerMovimientos(XDocument documento)
        {
            var movimientos = new List<Movimiento>();

            foreach (var nodo in documento.Descendants().Where(e => e.Name.LocalName == "movimiento"))
            {
                double importe;
                double.TryParse(Valor(nodo, "importe"), NumberStyles.Any, CultureInfo.InvariantCulture, out importe);

                movimientos.Add(new Movimiento
                {
                    NroMov = Valor(nodo, "nromov"),
                    Fecha = Valor(nodo, "fecha"),
                    Tipo = Valor(nodo, "tipo"),
                    Accion = Valor(nodo, "accion"),
                    Importe = importe,
                    Cuenta = Valor(nodo, "cuenta")
                });
            }

            return movimientos;
        }

        private static string Valor(XElement nodo, string etiqueta)
        {
            var hijo = nodo.Elements().FirstOrDefault(e => e.Name.LocalName == etiqueta);
            return hijo == null ? "" : hijo.Value;
        }
    }
}
