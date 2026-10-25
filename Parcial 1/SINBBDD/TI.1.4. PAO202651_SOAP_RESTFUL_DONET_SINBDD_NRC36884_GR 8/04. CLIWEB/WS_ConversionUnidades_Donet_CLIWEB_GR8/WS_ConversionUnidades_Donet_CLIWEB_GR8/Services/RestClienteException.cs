namespace WS_ConversionUnidades_Donet_CLIWEB_GR8.Services
{
    public class RestClienteException : Exception
    {
        // true cuando el servicio rechazó el token (falta, es inválido o la sesión expiró)
        public bool AccesoDenegado { get; }

        public RestClienteException(string mensaje, bool accesoDenegado = false) : base(mensaje)
        {
            AccesoDenegado = accesoDenegado;
        }
    }
}
