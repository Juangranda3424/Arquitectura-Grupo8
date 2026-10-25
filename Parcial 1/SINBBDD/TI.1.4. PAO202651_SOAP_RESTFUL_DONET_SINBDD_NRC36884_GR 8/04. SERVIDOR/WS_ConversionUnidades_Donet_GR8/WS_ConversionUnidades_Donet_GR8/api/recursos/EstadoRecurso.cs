using System.Globalization;
using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Mvc;
using WS_ConversionUnidades_Donet_GR8.api.soporte;
using WS_ConversionUnidades_Donet_GR8.modelo;

namespace WS_ConversionUnidades_Donet_GR8.api.recursos
{
    /// <summary>
    /// GET /api/estado: sonda pública del servicio (no requiere token).
    /// </summary>
    [Route("api/estado")]
    public class EstadoRecurso : ControllerBase
    {
        [HttpGet]
        public IActionResult obtenerEstado()
        {
            var resultado = new JsonObject
            {
                ["servicio"] = Constantes.NOMBRE_SERVICIO,
                ["version"] = Constantes.VERSION_SERVICIO,
                ["protocolo"] = "REST/JSON",
                ["horaServidor"] = DateTime.UtcNow.ToString("yyyy-MM-dd'T'HH:mm:ss.fffffff'Z'", CultureInfo.InvariantCulture),
            };

            return RespuestaAPI.respuesta(200, RespuestaAPI.ok(200, "Servicio disponible.", resultado));
        }
    }
}
