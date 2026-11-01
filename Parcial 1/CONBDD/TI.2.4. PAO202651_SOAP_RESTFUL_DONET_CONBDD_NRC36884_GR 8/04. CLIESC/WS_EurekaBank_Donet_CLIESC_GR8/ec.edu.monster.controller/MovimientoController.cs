using System;
using System.Collections.Generic;
using WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.model;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.controller
{
    public class MovimientoController
    {
        // Códigos de tipo de movimiento del esquema de la base de datos
        private static readonly HashSet<string> CodigosEgreso = new HashSet<string> { "004", "009", "002", "006", "007", "010" };
        private static readonly HashSet<string> CodigosIngreso = new HashSet<string> { "001", "003", "005", "008" };

        public MovimientoResumen Consultar(string cuenta)
        {
            if (string.IsNullOrWhiteSpace(cuenta))
            {
                throw new ArgumentException("Ingrese el número de cuenta");
            }

            var peticion = new { Numcuenta = cuenta.Trim() };
            var movimientos = ApiClient.Post<List<MovimientoModel>>("Movimiento", peticion) ?? new List<MovimientoModel>();
            var cuentaModel = ApiClient.Get<CuentaModel>("Cuenta/" + cuenta.Trim());

            double totalIngresos = 0;
            double totalEgresos = 0;

            foreach (var mov in movimientos)
            {
                if (mov == null)
                {
                    continue;
                }

                double importe = mov.ImporteMovimiento;
                string codigo = mov.CodigoTipoMovimiento ?? string.Empty;
                string descripcion = (mov.TipoDescripcion ?? string.Empty).ToLowerInvariant().Trim();

                if (CodigosEgreso.Contains(codigo) || descripcion == "retiro")
                {
                    totalEgresos += importe;
                }
                else if (CodigosIngreso.Contains(codigo) || descripcion == "deposito" || descripcion == "depósito" ||
                         descripcion == "transferencia" || descripcion == "apertura de cuenta")
                {
                    totalIngresos += importe;
                }
            }

            return new MovimientoResumen
            {
                Cuenta = cuentaModel,
                Movimientos = movimientos,
                SaldoActual = cuentaModel != null ? cuentaModel.DecCuenSaldo : 0,
                TotalIngresos = totalIngresos,
                TotalEgresos = totalEgresos
            };
        }
    }
}
