namespace WS_EurekaBank_Donet_CLIWEB_GR8.Services
{
    public class ServicioSoapOpciones
    {
        /// <summary>Dirección del servicio WCF WS_EurekaBank_Donet_GR8 (WSEureka.svc).</summary>
        public string Url { get; set; } = "";

        /// <summary>Namespace del contrato (los parámetros viajan calificados en este namespace).</summary>
        public string Namespace { get; set; } = "http://ws.monster.edu.ec/";

        /// <summary>Prefijo de los SOAPAction del contrato: namespace + nombre del servicio.</summary>
        public string NamespaceAccion { get; set; } = "http://ws.monster.edu.ec/WSEureka/";
    }
}
