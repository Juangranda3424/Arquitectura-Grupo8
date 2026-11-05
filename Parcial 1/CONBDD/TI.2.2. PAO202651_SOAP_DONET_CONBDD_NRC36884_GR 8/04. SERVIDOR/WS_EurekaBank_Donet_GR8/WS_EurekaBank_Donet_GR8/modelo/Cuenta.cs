using System;

namespace WS_EurekaBank_Donet_GR8.modelo
{
    /// <summary>
    /// Fila de la tabla cuenta.
    /// </summary>
    public class Cuenta
    {
        public string ChrCuenCodigo { get; set; }
        public string ChrMoneCodigo { get; set; }
        public string ChrSucucodigo { get; set; }
        public string ChrEmplCreaCuenta { get; set; }
        public string ChrClieCodigo { get; set; }
        public double DecCuenSaldo { get; set; }
        public DateTime DttCuenFechaCreacion { get; set; }
        public string VchCuenEstado { get; set; }
        public int IntCuenContMov { get; set; }
        public string ChrCuenClave { get; set; }
    }
}
