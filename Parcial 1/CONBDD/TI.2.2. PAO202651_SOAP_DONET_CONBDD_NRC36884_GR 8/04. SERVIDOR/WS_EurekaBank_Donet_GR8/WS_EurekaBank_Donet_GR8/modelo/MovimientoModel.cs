namespace WS_EurekaBank_Donet_GR8.modelo
{
    /// <summary>
    /// Movimiento leido desde las tablas movimiento y tipomovimiento,
    /// con el saldo acumulado de la cuenta.
    /// </summary>
    public class MovimientoModel
    {
        public string CodigoCuenta { get; set; }
        public int NumeroMovimiento { get; set; }
        public string FechaMovimiento { get; set; }
        public string CodigoEmpleado { get; set; }
        public string CodigoTipoMovimiento { get; set; }
        public string TipoDescripcion { get; set; }
        public string TipoAccion { get; set; }
        public double ImporteMovimiento { get; set; }
        public string CuentaReferencia { get; set; }
        public double Saldo { get; set; }
    }
}
