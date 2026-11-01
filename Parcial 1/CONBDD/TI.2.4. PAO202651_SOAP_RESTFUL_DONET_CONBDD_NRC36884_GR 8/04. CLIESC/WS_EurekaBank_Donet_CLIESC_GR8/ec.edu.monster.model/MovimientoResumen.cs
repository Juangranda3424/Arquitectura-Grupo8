using System.Collections.Generic;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.model
{
    /// <summary>Resumen de movimientos y saldos de una cuenta para la vista.</summary>
    public class MovimientoResumen
    {
        public CuentaModel Cuenta { get; set; }
        public List<MovimientoModel> Movimientos { get; set; }
        public double SaldoActual { get; set; }
        public double TotalIngresos { get; set; }
        public double TotalEgresos { get; set; }
        public double SaldoNeto { get { return TotalIngresos - TotalEgresos; } }
    }
}
