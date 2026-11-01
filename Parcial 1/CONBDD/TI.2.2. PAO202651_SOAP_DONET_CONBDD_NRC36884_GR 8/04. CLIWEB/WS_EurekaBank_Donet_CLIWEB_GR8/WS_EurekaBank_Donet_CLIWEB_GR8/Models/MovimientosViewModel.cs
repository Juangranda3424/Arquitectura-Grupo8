using System.ComponentModel.DataAnnotations;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Models
{
    /// <summary>
    /// Formulario de consulta de movimientos de una cuenta, con filtro por tipo.
    /// </summary>
    public class MovimientosViewModel
    {
        [Required(ErrorMessage = "Ingresa la cuenta.")]
        [Display(Name = "Cuenta")]
        public string Cuenta { get; set; } = "";

        // Opcional: una cadena vacía significa "Todos". Se declara anulable porque el
        // enlace de modelos convierte los valores vacíos del formulario en null.
        [Display(Name = "Filtrar por tipo")]
        public string? FiltroTipo { get; set; }

        // Datos de salida (no vienen del formulario)
        public List<Movimiento> Movimientos { get; set; } = new();
        public bool Consulto { get; set; }
        public string? Mensaje { get; set; }
        public string? Error { get; set; }
        public string? Usuario { get; set; }

        public static readonly IReadOnlyList<TipoDeMovimiento> Filtros = new[]
        {
            new TipoDeMovimiento("", "Todos"),
            new TipoDeMovimiento("Deposito", "Depósito"),
            new TipoDeMovimiento("Retiro", "Retiro"),
            new TipoDeMovimiento("Transferencia", "Transferencia")
        };
    }
}
