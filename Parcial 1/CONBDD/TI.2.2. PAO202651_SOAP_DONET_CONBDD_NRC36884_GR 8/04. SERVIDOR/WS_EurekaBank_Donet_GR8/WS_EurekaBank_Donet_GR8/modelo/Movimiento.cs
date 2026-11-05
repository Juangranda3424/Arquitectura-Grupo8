using System.Runtime.Serialization;

namespace WS_EurekaBank_Donet_GR8.modelo
{
    /// <summary>
    /// Movimiento transportado por el servicio SOAP.
    /// Conserva el contrato que consumen los cuatro clientes:
    /// accion (INGRESO/SALIDA), cuenta, fecha, importe, nromov y tipo.
    /// </summary>
    [DataContract(Name = "movimiento", Namespace = "http://ws.monster.edu.ec/")]
    public class Movimiento
    {
        [DataMember(Name = "accion", Order = 0)]
        public string Accion { get; set; }

        [DataMember(Name = "cuenta", Order = 1)]
        public string Cuenta { get; set; }

        [DataMember(Name = "fecha", Order = 2)]
        public string Fecha { get; set; }

        [DataMember(Name = "importe", Order = 3)]
        public double Importe { get; set; }

        [DataMember(Name = "nromov", Order = 4)]
        public int NroMov { get; set; }

        [DataMember(Name = "tipo", Order = 5)]
        public string Tipo { get; set; }
    }
}
