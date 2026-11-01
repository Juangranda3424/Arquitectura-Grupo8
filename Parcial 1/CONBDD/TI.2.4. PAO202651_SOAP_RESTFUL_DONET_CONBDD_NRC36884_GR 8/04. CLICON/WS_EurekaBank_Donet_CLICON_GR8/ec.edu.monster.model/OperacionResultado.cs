namespace WS_EurekaBank_Donet_CLICON_GR8.ec.edu.monster.model
{
    /// <summary>
    /// Resultado de una operación de cuenta (depósito, retiro o transferencia).
    /// </summary>
    public class OperacionResultado
    {
        public bool Exito { get; set; }
        public double Saldo { get; set; }
        public string Mensaje { get; set; }

        public OperacionResultado()
        {
        }

        public OperacionResultado(bool exito, double saldo, string mensaje)
        {
            Exito = exito;
            Saldo = saldo;
            Mensaje = mensaje;
        }
    }
}
