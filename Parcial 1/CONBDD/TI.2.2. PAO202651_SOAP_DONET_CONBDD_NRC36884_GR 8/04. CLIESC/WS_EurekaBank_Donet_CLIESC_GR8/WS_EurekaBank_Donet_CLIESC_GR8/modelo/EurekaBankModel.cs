using System.Globalization;
using System.Net;
using System.Security;
using System.Text;
using System.Xml.Linq;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.modelo
{
    /// <summary>
    /// Movimiento bancario mostrado en la tabla de la vista.
    /// </summary>
    public class Movimiento
    {
        public int NroMov { get; set; }
        public string Fecha { get; set; }
        public string Tipo { get; set; }
        public string Accion { get; set; }
        public double Importe { get; set; }
        public string Cuenta { get; set; }
    }

    /// <summary>
    /// Cliente SOAP del servidor WCF WS_EurekaBank_Donet_GR8.
    /// Envia sobres SOAP 1.1 con HttpWebRequest, igual que el cliente de referencia.
    /// </summary>
    public class EurekaBankModel
    {
        public const string UrlServicio = "http://localhost:8090/WS_EurekaBank_Donet_GR8/webservicio/WSEureka.svc";
        public const string EspacioNombres = "http://ws.monster.edu.ec/";

        public const string Deposito = "003";
        public const string Retiro = "004";
        public const string Transferencia = "008";

        public int Login(string usuario, string contrasena)
        {
            StringBuilder sobre = new StringBuilder();
            sobre.Append(CabeceraSobre());
            sobre.Append(AbrirOperacion("login"));
            sobre.Append("<usuario>" + Escapar(usuario) + "</usuario>");
            sobre.Append("<contrasena>" + Escapar(contrasena) + "</contrasena>");
            sobre.Append(CerrarOperacion("login"));
            sobre.Append(PieSobre());

            string respuesta = Enviar(sobre.ToString(), Accion("login"));
            return LeerEntero(respuesta, "loginResult") == 1 ? 1 : -1;
        }

        public int RegistrarMovimiento(string cuenta, string importe, string tipoMovimiento, string cuentaDestino)
        {
            StringBuilder sobre = new StringBuilder();
            sobre.Append(CabeceraSobre());
            sobre.Append(AbrirOperacion("regDeposito"));
            sobre.Append("<cuenta>" + Escapar(cuenta) + "</cuenta>");
            sobre.Append("<importe>" + Escapar(importe) + "</importe>");
            sobre.Append("<tipoMovimiento>" + Escapar(tipoMovimiento) + "</tipoMovimiento>");
            sobre.Append("<cuentaDestino>" + Escapar(cuentaDestino ?? string.Empty) + "</cuentaDestino>");
            sobre.Append(CerrarOperacion("regDeposito"));
            sobre.Append(PieSobre());

            string respuesta = Enviar(sobre.ToString(), Accion("regDeposito"));
            return LeerEntero(respuesta, "regDepositoResult") == 1 ? 1 : -1;
        }

        public List<Movimiento> ObtenerMovimientos(string cuenta)
        {
            StringBuilder sobre = new StringBuilder();
            sobre.Append(CabeceraSobre());
            sobre.Append(AbrirOperacion("traerMovimientos"));
            sobre.Append("<cuenta>" + Escapar(cuenta) + "</cuenta>");
            sobre.Append(CerrarOperacion("traerMovimientos"));
            sobre.Append(PieSobre());

            string respuesta = Enviar(sobre.ToString(), Accion("traerMovimientos"));
            return LeerMovimientos(respuesta);
        }

        private static string CabeceraSobre()
        {
            return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                + "  <soapenv:Header/>"
                + "  <soapenv:Body>";
        }

        /// <summary>
        /// Envoltorio de la operacion. El esquema del contrato WCF es
        /// elementFormDefault="qualified", por lo que el envoltorio y sus
        /// parametros deben viajar en el namespace http://ws.monster.edu.ec/.
        /// </summary>
        private static string AbrirOperacion(string operacion)
        {
            return "<" + operacion + " xmlns=\"" + EspacioNombres + "\">";
        }

        private static string CerrarOperacion(string operacion)
        {
            return "</" + operacion + ">";
        }

        private static string PieSobre()
        {
            return "  </soapenv:Body></soapenv:Envelope>";
        }

        private static string Accion(string operacion)
        {
            return EspacioNombres + "WSEureka/" + operacion;
        }

        private static string Enviar(string sobre, string accion)
        {
            HttpWebRequest peticion = (HttpWebRequest)WebRequest.Create(UrlServicio);
            peticion.Method = "POST";
            peticion.ContentType = "text/xml; charset=utf-8";
            peticion.Accept = "text/xml";
            peticion.Headers.Add("SOAPAction", accion);
            peticion.Timeout = 30000;

            byte[] cuerpo = Encoding.UTF8.GetBytes(sobre);
            peticion.ContentLength = cuerpo.Length;

            using (Stream salida = peticion.GetRequestStream())
            {
                salida.Write(cuerpo, 0, cuerpo.Length);
            }

            using (HttpWebResponse respuesta = (HttpWebResponse)peticion.GetResponse())
            using (StreamReader lector = new StreamReader(respuesta.GetResponseStream(), Encoding.UTF8))
            {
                return lector.ReadToEnd();
            }
        }

        private static int LeerEntero(string respuestaXml, string elemento)
        {
            XDocument documento = XDocument.Parse(respuestaXml);
            XElement nodo = documento.Descendants().FirstOrDefault(e => e.Name.LocalName == elemento);
            int valor;
            if (nodo != null && int.TryParse(nodo.Value, NumberStyles.Integer, CultureInfo.InvariantCulture, out valor))
            {
                return valor;
            }

            return -1;
        }

        private static List<Movimiento> LeerMovimientos(string respuestaXml)
        {
            List<Movimiento> movimientos = new List<Movimiento>();
            XDocument documento = XDocument.Parse(respuestaXml);

            foreach (XElement nodo in documento.Descendants().Where(e => e.Name.LocalName == "movimiento"))
            {
                Movimiento movimiento = new Movimiento();
                movimiento.Accion = Valor(nodo, "accion");
                movimiento.Cuenta = Valor(nodo, "cuenta");
                movimiento.Fecha = Valor(nodo, "fecha");
                movimiento.Tipo = Valor(nodo, "tipo");

                double importe;
                double.TryParse(Valor(nodo, "importe"), NumberStyles.Any, CultureInfo.InvariantCulture, out importe);
                movimiento.Importe = importe;

                int nroMov;
                int.TryParse(Valor(nodo, "nromov"), NumberStyles.Integer, CultureInfo.InvariantCulture, out nroMov);
                movimiento.NroMov = nroMov;

                movimientos.Add(movimiento);
            }

            return movimientos;
        }

        private static string Valor(XElement nodo, string elemento)
        {
            XElement hijo = nodo.Elements().FirstOrDefault(e => e.Name.LocalName == elemento);
            return hijo == null ? string.Empty : hijo.Value;
        }

        private static string Escapar(string valor)
        {
            return SecurityElement.Escape(valor ?? string.Empty) ?? string.Empty;
        }
    }
}
