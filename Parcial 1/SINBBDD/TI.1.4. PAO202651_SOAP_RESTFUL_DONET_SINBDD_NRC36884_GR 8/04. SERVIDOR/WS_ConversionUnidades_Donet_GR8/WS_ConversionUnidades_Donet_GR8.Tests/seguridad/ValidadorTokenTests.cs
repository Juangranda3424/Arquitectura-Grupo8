using WS_ConversionUnidades_Donet_GR8.api.soporte;
using WS_ConversionUnidades_Donet_GR8.modelo;
using WS_ConversionUnidades_Donet_GR8.seguridad;
using WS_ConversionUnidades_Donet_GR8.servicio;

namespace WS_ConversionUnidades_Donet_GR8.Tests.seguridad
{
    public class ValidadorTokenTests
    {
        [Theory]
        [InlineData("Bearer abc", "abc")]
        [InlineData("bearer abc", "abc")]
        [InlineData("  BEARER   abc  ", "abc")]
        public void extraerTokenAceptaElEsquemaBearerSinDistinguirMayusculas(string cabecera, string esperado)
        {
            Assert.Equal(esperado, ValidadorToken.extraerToken(cabecera));
        }

        [Theory]
        [InlineData(null)]
        [InlineData("")]
        [InlineData("Bearer")]
        [InlineData("Bearer    ")]
        [InlineData("Basic abc")]
        [InlineData("abc")]
        public void extraerTokenDevuelveNuloSinToken(string? cabecera)
        {
            Assert.Null(ValidadorToken.extraerToken(cabecera));
        }

        [Fact]
        public void exigirTokenValidoSinCabeceraLanza401()
        {
            var error = Assert.Throws<ErrorAPI>(() => ValidadorToken.exigirTokenValido(null, new TokenServicio()));
            Assert.Equal(401, error.codigoHttp);
            Assert.Equal(ValidadorToken.MENSAJE_FALTA_BEARER, error.Message);
        }

        [Fact]
        public void exigirTokenValidoConTokenDesconocidoLanza401()
        {
            var error = Assert.Throws<ErrorAPI>(() => ValidadorToken.exigirTokenValido("Bearer desconocido", new TokenServicio()));
            Assert.Equal(401, error.codigoHttp);
            Assert.Equal(ValidadorToken.MENSAJE_TOKEN_INVALIDO, error.Message);
        }

        [Fact]
        public void exigirTokenValidoConTokenVigenteNoLanza()
        {
            var tokens = new TokenServicio();
            string token = tokens.generarToken(new Usuario("MONSTER", "MONSTER9"));

            ValidadorToken.exigirTokenValido("Bearer " + token, tokens);
        }
    }
}
