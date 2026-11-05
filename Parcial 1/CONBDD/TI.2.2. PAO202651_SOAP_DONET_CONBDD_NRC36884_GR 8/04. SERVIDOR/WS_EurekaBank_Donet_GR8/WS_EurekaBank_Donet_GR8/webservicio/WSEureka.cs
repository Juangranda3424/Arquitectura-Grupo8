using System;
using System.Collections.Generic;
using CoreWCF;
using WS_EurekaBank_Donet_GR8.modelo;
using WS_EurekaBank_Donet_GR8.servicio;

namespace WS_EurekaBank_Donet_GR8.webservicio
{
    public class WSEureka : IWSEureka
    {
        private readonly LoginServicio loginServicio = new LoginServicio();
        private readonly CuentaServicio cuentaServicio = new CuentaServicio();
        private readonly MovimientoServicio movimientoServicio = new MovimientoServicio();

        public int login(string usuario, string contrasena)
        {
            return loginServicio.Login(usuario, contrasena) ? 1 : -1;
        }

        public int regDeposito(string cuenta, string importe, string tipoMovimiento, string cuentaDestino)
        {
            string tipo = NormalizarTipoMovimiento(tipoMovimiento);
            if (string.IsNullOrEmpty(tipo) || string.IsNullOrEmpty(cuenta))
            {
                return -1;
            }

            try
            {
                return cuentaServicio.ActualizarSaldoYRegistrarMovimiento(cuenta, importe, tipo, cuentaDestino) ? 1 : -1;
            }
            catch (Exception)
            {
                // Cualquier fallo de base de datos o de datos de entrada se informa como registro fallido,
                // igual que el servicio de referencia.
                return -1;
            }
        }

        public List<Movimiento> traerMovimientos(string cuenta)
        {
            List<Movimiento> respuesta = new List<Movimiento>();

            if (string.IsNullOrEmpty(cuenta))
            {
                return respuesta;
            }

            try
            {
                foreach (MovimientoModel modelo in movimientoServicio.ObtenerMovimientos(cuenta))
                {
                    Movimiento movimiento = new Movimiento();
                    movimiento.Accion = modelo.TipoAccion;
                    movimiento.Cuenta = modelo.CodigoCuenta;
                    movimiento.Fecha = modelo.FechaMovimiento;
                    movimiento.Importe = modelo.ImporteMovimiento;
                    movimiento.NroMov = modelo.NumeroMovimiento;
                    movimiento.Tipo = modelo.TipoDescripcion;
                    respuesta.Add(movimiento);
                }
            }
            catch (Exception ex)
            {
                throw new FaultException("No se pudieron consultar los movimientos de la cuenta " + cuenta + ": " + ex.Message);
            }

            return respuesta;
        }

        /// <summary>
        /// Traduce el codigo de tipo de movimiento enviado por los clientes
        /// (003 deposito, 004 retiro, 008/009 transferencia) al tipo de negocio.
        /// </summary>
        private static string NormalizarTipoMovimiento(string tipoMovimiento)
        {
            if (string.IsNullOrWhiteSpace(tipoMovimiento))
            {
                return null;
            }

            string tipo = tipoMovimiento.Trim().ToUpperInvariant();

            if (tipo == "003" || tipo == "DEP" || tipo == "DEPOSITO")
            {
                return "DEP";
            }

            if (tipo == "004" || tipo == "RET" || tipo == "RETIRO")
            {
                return "RET";
            }

            if (tipo == "008" || tipo == "009" || tipo == "TRA" || tipo == "TRANSFERENCIA")
            {
                return "TRA";
            }

            return null;
        }
    }
}
