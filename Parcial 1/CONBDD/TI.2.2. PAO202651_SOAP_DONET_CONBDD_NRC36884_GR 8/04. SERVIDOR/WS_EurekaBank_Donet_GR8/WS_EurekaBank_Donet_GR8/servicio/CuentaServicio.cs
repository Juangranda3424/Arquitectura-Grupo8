using System;
using System.Collections.Generic;
using System.Globalization;
using WS_EurekaBank_Donet_GR8.ec.edu.monster.dao;
using WS_EurekaBank_Donet_GR8.ec.edu.monster.modelo;

namespace WS_EurekaBank_Donet_GR8.ec.edu.monster.servicio
{
    /// <summary>
    /// Reglas de negocio de los movimientos de cuenta:
    /// deposito (003), retiro (004) y transferencia (009 en origen / 008 en destino).
    /// </summary>
    public class CuentaServicio
    {
        private const string EmpleadoPorDefecto = "0001";
        private const string TipoDeposito = "003";
        private const string TipoRetiro = "004";
        private const string TipoTransferenciaSalida = "009";
        private const string TipoTransferenciaIngreso = "008";

        private readonly CuentaDAO cuentaDAO = new CuentaDAO();
        private readonly MovimientoDAO movimientoDAO = new MovimientoDAO();

        /// <summary>
        /// Actualiza el saldo y registra el movimiento.
        /// tipo: DEP (deposito), RET (retiro) o TRA (transferencia a cuentaDestino).
        /// </summary>
        public bool ActualizarSaldoYRegistrarMovimiento(string codigoCuenta, string valorMovimiento, string tipo, string cuentaDestino)
        {
            try
            {
                double importe = double.Parse(valorMovimiento, CultureInfo.InvariantCulture);
                if (importe <= 0)
                {
                    return false;
                }

                if (tipo != null && tipo.Equals("RET", StringComparison.OrdinalIgnoreCase))
                {
                    Cuenta cuenta = cuentaDAO.ObtenerCuentaPorCodigo(codigoCuenta);
                    if (cuenta == null || cuenta.DecCuenSaldo < importe)
                    {
                        return false;
                    }

                    if (!cuentaDAO.ActualizarSaldoCuenta(codigoCuenta, (-importe).ToString(CultureInfo.InvariantCulture)))
                    {
                        return false;
                    }

                    RegistrarMovimiento(codigoCuenta, importe, TipoRetiro, null);
                }
                else if (tipo != null && tipo.Equals("DEP", StringComparison.OrdinalIgnoreCase))
                {
                    if (!cuentaDAO.ActualizarSaldoCuenta(codigoCuenta, valorMovimiento))
                    {
                        return false;
                    }

                    RegistrarMovimiento(codigoCuenta, importe, TipoDeposito, null);
                }
                else if (tipo != null && tipo.Equals("TRA", StringComparison.OrdinalIgnoreCase))
                {
                    if (string.IsNullOrEmpty(cuentaDestino))
                    {
                        return false;
                    }

                    Cuenta cuentaOrigen = cuentaDAO.ObtenerCuentaPorCodigo(codigoCuenta);
                    if (cuentaOrigen == null || cuentaOrigen.DecCuenSaldo < importe)
                    {
                        return false;
                    }

                    Cuenta cuentaDestinoModelo = cuentaDAO.ObtenerCuentaPorCodigo(cuentaDestino);
                    if (cuentaDestinoModelo == null)
                    {
                        return false;
                    }

                    if (!cuentaDAO.ActualizarSaldoCuenta(codigoCuenta, (-importe).ToString(CultureInfo.InvariantCulture)))
                    {
                        return false;
                    }

                    if (!cuentaDAO.ActualizarSaldoCuenta(cuentaDestino, valorMovimiento))
                    {
                        // Deshacer el cargo en la cuenta origen si no se pudo abonar el destino
                        cuentaDAO.ActualizarSaldoCuenta(codigoCuenta, valorMovimiento);
                        return false;
                    }

                    RegistrarMovimiento(codigoCuenta, importe, TipoTransferenciaSalida, cuentaDestino);
                    RegistrarMovimiento(cuentaDestino, importe, TipoTransferenciaIngreso, codigoCuenta);
                }
                else
                {
                    return false;
                }

                return true;
            }
            catch (FormatException)
            {
                return false;
            }
            catch (OverflowException)
            {
                return false;
            }
        }

        public Cuenta ObtenerCuenta(string codigoCuenta)
        {
            return cuentaDAO.ObtenerCuentaPorCodigo(codigoCuenta);
        }

        private void RegistrarMovimiento(string codigoCuenta, double importe, string codigoTipo, string cuentaReferencia)
        {
            MovimientoModel movimiento = new MovimientoModel();
            movimiento.CodigoCuenta = codigoCuenta;
            movimiento.NumeroMovimiento = ObtenerSiguienteNumeroMovimiento(codigoCuenta);
            movimiento.FechaMovimiento = DateTime.Now.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
            movimiento.CodigoEmpleado = EmpleadoPorDefecto;
            movimiento.CodigoTipoMovimiento = codigoTipo;
            movimiento.ImporteMovimiento = importe;
            movimiento.CuentaReferencia = cuentaReferencia;

            movimientoDAO.RegistrarMovimiento(movimiento);
        }

        private int ObtenerSiguienteNumeroMovimiento(string codigoCuenta)
        {
            List<MovimientoModel> movimientos = movimientoDAO.ObtenerMovimientosPorCuenta(codigoCuenta);
            int numeroMovimiento = 1;

            foreach (MovimientoModel movimiento in movimientos)
            {
                if (movimiento.NumeroMovimiento >= numeroMovimiento)
                {
                    numeroMovimiento = movimiento.NumeroMovimiento + 1;
                }
            }

            return numeroMovimiento;
        }
    }
}
