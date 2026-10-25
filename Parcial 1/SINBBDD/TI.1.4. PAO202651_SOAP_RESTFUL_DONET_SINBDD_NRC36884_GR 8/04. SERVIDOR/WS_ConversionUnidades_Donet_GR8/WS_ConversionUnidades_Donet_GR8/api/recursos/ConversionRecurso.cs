using System.Text.Json;
using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Mvc;
using WS_ConversionUnidades_Donet_GR8.api.soporte;
using WS_ConversionUnidades_Donet_GR8.dto;
using WS_ConversionUnidades_Donet_GR8.modelo;
using WS_ConversionUnidades_Donet_GR8.seguridad;
using WS_ConversionUnidades_Donet_GR8.servicio;

namespace WS_ConversionUnidades_Donet_GR8.api.recursos
{
    /// <summary>
    /// POST /api/conversiones/{magnitud}: ejecuta la conversión delegando en el núcleo de negocio de TI.1.2.
    /// La magnitud de la ruta no distingue mayúsculas y siempre gana sobre un tipoMagnitud del cuerpo.
    /// </summary>
    [Route("api/conversiones")]
    [RequiereToken]
    public class ConversionRecurso : ControllerBase
    {
        private static readonly HashSet<string> MAGNITUDES_VALIDAS = new HashSet<string>
        {
            CatalogoUnidades.LONGITUD, CatalogoUnidades.MASA, CatalogoUnidades.TEMPERATURA,
        };

        private readonly ConversorServicio conversor = new ConversorServicio();

        [HttpPost("{magnitud}")]
        public async Task<IActionResult> convertir(string magnitud)
        {
            SoportePeticion.exigirTipoJSON(Request.ContentType);

            JsonElement datos = SoportePeticion.leerObjetoJSON(await SoportePeticion.leerCuerpo(Request));

            string entrada = magnitud ?? "";
            string tipoMagnitud = entrada.Trim().ToUpperInvariant();
            if (!MAGNITUDES_VALIDAS.Contains(tipoMagnitud))
            {
                throw new ErrorAPI(404, $"La magnitud '{entrada}' no es válida. Debe ser LONGITUD, MASA o TEMPERATURA.");
            }

            var solicitud = new ConversionDTO(
                SoportePeticion.numero(datos, "valorOrigen"),
                null,
                SoportePeticion.texto(datos, "unidadOrigen"),
                SoportePeticion.texto(datos, "unidadDestino"),
                tipoMagnitud);

            ConversionDTO convertido = conversor.procesarConversion(solicitud);

            var resultado = new JsonObject
            {
                ["valorOrigen"] = convertido.ValorOrigen!.Value,
                ["valorResultado"] = convertido.ValorResultado!.Value,
                ["unidadOrigen"] = convertido.UnidadOrigen,
                ["unidadDestino"] = convertido.UnidadDestino,
                ["tipoMagnitud"] = tipoMagnitud,
            };

            return RespuestaAPI.respuesta(200, RespuestaAPI.ok(200, "Conversión realizada correctamente.", resultado));
        }
    }
}
