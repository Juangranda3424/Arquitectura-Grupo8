using System;
using System.Collections.Generic;
using System.Globalization;
using WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.modelo;

namespace WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.servicio
{
    /// <summary>
    /// Adapta el proxy SOAP al modelo de la aplicacion.
    /// </summary>
    public class ServicioSoap
    {
        public const string TipoDeposito = "003";
        public const string TipoRetiro = "004";
        public const string TipoTransferencia = "008";

        public Respuesta Login(string usuario, string contrasena)
        {
            using (WSEurekaClient cliente = new WSEurekaClient())
            {
                return new Respuesta { Resultado = cliente.login(usuario, contrasena) };
            }
        }

        public Respuesta RegistrarMovimiento(string cuenta, string importe, string tipoMovimiento, string cuentaDestino)
        {
            using (WSEurekaClient cliente = new WSEurekaClient())
            {
                return new Respuesta { Resultado = cliente.regDeposito(cuenta, importe, tipoMovimiento, cuentaDestino) };
            }
        }

        public List<Movimiento> TraerMovimientos(string cuenta)
        {
            List<Movimiento> movimientos = new List<Movimiento>();

            using (WSEurekaClient cliente = new WSEurekaClient())
            {
                List<MovimientoSoap> respuesta = cliente.traerMovimientos(cuenta);
                if (respuesta == null)
                {
                    return movimientos;
                }

                foreach (MovimientoSoap mov in respuesta)
                {
                    movimientos.Add(new Movimiento
                    {
                        Accion = mov.Accion,
                        Cuenta = mov.Cuenta,
                        Fecha = InterpretarFecha(mov.Fecha),
                        Importe = mov.Importe,
                        NroMov = mov.NroMov,
                        Tipo = mov.Tipo
                    });
                }
            }

            return movimientos;
        }

        private static DateTime InterpretarFecha(string fecha)
        {
            DateTime valor;
            if (!string.IsNullOrWhiteSpace(fecha)
                && DateTime.TryParse(fecha, CultureInfo.InvariantCulture, DateTimeStyles.None, out valor))
            {
                return valor;
            }

            return DateTime.MinValue;
        }
    }
}
