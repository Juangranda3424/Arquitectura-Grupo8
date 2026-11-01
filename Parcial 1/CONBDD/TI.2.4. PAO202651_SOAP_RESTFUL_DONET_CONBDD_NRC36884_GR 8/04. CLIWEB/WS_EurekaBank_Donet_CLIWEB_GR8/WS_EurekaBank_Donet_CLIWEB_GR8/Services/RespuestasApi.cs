namespace WS_EurekaBank_Donet_CLIWEB_GR8.Services
{
    /// <summary>
    /// Respuesta de POST /Login: {"success": bool, "message": string}.
    /// El servidor responde 401 con success=false cuando las credenciales son inválidas.
    /// </summary>
    public class RespuestaLogin
    {
        public bool Success { get; set; }
        public string? Message { get; set; }
    }

    /// <summary>
    /// Respuesta de POST /Cuenta/deposito, /Cuenta/retiro y /Cuenta/transferencia:
    /// {"success": bool, "saldo": number, "mensaje": string}.
    /// </summary>
    public class RespuestaOperacion
    {
        public bool Success { get; set; }
        public double Saldo { get; set; }
        public string? Mensaje { get; set; }
    }
}
