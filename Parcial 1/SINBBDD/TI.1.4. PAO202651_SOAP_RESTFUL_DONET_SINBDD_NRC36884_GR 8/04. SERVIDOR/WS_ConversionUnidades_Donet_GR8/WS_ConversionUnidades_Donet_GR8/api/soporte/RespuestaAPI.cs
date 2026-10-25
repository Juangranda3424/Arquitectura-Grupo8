using System.Text.Encodings.Web;
using System.Text.Json;
using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Mvc;

namespace WS_ConversionUnidades_Donet_GR8.api.soporte
{
    /// <summary>
    /// Constructor del envelope JSON único de la API REST:
    /// {"estado":"OK|ERROR","codigo":&lt;int&gt;,"mensaje":"&lt;texto&gt;","resultado":&lt;json|null&gt;}.
    /// Las cuatro claves están siempre presentes; en los errores "resultado" es null explícito.
    /// </summary>
    public static class RespuestaAPI
    {
        public const string ESTADO_OK = "OK";
        public const string ESTADO_ERROR = "ERROR";

        /// <summary>Tipo de contenido de todas las respuestas del servicio.</summary>
        public const string TIPO_JSON = "application/json; charset=utf-8";

        // Escritura sin escapar caracteres no ASCII (tildes, símbolo de grado).
        private static readonly JsonSerializerOptions OPCIONES = new JsonSerializerOptions
        {
            Encoder = JavaScriptEncoder.UnsafeRelaxedJsonEscaping,
        };

        public static string ok(int codigo, string? mensaje, JsonNode? resultado)
        {
            return construir(ESTADO_OK, codigo, mensaje, resultado);
        }

        public static string error(int codigo, string? mensaje)
        {
            return construir(ESTADO_ERROR, codigo, mensaje, null);
        }

        /// <summary>
        /// Respuesta MVC completa (código HTTP + envelope ya serializado).
        /// </summary>
        public static ContentResult respuesta(int codigoHttp, string json)
        {
            return new ContentResult
            {
                StatusCode = codigoHttp,
                Content = json,
                ContentType = TIPO_JSON,
            };
        }

        /// <summary>
        /// Escribe el envelope directamente en la respuesta HTTP (uso desde middleware).
        /// </summary>
        public static async Task escribir(HttpResponse respuestaHttp, int codigoHttp, string json)
        {
            respuestaHttp.StatusCode = codigoHttp;
            respuestaHttp.ContentType = TIPO_JSON;
            await respuestaHttp.WriteAsync(json);
        }

        private static string construir(string estado, int codigo, string? mensaje, JsonNode? resultado)
        {
            var envelope = new JsonObject
            {
                ["estado"] = estado,
                ["codigo"] = codigo,
                ["mensaje"] = mensaje ?? "",
                ["resultado"] = resultado,
            };
            return envelope.ToJsonString(OPCIONES);
        }
    }
}
