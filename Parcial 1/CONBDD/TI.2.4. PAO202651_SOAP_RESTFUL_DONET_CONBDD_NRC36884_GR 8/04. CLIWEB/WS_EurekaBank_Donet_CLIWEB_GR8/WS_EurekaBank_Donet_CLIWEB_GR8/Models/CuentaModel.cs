namespace WS_EurekaBank_Donet_CLIWEB_GR8.Models
{
    /// <summary>Datos de la tabla Cuenta que publica GET /Cuenta/{codigo}.</summary>
    public class CuentaModel
    {
        public string? ChrCuenCodigo { get; set; }            // Código de la cuenta
        public string? ChrMoneCodigo { get; set; }            // Código de moneda
        public string? ChrSucucodigo { get; set; }            // Código de sucursal
        public string? ChrEmplCreaCuenta { get; set; }        // Empleado que creó la cuenta
        public string? ChrClieCodigo { get; set; }            // Código de cliente
        public double DecCuenSaldo { get; set; }              // Saldo de la cuenta
        public DateTime? DttCuenFechaCreacion { get; set; }   // Fecha de creación
        public string? VchCuenEstado { get; set; }            // ACTIVO, ANULADO, CANCELADO
        public int IntCuenContMov { get; set; }               // Contador de movimientos
        public string? ChrCuenClave { get; set; }             // Clave de la cuenta
    }
}
