using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Mvc;
using WS_ConversionUnidades_Donet_GR8.api.soporte;
using WS_ConversionUnidades_Donet_GR8.modelo;
using WS_ConversionUnidades_Donet_GR8.seguridad;

namespace WS_ConversionUnidades_Donet_GR8.api.recursos
{
    /// <summary>
    /// GET /api/catalogo: unidades soportadas por magnitud (requiere token vigente).
    /// </summary>
    [Route("api/catalogo")]
    [RequiereToken]
    public class CatalogoRecurso : ControllerBase
    {
        [HttpGet]
        public IActionResult listarCatalogo()
        {
            var resultado = new JsonObject();
            foreach (var magnitud in CatalogoUnidades.catalogo())
            {
                var unidades = new JsonArray();
                foreach (UnidadInfo unidad in magnitud.Value)
                {
                    unidades.Add(new JsonObject
                    {
                        ["clave"] = unidad.clave,
                        ["etiqueta"] = unidad.etiqueta,
                        ["simbolo"] = unidad.simbolo,
                    });
                }
                resultado[magnitud.Key] = unidades;
            }

            return RespuestaAPI.respuesta(200, RespuestaAPI.ok(200, "Catálogo de unidades.", resultado));
        }
    }
}
