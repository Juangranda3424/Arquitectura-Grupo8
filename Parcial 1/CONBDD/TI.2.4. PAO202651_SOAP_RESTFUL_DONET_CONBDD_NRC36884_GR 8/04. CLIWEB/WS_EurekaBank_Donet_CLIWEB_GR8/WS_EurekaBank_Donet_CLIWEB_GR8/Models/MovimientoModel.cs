namespace WS_EurekaBank_Donet_CLIWEB_GR8.Models
{
    /// <summary>Movimiento de una cuenta, tal como lo publica POST /Movimiento.</summary>
    public class MovimientoModel
    {
        public string? CodigoCuenta { get; set; }
        public int NumeroMovimiento { get; set; }
        public string? FechaMovimiento { get; set; }
        public string? CodigoEmpleado { get; set; }
        public string? CodigoTipoMovimiento { get; set; }
        public string? TipoDescripcion { get; set; }
        public double ImporteMovimiento { get; set; }
        public string? CuentaReferencia { get; set; }
        public double Saldo { get; set; } // Saldo acumulado después del movimiento
    }
}
