package ec.edu.monster.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class CatalogoUnidadesTest {

    @Test
    void elCatalogoTieneTresMagnitudesEnOrden() {
        Map<String, List<CatalogoUnidades.UnidadInfo>> catalogo = CatalogoUnidades.catalogo();

        assertEquals(3, catalogo.size());
        assertEquals(List.of("LONGITUD", "MASA", "TEMPERATURA"), List.copyOf(catalogo.keySet()));
    }

    @Test
    void cadaMagnitudTieneCincoUnidades() {
        assertEquals(5, CatalogoUnidades.unidadesDe("LONGITUD").size());
        assertEquals(5, CatalogoUnidades.unidadesDe("MASA").size());
        assertEquals(5, CatalogoUnidades.unidadesDe("TEMPERATURA").size());
    }

    @Test
    void noHayClavesDuplicadasDentroDeUnaMagnitud() {
        CatalogoUnidades.catalogo().forEach((magnitud, unidades) -> {
            Set<String> claves = unidades.stream()
                    .map(CatalogoUnidades.UnidadInfo::clave)
                    .collect(Collectors.toSet());
            assertEquals(unidades.size(), claves.size(), "Claves duplicadas en " + magnitud);
        });
    }

    @Test
    void noHaySimbolosDuplicadosDentroDeUnaMagnitud() {
        CatalogoUnidades.catalogo().forEach((magnitud, unidades) -> {
            Set<String> simbolos = unidades.stream()
                    .map(CatalogoUnidades.UnidadInfo::simbolo)
                    .collect(Collectors.toSet());
            assertEquals(unidades.size(), simbolos.size(), "Símbolos duplicados en " + magnitud);
        });
    }

    @Test
    void lasClavesDelCatalogoCoincidenConLosEnumsDeConversion() {
        Set<String> clavesLongitud = claves(CatalogoUnidades.unidadesDe("LONGITUD"));
        Set<String> clavesMasa = claves(CatalogoUnidades.unidadesDe("MASA"));
        Set<String> clavesTemperatura = claves(CatalogoUnidades.unidadesDe("TEMPERATURA"));

        assertEquals(5, clavesLongitud.size());
        assertEquals(Set.of("CENTIMETRO", "METRO", "PIE", "YARDA", "MILLA"), clavesLongitud);
        assertEquals(Set.of("GRAMO", "KILOGRAMO", "ONZA", "LIBRA", "QUINTAL"), clavesMasa);
        assertEquals(Set.of("CELSIUS", "FAHRENHEIT", "KELVIN", "NEWTON", "REAUMUR"), clavesTemperatura);
    }

    @Test
    void lasEtiquetasYSimbolosSonLosEsperados() {
        assertEquals(Optional.of(new CatalogoUnidades.UnidadInfo("MILLA", "Milla", "mi")),
                CatalogoUnidades.buscar("LONGITUD", "MILLA"));
        assertEquals(Optional.of(new CatalogoUnidades.UnidadInfo("QUINTAL", "Quintal", "qq")),
                CatalogoUnidades.buscar("MASA", "QUINTAL"));
        assertEquals(Optional.of(new CatalogoUnidades.UnidadInfo("CELSIUS", "Celsius", "°C")),
                CatalogoUnidades.buscar("TEMPERATURA", "CELSIUS"));
        assertEquals(Optional.of(new CatalogoUnidades.UnidadInfo("REAUMUR", "Réaumur", "°Ré")),
                CatalogoUnidades.buscar("TEMPERATURA", "REAUMUR"));
    }

    @Test
    void laMagnitudSeNormalizaAntesDeBuscar() {
        assertEquals(5, CatalogoUnidades.unidadesDe("  longitud ").size());
        assertTrue(CatalogoUnidades.buscar("longitud", "metro").isPresent());
        assertTrue(CatalogoUnidades.buscar("MASA", "gramo").isPresent());
    }

    @Test
    void unaMagnitudDesconocidaDevuelveListaVacia() {
        assertTrue(CatalogoUnidades.unidadesDe("VOLUMEN").isEmpty());
        assertTrue(CatalogoUnidades.unidadesDe(null).isEmpty());
        assertTrue(CatalogoUnidades.buscar("VOLUMEN", "METRO").isEmpty());
        assertTrue(CatalogoUnidades.buscar("LONGITUD", "VOLUMEN").isEmpty());
    }

    @Test
    void elCatalogoEsInmutable() {
        Map<String, List<CatalogoUnidades.UnidadInfo>> catalogo = CatalogoUnidades.catalogo();

        assertThrows(UnsupportedOperationException.class, catalogo::clear);
        assertThrows(UnsupportedOperationException.class, () -> catalogo.put("VOLUMEN", List.of()));
        assertThrows(UnsupportedOperationException.class,
                () -> catalogo.get("LONGITUD").add(new CatalogoUnidades.UnidadInfo("X", "X", "x")));
        assertFalse(catalogo.isEmpty());
    }

    private static Set<String> claves(List<CatalogoUnidades.UnidadInfo> unidades) {
        return unidades.stream()
                .map(CatalogoUnidades.UnidadInfo::clave)
                .collect(Collectors.toSet());
    }
}
