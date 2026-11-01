using System;
using System.Globalization;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.model;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controller
{
    public class DepositoController
    {
        public OperacionResultado Depositar(string cuenta, string montoTexto)
        {
            if (string.IsNullOrWhiteSpace(cuenta) || string.IsNullOrWhiteSpace(montoTexto))
            {
                return new OperacionResultado(false, 0, "Por favor complete todos los campos");
            }

            double monto;
            if (!double.TryParse(montoTexto, NumberStyles.Number, CultureInfo.CurrentCulture, out monto) || monto <= 0)
            {
                return new OperacionResultado(false, 0, "El monto debe ser un número positivo válido");
            }

            try
            {
                var peticion = new { Cuenta = cuenta.Trim(), Monto = monto };
                var respuesta = ApiClient.Post<OperacionResponse>("Cuenta/deposito", peticion);

                if (respuesta != null && respuesta.Success)
                {
                    return new OperacionResultado(true, respuesta.Saldo,
                        string.IsNullOrEmpty(respuesta.Mensaje) ? "Depósito realizado con éxito" : respuesta.Mensaje);
                }

                return new OperacionResultado(false, respuesta != null ? respuesta.Saldo : 0,
                    respuesta != null && !string.IsNullOrEmpty(respuesta.Mensaje)
                        ? respuesta.Mensaje
                        : "Error al realizar el depósito. Verifique los datos de la cuenta");
            }
            catch (Exception ex)
            {
                return new OperacionResultado(false, 0, "Error al conectar con el servidor: " + ex.Message);
            }
        }
    }
}
