using System;

namespace WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.modelo
{
    public class Movimiento
    {
        public string Accion { get; set; }
        public string Cuenta { get; set; }
        public DateTime Fecha { get; set; }
        public double Importe { get; set; }
        public int NroMov { get; set; }
        public string Tipo { get; set; }

        public override string ToString()
        {
            return string.Format(
                "Movimiento {{ Accion: {0}, Cuenta: {1}, Fecha: {2:yyyy-MM-dd}, Importe: {3}, Nro. Movimiento: {4}, Tipo: {5} }}",
                Accion, Cuenta, Fecha, Importe, NroMov, Tipo);
        }
    }
}
