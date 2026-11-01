namespace WS_EurekaBank_Donet_CLIWEB_GR8.Models
{
    public record TipoDeMovimiento(string Codigo, string Etiqueta);

    /// <summary>
    /// Códigos de tipo de movimiento que acepta la operación regDeposito del servicio.
    /// </summary>
    public static class TiposMovimiento
    {
        public const string Deposito = "003";
        public const string Retiro = "004";
        public const string Transferencia = "008";

        public static readonly IReadOnlyList<TipoDeMovimiento> Todos = new[]
        {
            new TipoDeMovimiento(Deposito, "Depósito"),
            new TipoDeMovimiento(Retiro, "Retiro"),
            new TipoDeMovimiento(Transferencia, "Transferencia")
        };

        public static bool EsValido(string? codigo) =>
            codigo != null && Todos.Any(t => t.Codigo == codigo);
    }
}
