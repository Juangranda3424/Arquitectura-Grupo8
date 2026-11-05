using System.Collections.Generic;
using CoreWCF;
using WS_EurekaBank_Donet_GR8.modelo;

namespace WS_EurekaBank_Donet_GR8.webservicio
{
    /// <summary>
    /// Contrato SOAP del banco. El namespace se mantiene igual al del servicio
    /// de referencia (http://ws.monster.edu.ec/) para que los cuatro clientes
    /// solo cambien la direccion del endpoint.
    /// </summary>
    [ServiceContract(Name = "WSEureka", Namespace = "http://ws.monster.edu.ec/")]
    public interface IWSEureka
    {
        [OperationContract(
            Action = "http://ws.monster.edu.ec/WSEureka/login",
            ReplyAction = "http://ws.monster.edu.ec/WSEureka/loginResponse")]
        int login(string usuario, string contrasena);

        /// <summary>
        /// Registra un movimiento: tipoMovimiento 003 = deposito, 004 = retiro,
        /// 008/009 = transferencia (requiere cuentaDestino).
        /// Devuelve 1 si el movimiento se registro y -1 si fallo.
        /// </summary>
        [OperationContract(
            Action = "http://ws.monster.edu.ec/WSEureka/regDeposito",
            ReplyAction = "http://ws.monster.edu.ec/WSEureka/regDepositoResponse")]
        int regDeposito(string cuenta, string importe, string tipoMovimiento, string cuentaDestino);

        [OperationContract(
            Action = "http://ws.monster.edu.ec/WSEureka/traerMovimientos",
            ReplyAction = "http://ws.monster.edu.ec/WSEureka/traerMovimientosResponse")]
        List<Movimiento> traerMovimientos(string cuenta);
    }
}
