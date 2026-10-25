using WS_ConversionUnidades_Donet_GR8.modelo;

namespace WS_ConversionUnidades_Donet_GR8.Tests.modelo
{
    public class ModelosTests
    {
        private const int Precision = 6;

        [Theory]
        [InlineData(UnidadLongitud.METRO, 1.0, UnidadLongitud.CENTIMETRO, 100.0)]
        [InlineData(UnidadLongitud.PIE, 1.0, UnidadLongitud.METRO, 0.3048)]
        [InlineData(UnidadLongitud.YARDA, 1.0, UnidadLongitud.PIE, 3.0)]
        [InlineData(UnidadLongitud.MILLA, 1.0, UnidadLongitud.METRO, 1609.34)]
        [InlineData(UnidadLongitud.CENTIMETRO, 250.0, UnidadLongitud.CENTIMETRO, 250.0)]
        public void convierteLongitud(UnidadLongitud origen, double valor, UnidadLongitud destino, double esperado)
        {
            Assert.Equal(esperado, origen.convertirA(valor, destino), Precision);
        }

        [Theory]
        [InlineData(UnidadMasa.KILOGRAMO, 1.0, UnidadMasa.GRAMO, 1000.0)]
        [InlineData(UnidadMasa.LIBRA, 1.0, UnidadMasa.GRAMO, 453.592)]
        [InlineData(UnidadMasa.ONZA, 16.0, UnidadMasa.LIBRA, 1.0)]
        [InlineData(UnidadMasa.QUINTAL, 1.0, UnidadMasa.KILOGRAMO, 100.0)]
        public void convierteMasa(UnidadMasa origen, double valor, UnidadMasa destino, double esperado)
        {
            Assert.Equal(esperado, origen.convertirA(valor, destino), Precision);
        }

        [Theory]
        [InlineData(UnidadTemperatura.CELSIUS, 100.0, UnidadTemperatura.FAHRENHEIT, 212.0)]
        [InlineData(UnidadTemperatura.FAHRENHEIT, 32.0, UnidadTemperatura.CELSIUS, 0.0)]
        [InlineData(UnidadTemperatura.CELSIUS, 0.0, UnidadTemperatura.KELVIN, 273.15)]
        [InlineData(UnidadTemperatura.CELSIUS, 100.0, UnidadTemperatura.NEWTON, 33.0)]
        [InlineData(UnidadTemperatura.REAUMUR, 80.0, UnidadTemperatura.CELSIUS, 100.0)]
        [InlineData(UnidadTemperatura.KELVIN, 0.0, UnidadTemperatura.CELSIUS, -273.15)]
        public void convierteTemperatura(UnidadTemperatura origen, double valor, UnidadTemperatura destino, double esperado)
        {
            Assert.Equal(esperado, origen.convertirA(valor, destino), Precision);
        }

        [Fact]
        public void usuarioConservaSusCredenciales()
        {
            var usuario = new Usuario("MONSTER", "MONSTER9");

            Assert.Equal("MONSTER", usuario.usuario);
            Assert.Equal("MONSTER9", usuario.contrasena);
        }
    }
}
