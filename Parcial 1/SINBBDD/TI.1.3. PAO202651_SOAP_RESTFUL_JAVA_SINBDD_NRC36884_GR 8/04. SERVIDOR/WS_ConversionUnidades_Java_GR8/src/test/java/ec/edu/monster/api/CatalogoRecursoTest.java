package ec.edu.monster.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ec.edu.monster.api.recurso.CatalogoRecurso;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

class CatalogoRecursoTest {

    private static final List<String> MAGNITUDES = List.of("LONGITUD", "MASA", "TEMPERATURA");
    private static final Set<String> CLAVES_UNIDAD = Set.of("clave", "etiqueta", "simbolo");

    private final CatalogoRecurso recurso = new CatalogoRecurso();

    private Response catalogo(String autorizacion) {
        return SoporteAPI.invocar(() -> recurso.listarCatalogo(autorizacion));
    }

    @Test
    void sinTokenDevuelve401() {
        SoporteAPI.asertaError(catalogo(null), 401, SoporteAPI.MENSAJE_FALTA_BEARER);
        SoporteAPI.asertaError(catalogo("Bearer no-existe"), 401, SoporteAPI.MENSAJE_TOKEN_INVALIDO);
        SoporteAPI.asertaError(catalogo(SoporteAPI.bearer(SoporteAPI.tokenVencido())),
                401, SoporteAPI.MENSAJE_TOKEN_INVALIDO);
    }

    @Test
    void conTokenValidoDevuelve200ConLasTresMagnitudesY5UnidadesCadaUna() {
        Response respuesta = catalogo(SoporteAPI.bearer(SoporteAPI.tokenValido()));

        SoporteAPI.asertaEnvelope(respuesta, 200, "OK", "Catálogo de unidades.");

        JsonObject resultado = SoporteAPI.resultado(respuesta);
        assertEquals(3, resultado.size());
        assertEquals(MAGNITUDES, new ArrayList<>(resultado.keySet()), "orden de las magnitudes");

        for (String magnitud : MAGNITUDES) {
            JsonArray unidades = resultado.getJsonArray(magnitud);
            assertEquals(5, unidades.size(), "unidades de " + magnitud);
            for (int i = 0; i < unidades.size(); i++) {
                JsonObject unidad = unidades.getJsonObject(i);
                assertEquals(CLAVES_UNIDAD, unidad.keySet(), "claves de la unidad");
                assertFalse(unidad.getString("clave").isBlank());
                assertFalse(unidad.getString("etiqueta").isBlank());
                assertFalse(unidad.getString("simbolo").isBlank());
            }
        }
    }

    @Test
    void lasUnidadesDeLongitudConservanClaveEtiquetaYSimbolo() {
        JsonArray longitud = SoporteAPI.resultado(catalogo(SoporteAPI.bearer(SoporteAPI.tokenValido())))
                .getJsonArray("LONGITUD");

        assertEquals(List.of("CENTIMETRO", "METRO", "PIE", "YARDA", "MILLA"), claves(longitud));
        assertEquals("Centímetro", longitud.getJsonObject(0).getString("etiqueta"));
        assertEquals("cm", longitud.getJsonObject(0).getString("simbolo"));
        assertEquals("m", longitud.getJsonObject(1).getString("simbolo"));
        assertEquals("mi", longitud.getJsonObject(4).getString("simbolo"));
    }

    @Test
    void lasUnidadesDeTemperaturaConservanLosSimbolosDeParidad() {
        JsonArray temperatura = SoporteAPI.resultado(catalogo(SoporteAPI.bearer(SoporteAPI.tokenValido())))
                .getJsonArray("TEMPERATURA");

        assertEquals(List.of("CELSIUS", "FAHRENHEIT", "KELVIN", "NEWTON", "REAUMUR"), claves(temperatura));
        assertEquals("°C", temperatura.getJsonObject(0).getString("simbolo"));
        assertEquals("K", temperatura.getJsonObject(2).getString("simbolo"));
        assertTrue(temperatura.getJsonObject(4).getString("etiqueta").startsWith("Réaumur"));
    }

    private static List<String> claves(JsonArray unidades) {
        List<String> claves = new ArrayList<>();
        for (int i = 0; i < unidades.size(); i++) {
            claves.add(unidades.getJsonObject(i).getString("clave"));
        }
        return claves;
    }
}
