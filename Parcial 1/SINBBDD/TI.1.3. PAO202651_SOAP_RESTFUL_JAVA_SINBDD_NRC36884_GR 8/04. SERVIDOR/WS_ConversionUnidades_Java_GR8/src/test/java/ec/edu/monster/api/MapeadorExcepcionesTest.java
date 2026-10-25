package ec.edu.monster.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import ec.edu.monster.api.soporte.ErrorAPI;
import ec.edu.monster.api.soporte.MapeadorExcepciones;

import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.NotSupportedException;
import jakarta.ws.rs.core.Response;

class MapeadorExcepcionesTest {

    private final MapeadorExcepciones mapeador = new MapeadorExcepciones();

    private Response mapear(Throwable error) {
        return mapeador.toResponse(error);
    }

    @Test
    void errorAPIConservaSuCodigoHTTPYMensaje() {
        SoporteAPI.asertaError(mapear(new ErrorAPI(401, "Acceso denegado: Token inválido o sesión expirada.")),
                401, "Acceso denegado: Token inválido o sesión expirada.");
        SoporteAPI.asertaError(mapear(new ErrorAPI(404, "no encontrado")), 404, "no encontrado");
        SoporteAPI.asertaError(mapear(new ErrorAPI(415, "json requerido")), 415, "json requerido");
    }

    @Test
    void argumentoIlegalDevuelve400ConElMensajeDeLaExcepcion() {
        SoporteAPI.asertaError(mapear(new IllegalArgumentException("La unidad 'X' no es válida.")),
                400, "La unidad 'X' no es válida.");
    }

    @Test
    void recursoDesconocidoDevuelve404() {
        SoporteAPI.asertaError(mapear(new NotFoundException()), 404, "Recurso no encontrado.");
    }

    @Test
    void metodoNoPermitidoDevuelve405() {
        SoporteAPI.asertaError(mapear(new NotAllowedException("POST")), 405,
                "Método HTTP no permitido para este recurso.");
    }

    @Test
    void tipoDeContenidoNoSoportadoDevuelve415() {
        SoporteAPI.asertaError(mapear(new NotSupportedException()), 415,
                "El contenido de la petición debe ser JSON.");
    }

    @Test
    void cualquierOtraExcepcionDevuelve500SinFiltrarDetalles() {
        Response respuesta = mapear(new IllegalStateException("detalle interno sensible"));

        SoporteAPI.asertaError(respuesta, 500, "Error interno del servidor.");
        assertFalse(SoporteAPI.envelope(respuesta).getString("mensaje").contains("sensible"));
        assertNotEquals("detalle interno sensible", SoporteAPI.envelope(respuesta).getString("mensaje"));
    }

    @Test
    void elMapeadorEsUnProviderJAXRS() {
        assertNotNull(mapeador.getClass().getAnnotation(jakarta.ws.rs.ext.Provider.class),
                "@Provider presente");
    }
}
