using WS_ConversionUnidades_Donet_GR8.api.soporte;
using WS_ConversionUnidades_Donet_GR8.servicio;

namespace WS_ConversionUnidades_Donet_GR8.seguridad
{
    /// <summary>
    /// Extrae y valida el token de sesión del encabezado HTTP "Authorization: Bearer &lt;token&gt;".
    /// Reemplaza a la cabecera SOAP &lt;Token&gt; de TI.1.2; el esquema Bearer no distingue mayúsculas.
    /// </summary>
    public static class ValidadorToken
    {
        public const string MENSAJE_FALTA_BEARER =
            "Acceso denegado: falta el encabezado Authorization con el esquema Bearer.";

        public const string MENSAJE_TOKEN_INVALIDO =
            "Acceso denegado: Token inválido o sesión expirada.";

        private const string ESQUEMA_BEARER = "Bearer ";

        /// <summary>
        /// Devuelve el token que sigue al esquema Bearer, o null si el encabezado no trae un token.
        /// </summary>
        public static string? extraerToken(string? authorizationHeader)
        {
            if (authorizationHeader == null)
            {
                return null;
            }
            string cabecera = authorizationHeader.Trim();
            if (cabecera.Length <= ESQUEMA_BEARER.Length
                || !cabecera.StartsWith(ESQUEMA_BEARER, StringComparison.OrdinalIgnoreCase))
            {
                return null;
            }
            string token = cabecera.Substring(ESQUEMA_BEARER.Length).Trim();
            return token.Length == 0 ? null : token;
        }

        /// <summary>
        /// Exige un token vigente; lanza ErrorAPI 401 si falta el encabezado o el token es desconocido.
        /// </summary>
        public static void exigirTokenValido(string? authorizationHeader, TokenServicio tokens)
        {
            string? token = extraerToken(authorizationHeader);
            if (token == null)
            {
                throw new ErrorAPI(401, MENSAJE_FALTA_BEARER);
            }
            if (!tokens.validarToken(token))
            {
                throw new ErrorAPI(401, MENSAJE_TOKEN_INVALIDO);
            }
        }
    }
}
