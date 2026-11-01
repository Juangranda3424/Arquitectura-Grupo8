namespace WS_EurekaBank_Donet_CLIWEB_GR8.Services
{
    /// <summary>
    /// Error de comunicación con el servicio SOAP: el servidor no responde,
    /// devolvió una falla SOAP o una respuesta que no se pudo interpretar.
    /// </summary>
    public class SoapClienteException : Exception
    {
        public SoapClienteException(string mensaje) : base(mensaje)
        {
        }
    }
}
