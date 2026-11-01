using System;
using System.Threading;
using System.Web;

namespace WS_EurekaBank_Donet_GR8
{
    /// <summary>
    /// Habilita CORS para el cliente web (CLIWEB), que se ejecuta en otro origen
    /// (por ejemplo http://127.0.0.1:5501) y consume el servicio SOAP con XHR/fetch.
    /// </summary>
    public class Global : HttpApplication
    {
        protected void Application_BeginRequest(object sender, EventArgs e)
        {
            HttpContext contexto = HttpContext.Current;
            if (contexto == null)
            {
                return;
            }

            HttpResponse respuesta = contexto.Response;
            respuesta.AddHeader("Access-Control-Allow-Origin", "*");
            respuesta.AddHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
            respuesta.AddHeader("Access-Control-Allow-Headers", "Content-Type, SOAPAction, Accept, Origin, X-Requested-With");
            respuesta.AddHeader("Access-Control-Max-Age", "86400");

            // Preflight del navegador: no se envia el sobre SOAP, solo se confirman los permisos.
            if (string.Equals(contexto.Request.HttpMethod, "OPTIONS", StringComparison.OrdinalIgnoreCase))
            {
                try
                {
                    respuesta.StatusCode = 200;
                    respuesta.Flush();
                    respuesta.End();
                }
                catch (ThreadAbortException)
                {
                    // Response.End() aborta el hilo de la peticion preflight; es el comportamiento esperado.
                }
            }
        }

        protected void Application_Start(object sender, EventArgs e)
        {
        }
    }
}
