using System.Text.Json;
using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging.Abstractions;
using WS_ConversionUnidades_Donet_GR8.api.soporte;

namespace WS_ConversionUnidades_Donet_GR8.Tests.api
{
    public class RespuestaAPITests
    {
        [Fact]
        public void okIncluyeLasCuatroClaves()
        {
            using var json = JsonDocument.Parse(RespuestaAPI.ok(200, "listo", new JsonObject { ["x"] = 1 }));
            JsonElement raiz = json.RootElement;

            Assert.Equal("OK", raiz.GetProperty("estado").GetString());
            Assert.Equal(200, raiz.GetProperty("codigo").GetInt32());
            Assert.Equal("listo", raiz.GetProperty("mensaje").GetString());
            Assert.Equal(1, raiz.GetProperty("resultado").GetProperty("x").GetInt32());
        }

        [Fact]
        public void errorTieneResultadoNuloExplicito()
        {
            using var json = JsonDocument.Parse(RespuestaAPI.error(400, null));
            JsonElement raiz = json.RootElement;

            Assert.Equal("ERROR", raiz.GetProperty("estado").GetString());
            Assert.Equal("", raiz.GetProperty("mensaje").GetString());
            Assert.Equal(JsonValueKind.Null, raiz.GetProperty("resultado").ValueKind);
        }
    }

    public class SoportePeticionTests
    {
        [Theory]
        [InlineData(null)]
        [InlineData("")]
        [InlineData("application/json")]
        [InlineData("APPLICATION/JSON; charset=utf-8")]
        public void exigirTipoJSONAceptaJsonOAusente(string? tipo)
        {
            SoportePeticion.exigirTipoJSON(tipo);
        }

        [Fact]
        public void exigirTipoJSONRechazaOtrosTipos()
        {
            var error = Assert.Throws<ErrorAPI>(() => SoportePeticion.exigirTipoJSON("text/xml"));
            Assert.Equal(415, error.codigoHttp);
        }

        [Fact]
        public void textoYNumeroSoloAceptanSuTipoJSON()
        {
            JsonElement datos = SoportePeticion.leerObjetoJSON("{\"a\":\"x\",\"b\":2.5,\"c\":true}");

            Assert.Equal("x", SoportePeticion.texto(datos, "a"));
            Assert.Null(SoportePeticion.texto(datos, "b"));
            Assert.Equal(2.5, SoportePeticion.numero(datos, "b"));
            Assert.Null(SoportePeticion.numero(datos, "a"));
            Assert.Null(SoportePeticion.numero(datos, "falta"));
        }
    }

    public class MapeadorExcepcionesUnitariosTests
    {
        private static async Task<(int codigo, JsonElement envelope)> ejecutar(Exception error)
        {
            var contexto = new DefaultHttpContext();
            contexto.Response.Body = new MemoryStream();
            var mapeador = new MapeadorExcepciones(_ => throw error, NullLogger<MapeadorExcepciones>.Instance);

            await mapeador.InvokeAsync(contexto);

            contexto.Response.Body.Position = 0;
            using var json = await JsonDocument.ParseAsync(contexto.Response.Body);
            return (contexto.Response.StatusCode, json.RootElement.Clone());
        }

        [Fact]
        public async Task errorAPIConservaSuCodigoHTTPYMensaje()
        {
            var (codigo, envelope) = await ejecutar(new ErrorAPI(404, "no encontrado"));

            Assert.Equal(404, codigo);
            Assert.Equal("no encontrado", envelope.GetProperty("mensaje").GetString());
        }

        [Fact]
        public async Task argumentoInvalidoDevuelve400ConElMensajeDeLaExcepcion()
        {
            var (codigo, envelope) = await ejecutar(new ArgumentException("dato malo"));

            Assert.Equal(400, codigo);
            Assert.Equal("dato malo", envelope.GetProperty("mensaje").GetString());
        }

        [Fact]
        public async Task cualquierOtraExcepcionDevuelve500SinFiltrarDetalles()
        {
            var (codigo, envelope) = await ejecutar(new InvalidOperationException("detalle interno"));

            Assert.Equal(500, codigo);
            Assert.Equal("Error interno del servidor.", envelope.GetProperty("mensaje").GetString());
            Assert.Equal("ERROR", envelope.GetProperty("estado").GetString());
        }
    }
}
