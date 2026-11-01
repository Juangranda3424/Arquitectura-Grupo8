using System.Net;
using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;
using Microsoft.Extensions.Options;
using WS_EurekaBank_Donet_CLIWEB_GR8.Models;

namespace WS_EurekaBank_Donet_CLIWEB_GR8.Services
{
    /// <summary>
    /// Cliente REST (HttpClient + System.Text.Json) para la API de EurekaBank:
    ///   - POST /Login                  -> autenticación (usuario / contraseña)
    ///   - GET  /Cuenta/{codigo}        -> datos de la cuenta
    ///   - POST /Cuenta/deposito        -> depósito
    ///   - POST /Cuenta/retiro          -> retiro
    ///   - POST /Cuenta/transferencia   -> transferencia entre cuentas
    ///   - POST /Movimiento             -> historial de movimientos
    /// Todo error del servicio se traduce en RestClienteException con el mensaje del servidor.
    /// </summary>
    public class RestClienteServicio
    {
        // Las propiedades del contrato viajan en camelCase y se leen sin distinguir mayúsculas
        private static readonly JsonSerializerOptions Json = new()
        {
            PropertyNamingPolicy = JsonNamingPolicy.CamelCase,
            PropertyNameCaseInsensitive = true
        };

        private readonly HttpClient _http;
        private readonly ServicioRestOpciones _opciones;

        public RestClienteServicio(HttpClient http, IOptions<ServicioRestOpciones> opciones)
        {
            _http = http;
            _opciones = opciones.Value;
        }

        // ---------- Operaciones ----------

        public async Task<RespuestaLogin> LoginAsync(string usuario, string contrasena)
        {
            var (codigo, cuerpo) = await EnviarAsync(HttpMethod.Post, "Login",
                new { username = usuario, password = contrasena });

            // 401 (credenciales inválidas) y 400 (datos incompletos) también traen el detalle del servidor
            if (codigo is HttpStatusCode.OK or HttpStatusCode.Unauthorized or HttpStatusCode.BadRequest)
            {
                var respuesta = Leer<RespuestaLogin>(cuerpo);
                if (respuesta != null) return respuesta;

                throw new RestClienteException(Mensaje(cuerpo) ?? "Credenciales incorrectas");
            }

            throw new RestClienteException(Mensaje(cuerpo)
                ?? $"El servicio devolvió un error al iniciar sesión (HTTP {(int)codigo}).");
        }

        /// <summary>Datos de la cuenta, o null cuando el servicio responde 404 (cuenta inexistente).</summary>
        public async Task<CuentaModel?> ObtenerCuentaAsync(string cuenta)
        {
            var (codigo, cuerpo) = await EnviarAsync(HttpMethod.Get, "Cuenta/" + Uri.EscapeDataString(cuenta), null);

            if (codigo == HttpStatusCode.NotFound) return null;

            if (codigo != HttpStatusCode.OK)
            {
                throw new RestClienteException(Mensaje(cuerpo)
                    ?? $"El servicio devolvió un error al consultar la cuenta (HTTP {(int)codigo}).");
            }

            return Leer<CuentaModel>(cuerpo)
                ?? throw new RestClienteException("El servicio devolvió una cuenta que no se pudo interpretar.");
        }

        public Task<RespuestaOperacion> DepositarAsync(string cuenta, double monto) =>
            OperacionAsync("Cuenta/deposito", new { cuenta, monto });

        public Task<RespuestaOperacion> RetirarAsync(string cuenta, double monto) =>
            OperacionAsync("Cuenta/retiro", new { cuenta, monto });

        public Task<RespuestaOperacion> TransferirAsync(string cuenta, double monto, string cuentaDestino) =>
            OperacionAsync("Cuenta/transferencia", new { cuenta, monto, cuentaDestino });

        public async Task<IReadOnlyList<MovimientoModel>> ObtenerMovimientosAsync(string cuenta)
        {
            var (codigo, cuerpo) = await EnviarAsync(HttpMethod.Post, "Movimiento", new { numcuenta = cuenta });

            if (codigo != HttpStatusCode.OK)
            {
                throw new RestClienteException(Mensaje(cuerpo)
                    ?? $"El servicio devolvió un error al consultar los movimientos (HTTP {(int)codigo}).");
            }

            if (cuerpo.ValueKind != JsonValueKind.Array) return Array.Empty<MovimientoModel>();

            return Leer<List<MovimientoModel>>(cuerpo) ?? new List<MovimientoModel>();
        }

        // ---------- Infraestructura REST ----------

        /// <summary>
        /// Envía una operación de saldo. El servidor responde 400/500 con {"success": false, "mensaje": "..."}
        /// para los rechazos esperados (saldo insuficiente, cuentas iguales, datos inválidos), así que ese
        /// cuerpo se devuelve tal cual para que el controlador muestre el mensaje del servidor.
        /// </summary>
        private async Task<RespuestaOperacion> OperacionAsync(string ruta, object peticion)
        {
            var (codigo, cuerpo) = await EnviarAsync(HttpMethod.Post, ruta, peticion);

            var respuesta = Leer<RespuestaOperacion>(cuerpo);
            if (respuesta != null) return respuesta;

            throw new RestClienteException(Mensaje(cuerpo)
                ?? $"El servicio devolvió un error al registrar la operación (HTTP {(int)codigo}).");
        }

        private async Task<(HttpStatusCode Codigo, JsonElement Cuerpo)> EnviarAsync(
            HttpMethod metodo, string ruta, object? cuerpo)
        {
            var url = _opciones.UrlBase.TrimEnd('/') + "/" + ruta.TrimStart('/');

            using var peticion = new HttpRequestMessage(metodo, url);
            peticion.Headers.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
            if (cuerpo != null)
            {
                peticion.Content = new StringContent(
                    JsonSerializer.Serialize(cuerpo, Json), Encoding.UTF8, "application/json");
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
                var codigo = respuestaHttp.StatusCode;
                var texto = await respuestaHttp.Content.ReadAsStringAsync();

                if (string.IsNullOrWhiteSpace(texto)) return (codigo, default);

                try
                {
                    using var documento = JsonDocument.Parse(texto);
                    return (codigo, documento.RootElement.Clone());
                }
                catch (JsonException)
                {
                    throw new RestClienteException($"El servicio respondió algo inesperado (HTTP {(int)codigo}).");
                }
            }
        }

        private static T? Leer<T>(JsonElement cuerpo) where T : class =>
            cuerpo.ValueKind == JsonValueKind.Undefined ? null : cuerpo.Deserialize<T>(Json);

        private static string? Mensaje(JsonElement cuerpo) =>
            Texto(cuerpo, "mensaje") ?? Texto(cuerpo, "message");

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
