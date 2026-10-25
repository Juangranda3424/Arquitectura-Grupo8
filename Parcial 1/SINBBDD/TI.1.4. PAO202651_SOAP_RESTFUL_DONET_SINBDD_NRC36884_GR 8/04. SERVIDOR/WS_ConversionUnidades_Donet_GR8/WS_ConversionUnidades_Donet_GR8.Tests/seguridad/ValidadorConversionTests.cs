using WS_ConversionUnidades_Donet_GR8.dto;
using WS_ConversionUnidades_Donet_GR8.modelo;
using WS_ConversionUnidades_Donet_GR8.seguridad;

namespace WS_ConversionUnidades_Donet_GR8.Tests.seguridad
{
    public class ValidadorConversionTests
    {
        private readonly ValidadorConversion validador = new ValidadorConversion();

        private static ConversionDTO dtoValido() => new ConversionDTO(1.0, null, "METRO", "PIE", "LONGITUD");

        [Fact]
        public void estructuraValidaNoLanza()
        {
            validador.validarEstructuraEntrada(dtoValido());
        }

        [Fact]
        public void dtoNuloLanza()
        {
            var error = Assert.Throws<ArgumentException>(() => validador.validarEstructuraEntrada(null));
            Assert.Equal("Los datos de la conversión no pueden ser nulos.", error.Message);
        }

        [Fact]
        public void valorOrigenNuloLanza()
        {
            var dto = dtoValido();
            dto.ValorOrigen = null;
            var error = Assert.Throws<ArgumentException>(() => validador.validarEstructuraEntrada(dto));
            Assert.Equal("El valor numérico de origen es obligatorio y debe ser un número válido.", error.Message);
        }

        [Theory]
        [InlineData(double.NaN)]
        [InlineData(double.PositiveInfinity)]
        public void valorNoFinitoLanza(double valor)
        {
            var dto = dtoValido();
            dto.ValorOrigen = valor;
            var error = Assert.Throws<ArgumentException>(() => validador.validarEstructuraEntrada(dto));
            Assert.Equal("El valor numérico ingresado no es válido.", error.Message);
        }

        [Fact]
        public void camposDeTextoVaciosLanzanConSuMensaje()
        {
            var sinMagnitud = dtoValido(); sinMagnitud.TipoMagnitud = " ";
            var sinOrigen = dtoValido(); sinOrigen.UnidadOrigen = null;
            var sinDestino = dtoValido(); sinDestino.UnidadDestino = "";

            Assert.Equal("El tipo de magnitud es obligatorio.",
                Assert.Throws<ArgumentException>(() => validador.validarEstructuraEntrada(sinMagnitud)).Message);
            Assert.Equal("La unidad de origen es obligatoria.",
                Assert.Throws<ArgumentException>(() => validador.validarEstructuraEntrada(sinOrigen)).Message);
            Assert.Equal("La unidad de destino es obligatoria.",
                Assert.Throws<ArgumentException>(() => validador.validarEstructuraEntrada(sinDestino)).Message);
        }

        [Fact]
        public void valorNegativoLanza()
        {
            var error = Assert.Throws<ArgumentException>(() => validador.validarValorNoNegativo(-1, "MASA"));
            Assert.Equal("El valor para la magnitud 'MASA' no puede ser negativo.", error.Message);
            validador.validarValorNoNegativo(0, "MASA");
        }

        [Fact]
        public void temperaturaBajoCeroAbsolutoLanza()
        {
            Assert.Throws<ArgumentException>(() => validador.validarTemperaturaMinima(-1, UnidadTemperatura.KELVIN));
            Assert.Throws<ArgumentException>(() => validador.validarTemperaturaMinima(-500, UnidadTemperatura.FAHRENHEIT));
            validador.validarTemperaturaMinima(-273.15, UnidadTemperatura.CELSIUS);
        }

        [Fact]
        public void buscarEnumValidoNoDistingueMayusculas()
        {
            Assert.Equal(UnidadMasa.LIBRA, validador.buscarEnumValido<UnidadMasa>("libra", "MASA"));
        }

        [Theory]
        [InlineData("GALON")]
        [InlineData("1")]
        [InlineData("99")]
        public void buscarEnumValidoRechazaNombresInexistentesONumericos(string nombre)
        {
            var error = Assert.Throws<ArgumentException>(() => validador.buscarEnumValido<UnidadLongitud>(nombre, "LONGITUD"));
            Assert.Equal($"La unidad '{nombre}' no es válida para la magnitud 'LONGITUD'.", error.Message);
        }
    }
}
