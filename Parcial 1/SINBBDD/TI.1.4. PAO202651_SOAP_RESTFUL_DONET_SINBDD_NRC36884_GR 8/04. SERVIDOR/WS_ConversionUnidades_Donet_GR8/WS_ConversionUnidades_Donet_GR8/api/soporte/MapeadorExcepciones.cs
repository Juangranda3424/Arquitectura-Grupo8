using Microsoft.AspNetCore.Diagnostics;

namespace WS_ConversionUnidades_Donet_GR8.api.soporte
{
    /// <summary>
    /// Middleware que traduce cualquier excepción de la capa REST al envelope JSON de error, de modo que
    /// el cliente nunca reciba un cuerpo distinto del contrato.
    /// Mapeos: ErrorAPI conserva su código; ArgumentException es 400 con el mensaje de validación;
    /// cualquier otra causa es 500 genérico (sin filtrar detalles internos).
    /// Los 404/405 del enrutador se completan con escribirCodigoEstado (UseStatusCodePages).
    /// </summary>
    public class MapeadorExcepciones
    {
        public const string MENSAJE_NO_ENCONTRADO = "Recurso no encontrado.";
        public const string MENSAJE_METODO_NO_PERMITIDO = "Método HTTP no permitido para este recurso.";
        public const string MENSAJE_ERROR_INTERNO = "Error interno del servidor.";
        public const string MENSAJE_SOLICITUD_INVALIDA = "Solicitud inválida.";

        private readonly RequestDelegate siguiente;
        private readonly ILogger<MapeadorExcepciones> registro;

        public MapeadorExcepciones(RequestDelegate siguiente, ILogger<MapeadorExcepciones> registro)
        {
            this.siguiente = siguiente;
            this.registro = registro;
        }

        public async Task InvokeAsync(HttpContext contexto)
        {
            try
            {
                await siguiente(contexto);
            }
            catch (Exception error) when (!contexto.Response.HasStarted)
            {
                (int codigo, string mensaje) = mapear(error);
                if (codigo == 500)
                {
                    registro.LogError(error, "Error no controlado en {Ruta}", contexto.Request.Path);
                }
                contexto.Response.Clear();
                await RespuestaAPI.escribir(contexto.Response, codigo, RespuestaAPI.error(codigo, mensaje));
            }
        }

        public static (int codigo, string mensaje) mapear(Exception error)
        {
            return error switch
            {
                ErrorAPI errorApi => (errorApi.codigoHttp, errorApi.Message),
                ArgumentException argumento => (400,
                    string.IsNullOrWhiteSpace(argumento.Message) ? MENSAJE_SOLICITUD_INVALIDA : argumento.Message),
                _ => (500, MENSAJE_ERROR_INTERNO),
            };
        }

        /// <summary>
        /// Completa con el envelope de error las respuestas sin cuerpo generadas por el enrutador (404, 405, ...).
        /// </summary>
        public static Task escribirCodigoEstado(StatusCodeContext contexto)
        {
            int codigo = contexto.HttpContext.Response.StatusCode;
            string mensaje = codigo switch
            {
                404 => MENSAJE_NO_ENCONTRADO,
                405 => MENSAJE_METODO_NO_PERMITIDO,
                415 => SoportePeticion.MENSAJE_CONTENIDO_NO_JSON,
                500 => MENSAJE_ERROR_INTERNO,
                _ => MENSAJE_SOLICITUD_INVALIDA,
            };
            return RespuestaAPI.escribir(contexto.HttpContext.Response, codigo, RespuestaAPI.error(codigo, mensaje));
        }
    }
}
