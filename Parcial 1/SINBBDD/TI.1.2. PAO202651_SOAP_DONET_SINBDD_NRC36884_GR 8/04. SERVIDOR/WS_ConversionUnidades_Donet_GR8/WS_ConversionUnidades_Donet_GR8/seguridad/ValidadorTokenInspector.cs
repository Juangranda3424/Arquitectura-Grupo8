using System;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.ServiceModel.Dispatcher;
using System.Xml;
using WS_ConversionUnidades_Donet_GR8.modelo;
using WS_ConversionUnidades_Donet_GR8.servicio;

namespace WS_ConversionUnidades_Donet_GR8.seguridad
{
    public class ValidadorTokenInspector : IDispatchMessageInspector
    {
        private readonly TokenServicio validadorToken = new TokenServicio();

        public object AfterReceiveRequest(ref Message request, IClientChannel channel, InstanceContext instanceContext)
        {
            // Omitir validación si es una petición de Login (revisando la acción SOAP)
            string action = request.Headers.Action;
            if (!string.IsNullOrEmpty(action) && (action.EndsWith("/login") || action.EndsWith("/autenticadorLogin")))
            {
                return null;
            }

            try
            {
                // Buscar la cabecera <Token> en el SOAP Header
                int headerIndex = request.Headers.FindHeader("Token", Constantes.NAMESPACE);
                if (headerIndex < 0)
                {
                    // Intentar buscar sin namespace estricto si fuera necesario
                    headerIndex = request.Headers.FindHeader("Token", "");
                }

                if (headerIndex < 0)
                {
                    throw new FaultException("Acceso denegado: Falta el encabezado SOAP (Token) en la petición.");
                }

                // Extraer el valor del token
                string token = request.Headers.GetHeader<string>(headerIndex);

                if (string.IsNullOrWhiteSpace(token))
                {
                    throw new FaultException("Acceso denegado: No se encontró la etiqueta <Token> o estaba vacía en la cabecera SOAP.");
                }

                // Validar el token en el servicio
                if (!validadorToken.validarToken(token))
                {
                    throw new FaultException("Acceso denegado: Token inválido o sesión expirada.");
                }
            }
            catch (FaultException)
            {
                throw; // Re-lanzar los errores SOAP configurados
            }
            catch (Exception ex)
            {
                throw new FaultException($"Error procesando autenticación: {ex.Message}");
            }

            return null;
        }

        public void BeforeSendReply(ref Message reply, object correlationState)
        {
            // No se requiere acción al enviar la respuesta de salida
        }
    }
}