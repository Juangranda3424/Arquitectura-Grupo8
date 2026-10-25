package ec.edu.monster.api.recurso;

import java.util.Locale;
import java.util.Set;

import ec.edu.monster.api.soporte.ErrorAPI;
import ec.edu.monster.api.soporte.RespuestaAPI;
import ec.edu.monster.api.soporte.SoportePeticion;
import ec.edu.monster.dto.ConversionDTO;
import ec.edu.monster.modelo.CatalogoUnidades;
import ec.edu.monster.seguridad.ValidadorToken;
import ec.edu.monster.servicio.ConversorServicio;
import ec.edu.monster.servicio.TokenServicio;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * {@code POST /api/conversiones/{magnitud}}: ejecuta la conversión delegando en el núcleo de
 * negocio portado en T1 (mismos factores, validaciones y mensajes que TI.1.1).
 *
 * <p>La magnitud de la ruta siempre gana sobre cualquier {@code tipoMagnitud} del cuerpo y no
 * distingue mayúsculas.</p>
 */
@Path("conversiones")
public class ConversionRecurso {

    private static final Set<String> MAGNITUDES_VALIDAS = Set.of(
            CatalogoUnidades.LONGITUD, CatalogoUnidades.MASA, CatalogoUnidades.TEMPERATURA);

    private final ConversorServicio conversor = new ConversorServicio();
    private final TokenServicio tokens = new TokenServicio();

    @POST
    @Path("{magnitud}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response convertir(@PathParam("magnitud") String magnitud,
                              String cuerpo,
                              @HeaderParam("Authorization") String autorizacion,
                              @HeaderParam("Content-Type") String tipoContenido) {
        ValidadorToken.exigirTokenValido(autorizacion, tokens);
        SoportePeticion.exigirTipoJSON(tipoContenido);

        JsonObject datos = SoportePeticion.leerObjetoJSON(cuerpo);

        String entrada = magnitud == null ? "" : magnitud;
        String tipoMagnitud = entrada.trim().toUpperCase(Locale.ROOT);
        if (!MAGNITUDES_VALIDAS.contains(tipoMagnitud)) {
            throw new ErrorAPI(404, "La magnitud '" + entrada
                    + "' no es válida. Debe ser LONGITUD, MASA o TEMPERATURA.");
        }

        ConversionDTO solicitud = new ConversionDTO(
                SoportePeticion.numero(datos, "valorOrigen"),
                null,
                SoportePeticion.texto(datos, "unidadOrigen"),
                SoportePeticion.texto(datos, "unidadDestino"),
                tipoMagnitud);

        ConversionDTO convertido = conversor.procesarConversion(solicitud);

        JsonObject resultado = Json.createObjectBuilder()
                .add("valorOrigen", convertido.getValorOrigen().doubleValue())
                .add("valorResultado", convertido.getValorResultado().doubleValue())
                .add("unidadOrigen", convertido.getUnidadOrigen())
                .add("unidadDestino", convertido.getUnidadDestino())
                .add("tipoMagnitud", tipoMagnitud)
                .build();

        return RespuestaAPI.respuesta(200,
                RespuestaAPI.ok(200, "Conversión realizada correctamente.", resultado));
    }
}
