namespace WS_EurekaBank_Donet_CLIWEB_GR8.Services
{
    /// <summary>
    /// Error de comunicación con el servicio REST. El mensaje ya está listo para mostrarse al usuario.
    /// </summary>
    public class RestClienteException : Exception
    {
        public RestClienteException(string mensaje) : base(mensaje)
        {
        }
    }
}
