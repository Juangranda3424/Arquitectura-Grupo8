namespace WS_EurekaBank_Donet_CLIESC_GR8.ec.edu.monster.model
{
    /// <summary>Respuesta de los endpoints de depósito, retiro y transferencia.</summary>
    public class OperacionResponse
    {
        public bool Success { get; set; }
        public double Saldo { get; set; }
        public string Mensaje { get; set; }
    }

    /// <summary>Resultado ya interpretado que consumen las vistas.</summary>
    public class OperacionResultado
    {
        public bool Exito { get; set; }
        public double Saldo { get; set; }
        public string Mensaje { get; set; }

        public OperacionResultado(bool exito, double saldo, string mensaje)
        {
            Exito = exito;
            Saldo = saldo;
            Mensaje = mensaje;
        }
    }
}
