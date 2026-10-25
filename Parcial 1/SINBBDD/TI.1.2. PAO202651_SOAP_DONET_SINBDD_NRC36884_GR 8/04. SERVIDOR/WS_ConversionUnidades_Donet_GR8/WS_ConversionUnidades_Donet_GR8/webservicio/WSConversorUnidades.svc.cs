using System.ServiceModel;
using WS_ConversionUnidades_Donet_GR8.controlador;
using WS_ConversionUnidades_Donet_GR8.dto;
using WS_ConversionUnidades_Donet_GR8.seguridad;

namespace WS_ConversionUnidades_Donet_GR8.webservicio
{
    // ValidadorTokenBehavior intercepta las peticiones y valida el Token del Header automáticamente
    [ValidadorTokenBehavior]
    public class WSConversorUnidades : IWSConversorUnidades
    {
        private readonly ConversorUnidadesControlador controlador = new ConversorUnidadesControlador();

        public double convertirUnidades(ConversionDTO peticionConversion)
        {
            // Retorna el resultado evaluando si es nulo (devuelve 0.0 por seguridad)
            return controlador.conversor(peticionConversion) ?? 0.0;
        }
    }
}