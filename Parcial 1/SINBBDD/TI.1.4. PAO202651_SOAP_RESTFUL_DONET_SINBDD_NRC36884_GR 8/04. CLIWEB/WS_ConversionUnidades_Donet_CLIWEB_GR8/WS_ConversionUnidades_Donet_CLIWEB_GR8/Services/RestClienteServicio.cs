using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;
using Microsoft.Extensions.Options;
using WS_ConversionUnidades_Donet_CLIWEB_GR8.Models;

namespace WS_ConversionUnidades_Donet_CLIWEB_GR8.Services
{
    /// <summary>
    /// Cliente REST (HttpClient + System.Text.Json) para la API de conversión de unidades:
    ///   - POST /auth/login, POST /auth/logout   -> sesión
    ///   - GET  /catalogo                        -> unidades por magnitud
    ///   - POST /conversiones/{magnitud}         -> conversión
    /// El token viaja en el encabezado "Authorization: Bearer &lt;token&gt;".
    /// Toda respuesta llega en el envelope {"estado","codigo","mensaje","resultado"}.
    /// </summary>
    public class RestClienteServicio
    {
        private const string EstadoError = "ERROR";

        private readonly HttpClient _http;
        private readonly ServicioRestOpciones _opciones;

        public RestClienteServicio(HttpClient http, IOptions<ServicioRestOpciones> opciones)
        {
            _http = http;
            _opciones = opciones.Value;
        }

        // ---------- Operaciones ----------

        public async Task<string> LoginAsync(string usuario, string contrasena)
        {
            JsonElement resultado;
            try
            {
                resultado = await EnviarAsync(HttpMethod.Post, "auth/login", null,
                    new { username = usuario, password = contrasena });
            }
            catch (RestClienteException ex) when (ex.AccesoDenegado)
            {
                // En el login un 401 significa credenciales incorrectas, no una sesión vencida
                throw new RestClienteException("Usuario o contraseña incorrectos.");
            }

            var token = Texto(resultado, "token");
            if (string.IsNullOrWhiteSpace(token))
            {
                throw new RestClienteException("Usuario o contraseña incorrectos.");
            }
            return token;
        }

        public async Task<bool> LogoutAsync(string token)
        {
            var resultado = await EnviarAsync(HttpMethod.Post, "auth/logout", token, null);
            return resultado.ValueKind == JsonValueKind.True;
        }

        /// <summary>
        /// Unidades por magnitud publicadas por el servicio, en el orden en que llegan
        /// (LONGITUD, MASA, TEMPERATURA). La etiqueta se arma como "Etiqueta (símbolo)".
        /// </summary>
        public async Task<IReadOnlyDictionary<string, Unidad[]>> ObtenerCatalogoAsync(string token)
        {
            var resultado = await EnviarAsync(HttpMethod.Get, "catalogo", token, null);
            if (resultado.ValueKind != JsonValueKind.Object)
            {
                throw new RestClienteException("El servicio devolvió un catálogo que no se pudo interpretar.");
            }

            var catalogo = new Dictionary<string, Unidad[]>();
            foreach (var magnitud in resultado.EnumerateObject())
            {
                if (magnitud.Value.ValueKind != JsonValueKind.Array) continue;

                var unidades = new List<Unidad>();
                foreach (var unidad in magnitud.Value.EnumerateArray())
                {
                    var clave = Texto(unidad, "clave");
                    if (string.IsNullOrWhiteSpace(clave)) continue;

                    var etiqueta = Texto(unidad, "etiqueta") ?? clave;
                    var simbolo = Texto(unidad, "simbolo");
                    unidades.Add(new Unidad(clave,
                        string.IsNullOrWhiteSpace(simbolo) ? etiqueta : $"{etiqueta} ({simbolo})"));
                }
                if (unidades.Count > 0) catalogo[magnitud.Name] = unidades.ToArray();
            }

            if (catalogo.Count == 0)
            {
                throw new RestClienteException("El servicio devolvió un catálogo vacío.");
            }
            return catalogo;
        }

        public async Task<double> ConvertirAsync(string token, string tipoMagnitud,
            string unidadOrigen, string unidadDestino, double valorOrigen)
        {
            var resultado = await EnviarAsync(HttpMethod.Post,
                "conversiones/" + Uri.EscapeDataString(tipoMagnitud), token,
                new { valorOrigen, unidadOrigen, unidadDestino });

            if (resultado.ValueKind != JsonValueKind.Object
                || !resultado.TryGetProperty("valorResultado", out var valor)
                || !valor.TryGetDouble(out var convertido))
            {
                throw new RestClienteException("El servicio devolvió una respuesta que no se pudo interpretar.");
            }
            return convertido;
        }

        // ---------- Infraestructura REST ----------

        /// <summary>
        /// Envía la petición y devuelve el campo "resultado" del envelope.
        /// Un estado "ERROR" o un código HTTP distinto de 2xx se traduce en RestClienteException con el mensaje del servidor.
        /// </summary>
        private async Task<JsonElement> EnviarAsync(HttpMethod metodo, string ruta, string? token, object? cuerpo)
        {
            var url = _opciones.UrlBase.TrimEnd('/') + "/" + ruta;

            using var peticion = new HttpRequestMessage(metodo, url);
            peticion.Headers.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
            if (token != null)
            {
                peticion.Headers.Authorization = new AuthenticationHeaderValue("Bearer", token);
            }
            if (cuerpo != null)
            {
                peticion.Content = new StringContent(JsonSerializer.Serialize(cuerpo), Encoding.UTF8, "application/json");
            }

            HttpResponseMessage respuestaHttp;
            try
            {
                respuestaHttp = await _http.SendAsync(peticion);
            }
            catch (Exception ex) when (ex is HttpRequestException || ex is TaskCanceledException)
            {
                throw new RestClienteException(
                    $"No se pudo conectar con el servicio web ({url}). Verifica que el servidor esté en ejecución.");
            }

            using (respuestaHttp)
            {
                var codigoHttp = (int)respuestaHttp.StatusCode;
                var texto = await respuestaHttp.Content.ReadAsStringAsync();

                JsonElement envelope;
                try
                {
                    using var doc = JsonDocument.Parse(texto);
                    envelope = doc.RootElement.Clone();
                }
                catch (JsonException)
                {
                    throw new RestClienteException($"El servicio respondió algo inesperado (HTTP {codigoHttp}).");
                }

                if (envelope.ValueKind != JsonValueKind.Object)
                {
                    throw new RestClienteException($"El servicio respondió algo inesperado (HTTP {codigoHttp}).");
                }

                var estado = Texto(envelope, "estado");
                if (!respuestaHttp.IsSuccessStatusCode || string.Equals(estado, EstadoError, StringComparison.OrdinalIgnoreCase))
                {
                    var mensaje = Texto(envelope, "mensaje");
                    var codigo = envelope.TryGetProperty("codigo", out var c) && c.TryGetInt32(out var n) ? n : codigoHttp;
                    throw new RestClienteException(
                        string.IsNullOrWhiteSpace(mensaje) ? $"El servicio devolvió un error (HTTP {codigo})." : mensaje,
                        codigo == 401);
                }

                return envelope.TryGetProperty("resultado", out var resultado) ? resultado : default;
            }
        }

        private static string? Texto(JsonElement objeto, string clave)
        {
            return objeto.ValueKind == JsonValueKind.Object
                   && objeto.TryGetProperty(clave, out var valor)
                   && valor.ValueKind == JsonValueKind.String
                ? valor.GetString()
                : null;
        }
    }
}
