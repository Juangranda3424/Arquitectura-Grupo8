namespace WS_EurekaBank_Donet_CLIWEB_GR8.Models
{
    /// <summary>
    /// Movimiento devuelto por la operación traerMovimientos del servicio SOAP.
    /// </summary>
    public class Movimiento
    {
        public string NroMov { get; set; } = "";
        public string Fecha { get; set; } = "";
        public string Tipo { get; set; } = "";
        public string Accion { get; set; } = "";
        public double Importe { get; set; }
        public string Cuenta { get; set; } = "";
    }
}
