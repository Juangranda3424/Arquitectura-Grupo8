using WS_ConversionUnidades_Donet_GR8.dto;
using WS_ConversionUnidades_Donet_GR8.modelo;
using WS_ConversionUnidades_Donet_GR8.servicio;

namespace WS_ConversionUnidades_Donet_GR8.Tests.servicio
{
    public class TokenServicioTests
    {
        private readonly TokenServicio tokens = new TokenServicio();

        [Fact]
        public void generarTokenUsaElFormatoGuidMasUsuario()
        {
            string token = tokens.generarToken(new Usuario("MONSTER", "MONSTER9"));

            Assert.EndsWith("MONSTER", token);
            Assert.True(Guid.TryParse(token[..36], out _));
            Assert.True(tokens.validarToken(token));
        }

        [Fact]
        public void elRegistroEsCompartidoEntreInstancias()
        {
            string token = tokens.generarToken(new Usuario("MONSTER", "MONSTER9"));

            Assert.True(new TokenServicio().validarToken(token));
        }

        [Fact]
        public void eliminarTokenLoInvalida()
        {
            string token = tokens.generarToken(new Usuario("MONSTER", "MONSTER9"));

            Assert.True(tokens.eliminarToken(token));
            Assert.False(tokens.validarToken(token));
            Assert.False(tokens.eliminarToken(token));
        }

        [Theory]
        [InlineData(null)]
        [InlineData("")]
        [InlineData("   ")]
        public void tokensVaciosNoSonValidos(string? token)
        {
            Assert.False(tokens.validarToken(token));
            Assert.False(tokens.eliminarToken(token));
        }
    }

    public class AutenticadorServicioTests
    {
        private readonly AutenticadorServicio autenticador = new AutenticadorServicio();

        [Fact]
        public void loginCorrectoDevuelveToken()
        {
            string token = autenticador.autenticadorLogin(new Usuario("MONSTER", "MONSTER9"));

            Assert.NotEqual(AutenticadorServicio.USUARIO_INVALIDO, token);
            Assert.True(new TokenServicio().validarToken(token));
        }

        [Theory]
        [InlineData("MONSTER", "otra")]
        [InlineData("monster", "MONSTER9")]
        [InlineData("", "MONSTER9")]
        [InlineData("MONSTER", "")]
        [InlineData(null, "MONSTER9")]
        public void loginIncorrectoDevuelveUsuarioInvalido(string? usuario, string? contrasena)
        {
            Assert.Equal("Usuario invalido", autenticador.autenticadorLogin(new Usuario(usuario!, contrasena!)));
        }

        [Fact]
        public void loginNuloDevuelveUsuarioInvalido()
        {
            Assert.Equal("Usuario invalido", autenticador.autenticadorLogin(null));
        }

        [Fact]
        public void logoutInvalidaElToken()
        {
            string token = autenticador.autenticadorLogin(new Usuario("MONSTER", "MONSTER9"));

            Assert.True(autenticador.autenticadorLogout(token));
            Assert.False(autenticador.autenticadorLogout(token));
        }
    }

    public class ConversorServicioTests
    {
        private readonly ConversorServicio conversor = new ConversorServicio();

        [Fact]
        public void convierteLongitudSinDistinguirMayusculas()
        {
            var resultado = conversor.procesarConversion(new ConversionDTO(100.0, null, " centimetro ", "Metro", "longitud"));

            Assert.Equal(1.0, resultado.ValorResultado!.Value, 9);
        }

        [Fact]
        public void convierteMasa()
        {
            var resultado = conversor.procesarConversion(new ConversionDTO(2.0, null, "KILOGRAMO", "GRAMO", "MASA"));

            Assert.Equal(2000.0, resultado.ValorResultado!.Value, 9);
        }

        [Fact]
        public void convierteTemperaturaNegativaSobreElCeroAbsoluto()
        {
            var resultado = conversor.procesarConversion(new ConversionDTO(-40.0, null, "CELSIUS", "FAHRENHEIT", "TEMPERATURA"));

            Assert.Equal(-40.0, resultado.ValorResultado!.Value, 9);
        }

        [Fact]
        public void magnitudDesconocidaLanza()
        {
            var error = Assert.Throws<ArgumentException>(() =>
                conversor.procesarConversion(new ConversionDTO(1.0, null, "LITRO", "GALON", "VOLUMEN")));
            Assert.Equal("La magnitud 'VOLUMEN' no es válida. Debe ser LONGITUD, MASA o TEMPERATURA.", error.Message);
        }

        [Fact]
        public void longitudNegativaLanza()
        {
            Assert.Throws<ArgumentException>(() =>
                conversor.procesarConversion(new ConversionDTO(-1.0, null, "METRO", "PIE", "LONGITUD")));
        }

        [Fact]
        public void unidadDeOtraMagnitudLanza()
        {
            var error = Assert.Throws<ArgumentException>(() =>
                conversor.procesarConversion(new ConversionDTO(1.0, null, "GRAMO", "PIE", "LONGITUD")));
            Assert.Equal("La unidad 'GRAMO' no es válida para la magnitud 'LONGITUD'.", error.Message);
        }

        [Fact]
        public void temperaturaBajoCeroAbsolutoLanza()
        {
            Assert.Throws<ArgumentException>(() =>
                conversor.procesarConversion(new ConversionDTO(-300.0, null, "CELSIUS", "KELVIN", "TEMPERATURA")));
        }
    }
}
