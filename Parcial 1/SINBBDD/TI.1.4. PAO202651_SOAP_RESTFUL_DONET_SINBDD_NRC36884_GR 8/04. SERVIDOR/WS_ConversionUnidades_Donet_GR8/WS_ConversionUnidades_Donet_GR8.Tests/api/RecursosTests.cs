using System.Text.Json;
using static WS_ConversionUnidades_Donet_GR8.Tests.api.SoporteAPI;

namespace WS_ConversionUnidades_Donet_GR8.Tests.api
{
    public class EstadoRecursoTests : IClassFixture<FabricaAPI>
    {
        private readonly HttpClient cliente;

        public EstadoRecursoTests(FabricaAPI fabrica) => cliente = fabrica.CreateClient();

        [Fact]
        public async Task estadoResponde200ConElEnvelopeEsperado()
        {
            JsonElement resultado = await asertaOk(await cliente.GetAsync("/api/estado"), "Servicio disponible.");

            Assert.Equal("WS_ConversionUnidades_Donet_GR8", resultado.GetProperty("servicio").GetString());
            Assert.Equal("REST/JSON", resultado.GetProperty("protocolo").GetString());
            Assert.False(string.IsNullOrEmpty(resultado.GetProperty("version").GetString()));
            Assert.True(DateTimeOffset.TryParse(resultado.GetProperty("horaServidor").GetString(), out _));
        }

        [Fact]
        public async Task tambienRespondeBajoElContextoDelServicio()
        {
            await asertaOk(await cliente.GetAsync("/WS_ConversionUnidades_Donet_GR8/api/estado"), "Servicio disponible.");
        }
    }

    public class AutenticadorRecursoTests : IClassFixture<FabricaAPI>
    {
        private readonly HttpClient cliente;

        public AutenticadorRecursoTests(FabricaAPI fabrica) => cliente = fabrica.CreateClient();

        [Fact]
        public async Task loginValidoDevuelve200ConTokenUsuarioYVencimiento()
        {
            var respuesta = await cliente.PostAsync("/api/auth/login",
                json("{\"username\":\"MONSTER\",\"password\":\"MONSTER9\"}"));

            JsonElement resultado = await asertaOk(respuesta, "Autenticación exitosa.");
            Assert.EndsWith("MONSTER", resultado.GetProperty("token").GetString());
            Assert.Equal("MONSTER", resultado.GetProperty("usuario").GetString());
            Assert.Equal(JsonValueKind.String, resultado.GetProperty("expiraEn").ValueKind);
        }

        [Fact]
        public async Task loginConCharsetEnElContentTypeEsAceptado()
        {
            var respuesta = await cliente.PostAsync("/api/auth/login",
                json("{\"username\":\"MONSTER\",\"password\":\"MONSTER9\"}", "application/json; charset=utf-8"));

            await asertaOk(respuesta, "Autenticación exitosa.");
        }

        [Fact]
        public async Task loginConContentTypeDistintoDeJsonDevuelve415()
        {
            var respuesta = await cliente.PostAsync("/api/auth/login",
                json("{\"username\":\"MONSTER\",\"password\":\"MONSTER9\"}", "text/plain"));

            await asertaError(respuesta, 415, "El contenido de la petición debe ser JSON.");
        }

        [Theory]
        [InlineData("no es json")]
        [InlineData("[1,2]")]
        [InlineData("")]
        public async Task loginConJSONInvalidoDevuelve400(string cuerpo)
        {
            await asertaError(await cliente.PostAsync("/api/auth/login", json(cuerpo)), 400,
                "El cuerpo de la petición debe ser JSON válido.");
        }

        [Theory]
        [InlineData("{}")]
        [InlineData("{\"username\":\"MONSTER\"}")]
        [InlineData("{\"username\":\" \",\"password\":\"MONSTER9\"}")]
        [InlineData("{\"username\":\"MONSTER\",\"password\":123}")]
        public async Task loginConCamposFaltantesOVaciosDevuelve400(string cuerpo)
        {
            await asertaError(await cliente.PostAsync("/api/auth/login", json(cuerpo)), 400,
                "El nombre de usuario y la contraseña son obligatorios.");
        }

        [Fact]
        public async Task loginConCredencialesIncorrectasDevuelve401ConElMensajeDeParidad()
        {
            var respuesta = await cliente.PostAsync("/api/auth/login",
                json("{\"username\":\"MONSTER\",\"password\":\"incorrecta\"}"));

            await asertaError(respuesta, 401, "Usuario invalido");
        }

        [Fact]
        public async Task logoutSinCabeceraAuthorizationDevuelve401()
        {
            await asertaError(await cliente.PostAsync("/api/auth/logout", null), 401,
                "Acceso denegado: falta el encabezado Authorization con el esquema Bearer.");
        }

        [Fact]
        public async Task logoutConTokenDesconocidoDevuelve401()
        {
            await asertaError(await cliente.SendAsync(peticion(HttpMethod.Post, "/api/auth/logout", "desconocido")), 401,
                "Acceso denegado: Token inválido o sesión expirada.");
        }

        [Fact]
        public async Task logoutTrasLoginDevuelve200ConResultadoTrueYInvalidaElToken()
        {
            string token = await iniciarSesion(cliente);

            JsonElement resultado = await asertaOk(
                await cliente.SendAsync(peticion(HttpMethod.Post, "/api/auth/logout", token)), "Sesión finalizada.");
            Assert.Equal(JsonValueKind.True, resultado.ValueKind);

            await asertaError(await cliente.SendAsync(peticion(HttpMethod.Get, "/api/catalogo", token)), 401);
        }

        [Fact]
        public async Task elEsquemaBearerNoDistingueMayusculas()
        {
            string token = await iniciarSesion(cliente);
            var mensaje = new HttpRequestMessage(HttpMethod.Post, "/api/auth/logout");
            mensaje.Headers.TryAddWithoutValidation("Authorization", "bEaReR " + token);

            await asertaOk(await cliente.SendAsync(mensaje), "Sesión finalizada.");
        }
    }

    public class CatalogoRecursoTests : IClassFixture<FabricaAPI>
    {
        private readonly HttpClient cliente;

        public CatalogoRecursoTests(FabricaAPI fabrica) => cliente = fabrica.CreateClient();

        [Fact]
        public async Task sinTokenDevuelve401()
        {
            await asertaError(await cliente.GetAsync("/api/catalogo"), 401);
        }

        [Fact]
        public async Task conTokenValidoDevuelve200ConLasTresMagnitudesY5UnidadesCadaUna()
        {
            string token = await iniciarSesion(cliente);

            JsonElement resultado = await asertaOk(
                await cliente.SendAsync(peticion(HttpMethod.Get, "/api/catalogo", token)), "Catálogo de unidades.");

            Assert.Equal(new[] { "LONGITUD", "MASA", "TEMPERATURA" },
                resultado.EnumerateObject().Select(p => p.Name));
            foreach (JsonProperty magnitud in resultado.EnumerateObject())
            {
                Assert.Equal(5, magnitud.Value.GetArrayLength());
            }

            JsonElement metro = resultado.GetProperty("LONGITUD")[1];
            Assert.Equal("METRO", metro.GetProperty("clave").GetString());
            Assert.Equal("Metro", metro.GetProperty("etiqueta").GetString());
            Assert.Equal("m", metro.GetProperty("simbolo").GetString());
            Assert.Equal("°Ré", resultado.GetProperty("TEMPERATURA")[4].GetProperty("simbolo").GetString());
        }
    }

    public class ConversionRecursoTests : IClassFixture<FabricaAPI>
    {
        private readonly HttpClient cliente;

        public ConversionRecursoTests(FabricaAPI fabrica) => cliente = fabrica.CreateClient();

        private async Task<HttpResponseMessage> convertir(string magnitud, string cuerpo, string tipo = "application/json")
        {
            string token = await iniciarSesion(cliente);
            return await cliente.SendAsync(peticion(HttpMethod.Post, "/api/conversiones/" + magnitud, token, json(cuerpo, tipo)));
        }

        [Fact]
        public async Task convierteLongitud()
        {
            JsonElement resultado = await asertaOk(
                await convertir("longitud", "{\"valorOrigen\":1,\"unidadOrigen\":\"METRO\",\"unidadDestino\":\"CENTIMETRO\"}"),
                "Conversión realizada correctamente.");

            Assert.Equal(1.0, resultado.GetProperty("valorOrigen").GetDouble());
            Assert.Equal(100.0, resultado.GetProperty("valorResultado").GetDouble(), 9);
            Assert.Equal("METRO", resultado.GetProperty("unidadOrigen").GetString());
            Assert.Equal("CENTIMETRO", resultado.GetProperty("unidadDestino").GetString());
            Assert.Equal("LONGITUD", resultado.GetProperty("tipoMagnitud").GetString());
        }

        [Fact]
        public async Task convierteMasa()
        {
            JsonElement resultado = await asertaOk(
                await convertir("masa", "{\"valorOrigen\":1,\"unidadOrigen\":\"LIBRA\",\"unidadDestino\":\"GRAMO\"}"),
                "Conversión realizada correctamente.");

            Assert.Equal(453.592, resultado.GetProperty("valorResultado").GetDouble(), 9);
        }

        [Fact]
        public async Task convierteTemperatura()
        {
            JsonElement resultado = await asertaOk(
                await convertir("temperatura", "{\"valorOrigen\":100,\"unidadOrigen\":\"CELSIUS\",\"unidadDestino\":\"FAHRENHEIT\"}"),
                "Conversión realizada correctamente.");

            Assert.Equal(212.0, resultado.GetProperty("valorResultado").GetDouble(), 9);
        }

        [Fact]
        public async Task laMagnitudDeLaRutaNoDistingueMayusculasYElTipoMagnitudDelCuerpoSeIgnora()
        {
            JsonElement resultado = await asertaOk(
                await convertir("MaSa", "{\"valorOrigen\":1,\"unidadOrigen\":\"kilogramo\",\"unidadDestino\":\"gramo\",\"tipoMagnitud\":\"LONGITUD\"}"),
                "Conversión realizada correctamente.");

            Assert.Equal("MASA", resultado.GetProperty("tipoMagnitud").GetString());
            Assert.Equal(1000.0, resultado.GetProperty("valorResultado").GetDouble(), 9);
        }

        [Fact]
        public async Task magnitudDesconocidaDevuelve404()
        {
            await asertaError(
                await convertir("volumen", "{\"valorOrigen\":1,\"unidadOrigen\":\"LITRO\",\"unidadDestino\":\"GALON\"}"),
                404, "La magnitud 'volumen' no es válida. Debe ser LONGITUD, MASA o TEMPERATURA.");
        }

        [Fact]
        public async Task magnitudVaciaDevuelve404()
        {
            await asertaError(await convertir("", "{}"), 404);
        }

        [Fact]
        public async Task unidadInvalidaDevuelve400ConElMensajeDeParidad()
        {
            await asertaError(
                await convertir("longitud", "{\"valorOrigen\":1,\"unidadOrigen\":\"GRAMO\",\"unidadDestino\":\"METRO\"}"),
                400, "La unidad 'GRAMO' no es válida para la magnitud 'LONGITUD'.");
        }

        [Fact]
        public async Task temperaturaBajoElCeroAbsolutoDevuelve400()
        {
            await asertaError(
                await convertir("temperatura", "{\"valorOrigen\":-1,\"unidadOrigen\":\"KELVIN\",\"unidadDestino\":\"CELSIUS\"}"),
                400);
        }

        [Theory]
        [InlineData("longitud", "METRO", "PIE")]
        [InlineData("masa", "GRAMO", "ONZA")]
        public async Task valorNegativoDevuelve400(string magnitud, string origen, string destino)
        {
            await asertaError(
                await convertir(magnitud, $"{{\"valorOrigen\":-5,\"unidadOrigen\":\"{origen}\",\"unidadDestino\":\"{destino}\"}}"),
                400, $"El valor para la magnitud '{magnitud.ToUpperInvariant()}' no puede ser negativo.");
        }

        [Theory]
        [InlineData("{\"unidadOrigen\":\"METRO\",\"unidadDestino\":\"PIE\"}")]
        [InlineData("{\"valorOrigen\":\"10\",\"unidadOrigen\":\"METRO\",\"unidadDestino\":\"PIE\"}")]
        public async Task valorOrigenAusenteONoNumericoDevuelve400(string cuerpo)
        {
            await asertaError(await convertir("longitud", cuerpo), 400,
                "El valor numérico de origen es obligatorio y debe ser un número válido.");
        }

        [Fact]
        public async Task unidadFaltanteDevuelve400()
        {
            await asertaError(await convertir("longitud", "{\"valorOrigen\":1,\"unidadOrigen\":\"METRO\"}"), 400,
                "La unidad de destino es obligatoria.");
        }

        [Fact]
        public async Task cuerpoNoJSONDevuelve400()
        {
            await asertaError(await convertir("longitud", "valorOrigen=1"), 400,
                "El cuerpo de la petición debe ser JSON válido.");
        }

        [Fact]
        public async Task contentTypeDistintoDeJsonDevuelve415()
        {
            await asertaError(await convertir("longitud", "{}", "application/xml"), 415,
                "El contenido de la petición debe ser JSON.");
        }

        [Fact]
        public async Task sinTokenDevuelve401()
        {
            await asertaError(await cliente.PostAsync("/api/conversiones/longitud",
                json("{\"valorOrigen\":1,\"unidadOrigen\":\"METRO\",\"unidadDestino\":\"PIE\"}")), 401);
        }
    }

    public class MapeadorExcepcionesTests : IClassFixture<FabricaAPI>
    {
        private readonly HttpClient cliente;

        public MapeadorExcepcionesTests(FabricaAPI fabrica) => cliente = fabrica.CreateClient();

        [Fact]
        public async Task recursoDesconocidoDevuelve404()
        {
            await asertaError(await cliente.GetAsync("/api/no-existe"), 404, "Recurso no encontrado.");
        }

        [Fact]
        public async Task metodoNoPermitidoDevuelve405()
        {
            await asertaError(await cliente.DeleteAsync("/api/estado"), 405, "Método HTTP no permitido para este recurso.");
        }
    }
}
