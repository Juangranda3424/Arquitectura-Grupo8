using Microsoft.AspNetCore.Mvc.Filters;
using WS_ConversionUnidades_Donet_GR8.servicio;

namespace WS_ConversionUnidades_Donet_GR8.seguridad
{
    /// <summary>
    /// Filtro de autorización que sustituye al ValidadorTokenInspector de WCF: se ejecuta antes de la
    /// acción y exige un token Bearer vigente. Los errores llegan como ErrorAPI 401 al MapeadorExcepciones.
    /// </summary>
    [AttributeUsage(AttributeTargets.Class | AttributeTargets.Method)]
    public sealed class RequiereTokenAttribute : Attribute, IAuthorizationFilter
    {
        private readonly TokenServicio tokens = new TokenServicio();

        public void OnAuthorization(AuthorizationFilterContext context)
        {
            ValidadorToken.exigirTokenValido(context.HttpContext.Request.Headers.Authorization.ToString(), tokens);
        }
    }
}
