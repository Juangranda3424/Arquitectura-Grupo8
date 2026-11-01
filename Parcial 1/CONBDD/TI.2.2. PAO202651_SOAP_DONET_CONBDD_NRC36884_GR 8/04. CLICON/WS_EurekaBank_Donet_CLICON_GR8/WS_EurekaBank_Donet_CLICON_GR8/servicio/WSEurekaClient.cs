using System.Collections.Generic;
using System.ServiceModel;

namespace WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.servicio
{
    /// <summary>
    /// Proxy WCF del servicio SOAP. La direccion del endpoint se configura en App.config
    /// (endpoint WSEurekaPort).
    /// </summary>
    public class WSEurekaClient : ClientBase<IWSEureka>, IWSEureka
    {
        public WSEurekaClient() : base("WSEurekaPort")
        {
        }

        public int login(string usuario, string contrasena)
        {
            return Channel.login(usuario, contrasena);
        }

        public int regDeposito(string cuenta, string importe, string tipoMovimiento, string cuentaDestino)
        {
            return Channel.regDeposito(cuenta, importe, tipoMovimiento, cuentaDestino);
        }

        public List<MovimientoSoap> traerMovimientos(string cuenta)
        {
            return Channel.traerMovimientos(cuenta);
        }
    }
}
