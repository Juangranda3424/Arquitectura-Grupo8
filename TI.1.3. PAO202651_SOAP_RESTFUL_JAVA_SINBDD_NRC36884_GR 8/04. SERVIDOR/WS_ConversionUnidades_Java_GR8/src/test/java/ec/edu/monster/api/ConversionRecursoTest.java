package ec.edu.monster.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ec.edu.monster.api.recurso.ConversionRecurso;

import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

class ConversionRecursoTest {

    private static final String MENSAJE_JSON_INVALIDO = "El cuerpo de la petición debe ser JSON válido.";
    private static final String MENSAJE_OK = "Conversión realizada correctamente.";
    private static final String MENSAJE_CONTENIDO_NO_JSON = "El contenido de la petición debe ser JSON.";

    private final ConversionRecurso recurso = new ConversionRecurso();

    private Response convertir(String magnitud, String cuerpo, String autorizacion, String tipoContenido) {
        return SoporteAPI.invocar(() -> recurso.convertir(magnitud, cuerpo, autorizacion, tipoContenido));
    }

    private Response convertirConToken(String magnitud, String cuerpo) {
        return convertir(magnitud, cuerpo, SoporteAPI.bearer(SoporteAPI.tokenValido()), SoporteAPI.JSON);
    }

    private static String cuerpo(double valor, String origen, String destino) {
        return "{\"valorOrigen\":" + valor + ",\"unidadOrigen\":\"" + origen
                + "\",\"unidadDestino\":\"" + destino + "\"}";
    }

    private void asertaConversion(Response respuesta, double valor, double esperado,
                                  String origen, String destino, String magnitud) {
        SoporteAPI.asertaEnvelope(respuesta, 200, "OK", MENSAJE_OK);

        JsonObject resultado = SoporteAPI.resultado(respuesta);
        assertEquals(5, resultado.size());
        assertEquals(valor, resultado.getJsonNumber("valorOrigen").doubleValue(), 1.0e-9);
        assertEquals(esperado, resultado.getJsonNumber("valorResultado").doubleValue(), 1.0e-9);
        assertEquals(origen, resultado.getString("unidadOrigen"));
        assertEquals(destino, resultado.getString("unidadDestino"));
        assertEquals(magnitud, resultado.getString("tipoMagnitud"));
    }

    @Test
    void convierteLongitud() {
        asertaConversion(convertirConToken("LONGITUD", cuerpo(100, "CENTIMETRO", "METRO")),
                100, 1.0, "CENTIMETRO", "METRO", "LONGITUD");
    }

    @Test
    void convierteMasa() {
        asertaConversion(convertirConToken("MASA", cuerpo(1, "KILOGRAMO", "GRAMO")),
                1, 1000.0, "KILOGRAMO", "GRAMO", "MASA");
    }

    @Test
    void convierteTemperatura() {
        asertaConversion(convertirConToken("TEMPERATURA", cuerpo(100, "CELSIUS", "FAHRENHEIT")),
                100, 212.0, "CELSIUS", "FAHRENHEIT", "TEMPERATURA");
    }

    @Test
    void laMagnitudDeLaRutaNoDistingueMayusculasYSeNormalizaEnLaRespuesta() {
        asertaConversion(convertirConToken("longitud", cuerpo(100, "CENTIMETRO", "METRO")),
                100, 1.0, "CENTIMETRO", "METRO", "LONGITUD");
        asertaConversion(convertirConToken("  masa  ", cuerpo(1, "KILOGRAMO", "GRAMO")),
                1, 1000.0, "KILOGRAMO", "GRAMO", "MASA");
    }

    @Test
    void elTipoMagnitudDelCuerpoSeIgnora() {
        String conTipoEnElCuerpo = "{\"valorOrigen\":100,\"unidadOrigen\":\"CENTIMETRO\","
                + "\"unidadDestino\":\"METRO\",\"tipoMagnitud\":\"MASA\",\"valorResultado\":999}";

        asertaConversion(convertirConToken("LONGITUD", conTipoEnElCuerpo),
                100, 1.0, "CENTIMETRO", "METRO", "LONGITUD");
    }

    @Test
    void magnitudDesconocidaDevuelve404() {
        SoporteAPI.asertaError(convertirConToken("peso", cuerpo(1, "GRAMO", "LIBRA")), 404,
                "La magnitud 'peso' no es válida. Debe ser LONGITUD, MASA o TEMPERATURA.");
    }

    @Test
    void magnitudVaciaDevuelve404() {
        SoporteAPI.asertaError(convertirConToken("   ", cuerpo(1, "GRAMO", "LIBRA")), 404,
                "La magnitud '   ' no es válida. Debe ser LONGITUD, MASA o TEMPERATURA.");
    }

    @Test
    void unidadInvalidaDevuelve400ConElMensajeDeParidad() {
        SoporteAPI.asertaError(convertirConToken("LONGITUD", cuerpo(1, "METRO", "PULGADA")), 400,
                "La unidad 'PULGADA' no es válida para la magnitud 'LONGITUD'.");
        SoporteAPI.asertaError(convertirConToken("MASA", cuerpo(1, "GRAMO", "TONELADA")), 400,
                "La unidad 'TONELADA' no es válida para la magnitud 'MASA'.");
    }

    @Test
    void temperaturaBajoElCeroAbsolutoDevuelve400() {
        SoporteAPI.asertaError(convertirConToken("TEMPERATURA", cuerpo(-300, "CELSIUS", "KELVIN")), 400,
                "La temperatura ingresada (-300.0 CELSIUS) está por debajo del cero absoluto (-273.15 °C).");
    }

    @Test
    void longitudNegativaDevuelve400() {
        SoporteAPI.asertaError(convertirConToken("LONGITUD", cuerpo(-5, "CENTIMETRO", "METRO")), 400,
                "El valor para la magnitud 'LONGITUD' no puede ser negativo.");
    }

    @Test
    void masaNegativaDevuelve400() {
        SoporteAPI.asertaError(convertirConToken("MASA", cuerpo(-5, "GRAMO", "LIBRA")), 400,
                "El valor para la magnitud 'MASA' no puede ser negativo.");
    }

    @Test
    void valorOrigenAusenteONoNumericoDevuelve400() {
        SoporteAPI.asertaError(convertirConToken("LONGITUD",
                "{\"unidadOrigen\":\"CENTIMETRO\",\"unidadDestino\":\"METRO\"}"), 400,
                "El valor numérico de origen es obligatorio y debe ser un número válido.");
        SoporteAPI.asertaError(convertirConToken("LONGITUD",
                "{\"valorOrigen\":\"cien\",\"unidadOrigen\":\"CENTIMETRO\",\"unidadDestino\":\"METRO\"}"), 400,
                "El valor numérico de origen es obligatorio y debe ser un número válido.");
    }

    @Test
    void unidadFaltanteDevuelve400() {
        SoporteAPI.asertaError(convertirConToken("LONGITUD",
                "{\"valorOrigen\":1,\"unidadDestino\":\"METRO\"}"), 400,
                "La unidad de origen es obligatoria.");
        SoporteAPI.asertaError(convertirConToken("LONGITUD",
                "{\"valorOrigen\":1,\"unidadOrigen\":\"METRO\"}"), 400,
                "La unidad de destino es obligatoria.");
    }

    @Test
    void cuerpoNoJSONDevuelve400() {
        SoporteAPI.asertaError(convertirConToken("LONGITUD", "{no-es-json"), 400, MENSAJE_JSON_INVALIDO);
        SoporteAPI.asertaError(convertirConToken("LONGITUD", ""), 400, MENSAJE_JSON_INVALIDO);
    }

    @Test
    void contentTypesDistintosDeJsonDevuelven415() {
        String token = SoporteAPI.bearer(SoporteAPI.tokenValido());
        String cuerpo = cuerpo(100, "CENTIMETRO", "METRO");

        SoporteAPI.asertaError(convertir("LONGITUD", cuerpo, token, "text/plain"), 415, MENSAJE_CONTENIDO_NO_JSON);
        SoporteAPI.asertaError(convertir("LONGITUD", cuerpo, token, "text/xml;charset=utf-8"), 415,
                MENSAJE_CONTENIDO_NO_JSON);
    }

    @Test
    void sinTokenDevuelve401() {
        String cuerpo = cuerpo(100, "CENTIMETRO", "METRO");

        SoporteAPI.asertaError(convertir("LONGITUD", cuerpo, null, SoporteAPI.JSON),
                401, SoporteAPI.MENSAJE_FALTA_BEARER);
        SoporteAPI.asertaError(convertir("LONGITUD", cuerpo, "Bearer token-inexistente", SoporteAPI.JSON),
                401, SoporteAPI.MENSAJE_TOKEN_INVALIDO);
        SoporteAPI.asertaError(convertir("LONGITUD", cuerpo,
                SoporteAPI.bearer(SoporteAPI.tokenVencido()), SoporteAPI.JSON),
                401, SoporteAPI.MENSAJE_TOKEN_INVALIDO);
    }
}
