using System.Text;
using System.Text.Json;

namespace WS_ConversionUnidades_Donet_GR8.api.soporte
{
    /// <summary>
    /// Utilidades compartidas por los recursos para leer la petición HTTP: validación del Content-Type
    /// y parseo defensivo del cuerpo JSON (sin model binding, para controlar los códigos 400/415).
    /// </summary>
    public static class SoportePeticion
    {
        public const string MENSAJE_CONTENIDO_NO_JSON = "El contenido de la petición debe ser JSON.";
        public const string MENSAJE_CUERPO_NO_JSON = "El cuerpo de la petición debe ser JSON válido.";

        private const string TIPO_JSON = "application/json";

        /// <summary>
        /// Exige que el Content-Type sea application/json, ignorando parámetros (charset) y mayúsculas.
        /// Una cabecera ausente o vacía se acepta. Lanza ErrorAPI 415 en otro caso.
        /// </summary>
        public static void exigirTipoJSON(string? tipoContenido)
        {
            if (string.IsNullOrWhiteSpace(tipoContenido))
            {
                return;
            }
            string tipo = tipoContenido.Split(';', 2)[0].Trim();
            if (!string.Equals(TIPO_JSON, tipo, StringComparison.OrdinalIgnoreCase))
            {
                throw new ErrorAPI(415, MENSAJE_CONTENIDO_NO_JSON);
            }
        }

        /// <summary>
        /// Lee el cuerpo completo de la petición como texto UTF-8.
        /// </summary>
        public static async Task<string> leerCuerpo(HttpRequest peticion)
        {
            using var lector = new StreamReader(peticion.Body, Encoding.UTF8);
            return await lector.ReadToEndAsync();
        }

        /// <summary>
        /// Parsea el cuerpo como objeto JSON. Lanza ErrorAPI 400 si está vacío, no es JSON o no es un objeto.
        /// </summary>
        public static JsonElement leerObjetoJSON(string? cuerpo)
        {
            if (string.IsNullOrWhiteSpace(cuerpo))
            {
                throw new ErrorAPI(400, MENSAJE_CUERPO_NO_JSON);
            }
            try
            {
                using JsonDocument documento = JsonDocument.Parse(cuerpo);
                if (documento.RootElement.ValueKind != JsonValueKind.Object)
                {
                    throw new ErrorAPI(400, MENSAJE_CUERPO_NO_JSON);
                }
                return documento.RootElement.Clone();
            }
            catch (JsonException)
            {
                throw new ErrorAPI(400, MENSAJE_CUERPO_NO_JSON);
            }
        }

        /// <summary>
        /// Texto de la clave, o null si falta o no es una cadena JSON.
        /// </summary>
        public static string? texto(JsonElement objeto, string clave)
        {
            return objeto.TryGetProperty(clave, out JsonElement valor) && valor.ValueKind == JsonValueKind.String
                ? valor.GetString()
                : null;
        }

        /// <summary>
        /// Número de la clave, o null si falta o no es un número JSON. Un número fuera del rango
        /// de double se devuelve como NaN para que el validador lo rechace como valor no válido.
        /// </summary>
        public static double? numero(JsonElement objeto, string clave)
        {
            if (!objeto.TryGetProperty(clave, out JsonElement valor) || valor.ValueKind != JsonValueKind.Number)
            {
                return null;
            }
            return valor.TryGetDouble(out double numero) ? numero : double.NaN;
        }
    }
}
