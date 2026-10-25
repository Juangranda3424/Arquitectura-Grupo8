using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.ServiceModel.Channels;
using System.Text;
using WS_ConversionUnidades_Donet_GR8.controlador;
using WS_ConversionUnidades_Donet_GR8.modelo;
using WS_ConversionUnidades_Donet_GR8.seguridad;

namespace WS_ConversionUnidades_Donet_GR8.webservicio
{
    // NOTA: puede usar el comando "Rename" del menú "Refactorizar" para cambiar el nombre de clase "WSAutenticador" en el código, en svc y en el archivo de configuración a la vez.
    // NOTA: para iniciar el Cliente de prueba WCF para probar este servicio, seleccione WSAutenticador.svc o WSAutenticador.svc.cs en el Explorador de soluciones e inicie la depuración.
    [ValidadorTokenBehavior]
    public class WSAutenticador : IWSAutenticador
    {
        private readonly AutenticadorControlador autenticador = new AutenticadorControlador();

        public string login(Usuario usuario)
        {
            return autenticador.login(usuario);
        }

        public bool logout(string token)
        {
            // Si el token no viene en el cuerpo, lo extraemos del SOAP Header de forma nativa en WCF
            if (string.IsNullOrWhiteSpace(token))
            {
                token = ObtenerTokenDesdeHeader();
            }

            return autenticador.logout(token);
        }

        private string ObtenerTokenDesdeHeader()
        {
            try
            {
                OperationContext context = OperationContext.Current;
                if (context != null)
                {
                    MessageProperties props = context.IncomingMessageProperties;
                    MessageHeaders headers = context.IncomingMessageHeaders;

                    // Buscamos el header "Token" dentro de los headers de entrada
                    int headerIndex = context.IncomingMessageHeaders.FindHeader("Token", Constantes.NAMESPACE);
                    if (headerIndex < 0)
                    {
                        headerIndex = context.IncomingMessageHeaders.FindHeader("token", "");
                    }

                    if (headerIndex >= 0)
                    {
                        return context.IncomingMessageHeaders.GetHeader<string>(headerIndex);
                    }
                }
            }
            catch{
                // Si ocurre algun error al leer el header, retornamos null
            }

            return null;
        }
    }
}
