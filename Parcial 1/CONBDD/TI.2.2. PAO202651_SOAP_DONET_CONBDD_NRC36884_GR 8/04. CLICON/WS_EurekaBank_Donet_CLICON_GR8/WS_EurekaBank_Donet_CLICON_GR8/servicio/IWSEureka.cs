using System.Collections.Generic;
using System.Runtime.Serialization;
using System.ServiceModel;

namespace WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.servicio
{
    /// <summary>
    /// Contrato del servicio SOAP del servidor WCF WS_EurekaBank_Donet_GR8.
    /// </summary>
    [ServiceContract(Name = "WSEureka", Namespace = "http://ws.monster.edu.ec/")]
    public interface IWSEureka
    {
        [OperationContract(
            Action = "http://ws.monster.edu.ec/WSEureka/login",
            ReplyAction = "http://ws.monster.edu.ec/WSEureka/loginResponse")]
        int login(string usuario, string contrasena);

        [OperationContract(
            Action = "http://ws.monster.edu.ec/WSEureka/regDeposito",
            ReplyAction = "http://ws.monster.edu.ec/WSEureka/regDepositoResponse")]
        int regDeposito(string cuenta, string importe, string tipoMovimiento, string cuentaDestino);

        [OperationContract(
            Action = "http://ws.monster.edu.ec/WSEureka/traerMovimientos",
            ReplyAction = "http://ws.monster.edu.ec/WSEureka/traerMovimientosResponse")]
        List<MovimientoSoap> traerMovimientos(string cuenta);
    }

    /// <summary>
    /// Movimiento tal como viaja en el sobre SOAP.
    /// </summary>
    [DataContract(Name = "movimiento", Namespace = "http://ws.monster.edu.ec/")]
    public class MovimientoSoap
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
