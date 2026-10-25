using WS_ConversionUnidades_Donet_GR8.modelo;

namespace WS_ConversionUnidades_Donet_GR8.Tests.modelo
{
    public class CatalogoUnidadesTests
    {
        [Fact]
        public void elCatalogoTieneLasTresMagnitudesEnOrden()
        {
            Assert.Equal(
                new[] { CatalogoUnidades.LONGITUD, CatalogoUnidades.MASA, CatalogoUnidades.TEMPERATURA },
                CatalogoUnidades.catalogo().Select(entrada => entrada.Key));
        }

        [Fact]
        public void lasClavesCoincidenConLosEnumsDeConversion()
        {
            Assert.Equal(Enum.GetNames<UnidadLongitud>(), CatalogoUnidades.unidadesDe("LONGITUD").Select(u => u.clave));
            Assert.Equal(Enum.GetNames<UnidadMasa>(), CatalogoUnidades.unidadesDe("MASA").Select(u => u.clave));
            Assert.Equal(Enum.GetNames<UnidadTemperatura>(), CatalogoUnidades.unidadesDe("TEMPERATURA").Select(u => u.clave));
        }

        [Fact]
        public void unidadesDeNoDistingueMayusculasNiEspacios()
        {
            Assert.Equal(5, CatalogoUnidades.unidadesDe("  masa ").Count);
        }

        [Fact]
        public void magnitudDesconocidaDevuelveListaVacia()
        {
            Assert.Empty(CatalogoUnidades.unidadesDe("VOLUMEN"));
            Assert.Empty(CatalogoUnidades.unidadesDe(null));
        }

        [Fact]
        public void buscarEncuentraLaUnidadSinDistinguirMayusculas()
        {
            var unidad = CatalogoUnidades.buscar("temperatura", " celsius ");

            Assert.NotNull(unidad);
            Assert.Equal("CELSIUS", unidad!.clave);
            Assert.Equal("°C", unidad.simbolo);
        }

        [Fact]
        public void buscarDevuelveNuloSiNoExiste()
        {
            Assert.Null(CatalogoUnidades.buscar("LONGITUD", "GRAMO"));
            Assert.Null(CatalogoUnidades.buscar("LONGITUD", null));
        }
    }
}
