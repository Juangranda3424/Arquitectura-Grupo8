using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;
using Microsoft.AspNetCore.Mvc.Testing;

namespace WS_ConversionUnidades_Donet_GR8.Tests.api
{
    /// <summary>
    /// Utilidades compartidas por las pruebas de la capa REST (cliente en memoria y aserciones del envelope).
    /// </summary>
    public static class SoporteAPI
    {
        public static StringContent json(string cuerpo, string tipo = "application/json")
        {
            var contenido = new StringContent(cuerpo, Encoding.UTF8);
            contenido.Headers.ContentType = MediaTypeHeaderValue.Parse(tipo);
            return contenido;
        }

        public static async Task<JsonElement> leerEnvelope(HttpResponseMessage respuesta)
        {
            Assert.Equal("application/json", respuesta.Content.Headers.ContentType?.MediaType);
            string texto = await respuesta.Content.ReadAsStringAsync();
            using var documento = JsonDocument.Parse(texto);
            return documento.RootElement.Clone();
        }

        public static async Task<JsonElement> asertaOk(HttpResponseMessage respuesta, string mensaje)
        {
            JsonElement envelope = await leerEnvelope(respuesta);
            Assert.Equal(200, (int)respuesta.StatusCode);
            Assert.Equal("OK", envelope.GetProperty("estado").GetString());
            Assert.Equal(200, envelope.GetProperty("codigo").GetInt32());
            Assert.Equal(mensaje, envelope.GetProperty("mensaje").GetString());
            return envelope.GetProperty("resultado");
        }

        public static async Task asertaError(HttpResponseMessage respuesta, int codigo, string? mensaje = null)
        {
            JsonElement envelope = await leerEnvelope(respuesta);
            Assert.Equal(codigo, (int)respuesta.StatusCode);
            Assert.Equal("ERROR", envelope.GetProperty("estado").GetString());
            Assert.Equal(codigo, envelope.GetProperty("codigo").GetInt32());
            Assert.Equal(JsonValueKind.Null, envelope.GetProperty("resultado").ValueKind);
            if (mensaje != null)
            {
                Assert.Equal(mensaje, envelope.GetProperty("mensaje").GetString());
            }
        }

        public static async Task<string> iniciarSesion(HttpClient cliente)
        {
            var respuesta = await cliente.PostAsync("/api/auth/login",
                json("{\"username\":\"MONSTER\",\"password\":\"MONSTER9\"}"));
            JsonElement resultado = await asertaOk(respuesta, "Autenticación exitosa.");
            return resultado.GetProperty("token").GetString()!;
        }

        public static HttpRequestMessage peticion(HttpMethod metodo, string ruta, string? token, HttpContent? cuerpo = null)
        {
            var mensaje = new HttpRequestMessage(metodo, ruta) { Content = cuerpo };
            if (token != null)
            {
                mensaje.Headers.TryAddWithoutValidation("Authorization", "Bearer " + token);
            }
            return mensaje;
        }
    }

    /// <summary>
    /// Fábrica del servidor en memoria compartida por cada clase de pruebas de la capa REST.
    /// </summary>
    public class FabricaAPI : WebApplicationFactory<Program>
    {
    }
}
