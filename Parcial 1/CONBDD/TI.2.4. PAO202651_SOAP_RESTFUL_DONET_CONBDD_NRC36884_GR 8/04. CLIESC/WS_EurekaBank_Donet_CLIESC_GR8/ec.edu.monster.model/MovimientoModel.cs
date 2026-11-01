using System;

namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.model
{
    public class MovimientoModel
    {
        public string CodigoCuenta { get; set; }
        public int NumeroMovimiento { get; set; }
        public string FechaMovimiento { get; set; }
        public DateTime? FechaMovimientoDt
        {
            get
            {
                DateTime fecha;
                if (DateTime.TryParse(FechaMovimiento, out fecha))
                {
                    return fecha;
                }
                return null;
            }
        }

        public string CodigoEmpleado { get; set; }
        public string CodigoTipoMovimiento { get; set; }
        public string TipoDescripcion { get; set; }
        public double ImporteMovimiento { get; set; }
        public string CuentaReferencia { get; set; }
        public double Saldo { get; set; }
    }
}
