using System;
using System.Globalization;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.model;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controller
{
    public class TransferenciaController
    {
        public OperacionResultado Transferir(string cuentaOrigen, string cuentaDestino, string montoTexto)
        {
            if (string.IsNullOrWhiteSpace(cuentaOrigen) || string.IsNullOrWhiteSpace(cuentaDestino) ||
                string.IsNullOrWhiteSpace(montoTexto))
            {
                return new OperacionResultado(false, 0, "Por favor complete todos los campos");
            }

            if (cuentaOrigen.Trim() == cuentaDestino.Trim())
            {
                return new OperacionResultado(false, 0, "La cuenta origen y destino no pueden ser la misma");
            }

            double monto;
            if (!double.TryParse(montoTexto, NumberStyles.Number, CultureInfo.CurrentCulture, out monto) || monto <= 0)
            {
                return new OperacionResultado(false, 0, "El monto debe ser un número positivo válido");
            }

            try
            {
                var peticion = new
                {
                    Cuenta = cuentaOrigen.Trim(),
                    Monto = monto,
                    CuentaDestino = cuentaDestino.Trim()
                };

                var respuesta = ApiClient.Post<OperacionResponse>("Cuenta/transferencia", peticion);

                if (respuesta != null && respuesta.Success)
                {
                    return new OperacionResultado(true, respuesta.Saldo,
                        string.IsNullOrEmpty(respuesta.Mensaje) ? "Transferencia realizada con éxito" : respuesta.Mensaje);
                }

                return new OperacionResultado(false, respuesta != null ? respuesta.Saldo : 0,
                    respuesta != null && !string.IsNullOrEmpty(respuesta.Mensaje)
                        ? respuesta.Mensaje
                        : "Error al realizar la transferencia. Verifique las cuentas y el saldo disponible");
            }
            catch (Exception ex)
            {
                return new OperacionResultado(false, 0, "Error al conectar con el servidor: " + ex.Message);
            }
        }
    }
}
