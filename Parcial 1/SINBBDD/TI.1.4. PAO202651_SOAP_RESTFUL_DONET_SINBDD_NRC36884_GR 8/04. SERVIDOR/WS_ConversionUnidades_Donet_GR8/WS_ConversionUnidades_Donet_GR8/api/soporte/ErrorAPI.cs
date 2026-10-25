namespace WS_ConversionUnidades_Donet_GR8.api.soporte
{
    /// <summary>
    /// Excepción de la capa REST que transporta el código HTTP con el que debe responder el servicio.
    /// Los recursos la lanzan para errores de contrato (401, 404, 415, ...) y MapeadorExcepciones la
    /// traduce al envelope JSON de error.
    /// </summary>
    public class ErrorAPI : Exception
    {
        public int codigoHttp { get; }

        public ErrorAPI(int codigoHttp, string mensaje) : base(mensaje)
        {
            this.codigoHttp = codigoHttp;
        }
    }
}
