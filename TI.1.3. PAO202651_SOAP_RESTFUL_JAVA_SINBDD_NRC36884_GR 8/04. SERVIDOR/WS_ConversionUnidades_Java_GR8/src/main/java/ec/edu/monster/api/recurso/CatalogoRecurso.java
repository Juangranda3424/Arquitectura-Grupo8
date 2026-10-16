package ec.edu.monster.api.recurso;

import java.util.List;
import java.util.Map;

import ec.edu.monster.api.soporte.RespuestaAPI;
import ec.edu.monster.modelo.CatalogoUnidades;
import ec.edu.monster.modelo.CatalogoUnidades.UnidadInfo;
import ec.edu.monster.seguridad.ValidadorToken;
import ec.edu.monster.servicio.TokenServicio;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

/**
 * {@code GET /api/catalogo}: unidades soportadas por magnitud (requiere token vigente).
 */
@Path("catalogo")
public class CatalogoRecurso {

    private final TokenServicio tokens = new TokenServicio();

    @GET
    public Response listarCatalogo(@HeaderParam("Authorization") String autorizacion) {
        ValidadorToken.exigirTokenValido(autorizacion, tokens);

        JsonObjectBuilder resultado = Json.createObjectBuilder();
        for (Map.Entry<String, List<UnidadInfo>> magnitud : CatalogoUnidades.catalogo().entrySet()) {
            JsonArrayBuilder unidades = Json.createArrayBuilder();
            for (UnidadInfo unidad : magnitud.getValue()) {
                unidades.add(Json.createObjectBuilder()
                        .add("clave", unidad.clave())
                        .add("etiqueta", unidad.etiqueta())
                        .add("simbolo", unidad.simbolo()));
            }
            resultado.add(magnitud.getKey(), unidades);
        }

        return RespuestaAPI.respuesta(200,
                RespuestaAPI.ok(200, "Catálogo de unidades.", resultado.build()));
    }
}
