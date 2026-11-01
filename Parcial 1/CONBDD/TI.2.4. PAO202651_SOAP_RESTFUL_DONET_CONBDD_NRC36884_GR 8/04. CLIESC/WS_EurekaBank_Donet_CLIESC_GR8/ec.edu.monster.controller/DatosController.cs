using System;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.model;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controller
{
    public class DatosController
    {
        public CuentaModel Consultar(string cuenta)
        {
            if (string.IsNullOrWhiteSpace(cuenta))
            {
                throw new ArgumentException("Ingrese el número de cuenta");
            }

            return ApiClient.Get<CuentaModel>("Cuenta/" + cuenta.Trim());
        }
    }
}
