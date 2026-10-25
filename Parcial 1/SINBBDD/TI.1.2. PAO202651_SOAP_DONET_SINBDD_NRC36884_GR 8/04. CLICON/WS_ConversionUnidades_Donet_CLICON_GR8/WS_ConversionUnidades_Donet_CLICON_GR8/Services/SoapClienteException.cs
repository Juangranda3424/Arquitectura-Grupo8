namespace WS_ConversionUnidades_Donet_CLICON_GR8.Services
{
    public class SoapClienteException : Exception
    {
        // true cuando el servicio rechazó el token (falta, es inválido o la sesión expiró)
        public bool AccesoDenegado { get; }

        public SoapClienteException(string mensaje, bool accesoDenegado = false) : base(mensaje)
        {
            AccesoDenegado = accesoDenegado;
        }
    }
}
