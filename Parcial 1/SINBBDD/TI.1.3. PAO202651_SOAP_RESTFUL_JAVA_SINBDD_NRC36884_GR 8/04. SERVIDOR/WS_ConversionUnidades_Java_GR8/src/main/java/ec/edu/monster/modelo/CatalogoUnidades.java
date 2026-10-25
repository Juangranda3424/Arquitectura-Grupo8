package ec.edu.monster.modelo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Catálogo en memoria de las unidades soportadas por el servidor (SINBDD).
 *
 * <p>Las claves de cada unidad coinciden con el nombre del enum de conversión
 * ({@link UnidadLongitud}, {@link UnidadMasa}, {@link UnidadTemperatura}) para que el
 * catálogo publicado y el motor de conversión no puedan divergir.</p>
 */
public final class CatalogoUnidades {

    public static final String LONGITUD = "LONGITUD";
    public static final String MASA = "MASA";
    public static final String TEMPERATURA = "TEMPERATURA";

    /**
     * Entrada inmutable del catálogo: clave (nombre del enum), etiqueta humana y símbolo.
     */
    public record UnidadInfo(String clave, String etiqueta, String simbolo) {
    }

    private static final Map<String, List<UnidadInfo>> CATALOGO = construirCatalogo();

    private CatalogoUnidades() {
    }

    private static Map<String, List<UnidadInfo>> construirCatalogo() {
        Map<String, List<UnidadInfo>> catalogo = new LinkedHashMap<>();
        catalogo.put(LONGITUD, List.of(
                new UnidadInfo("CENTIMETRO", "Centímetro", "cm"),
                new UnidadInfo("METRO", "Metro", "m"),
                new UnidadInfo("PIE", "Pie", "ft"),
                new UnidadInfo("YARDA", "Yarda", "yd"),
                new UnidadInfo("MILLA", "Milla", "mi")
        ));
        catalogo.put(MASA, List.of(
                new UnidadInfo("GRAMO", "Gramo", "g"),
                new UnidadInfo("KILOGRAMO", "Kilogramo", "kg"),
                new UnidadInfo("ONZA", "Onza", "oz"),
                new UnidadInfo("LIBRA", "Libra", "lb"),
                new UnidadInfo("QUINTAL", "Quintal", "qq")
        ));
        catalogo.put(TEMPERATURA, List.of(
                new UnidadInfo("CELSIUS", "Celsius", "°C"),
                new UnidadInfo("FAHRENHEIT", "Fahrenheit", "°F"),
                new UnidadInfo("KELVIN", "Kelvin", "K"),
                new UnidadInfo("NEWTON", "Newton", "°N"),
                new UnidadInfo("REAUMUR", "Réaumur", "°Ré")
        ));
        return Collections.unmodifiableMap(catalogo);
    }

    /**
     * @return catálogo completo, inmutable y en el orden LONGITUD, MASA, TEMPERATURA.
     */
    public static Map<String, List<UnidadInfo>> catalogo() {
        return CATALOGO;
    }

    /**
     * @param magnitud nombre de la magnitud (sin distinguir mayúsculas ni espacios)
     * @return unidades registradas, o lista vacía si la magnitud es desconocida
     */
    public static List<UnidadInfo> unidadesDe(String magnitud) {
        List<UnidadInfo> unidades = CATALOGO.get(normalizar(magnitud));
        return unidades == null ? List.of() : unidades;
    }

    /**
     * @param magnitud nombre de la magnitud
     * @param clave nombre del enum de la unidad
     * @return la entrada del catálogo si existe, {@code Optional.empty()} en caso contrario
     */
    public static Optional<UnidadInfo> buscar(String magnitud, String clave) {
        if (clave == null) {
            return Optional.empty();
        }
        String claveNormalizada = clave.trim().toUpperCase(Locale.ROOT);
        return unidadesDe(magnitud).stream()
                .filter(unidad -> unidad.clave().equals(claveNormalizada))
                .findFirst();
    }

    private static String normalizar(String magnitud) {
        return magnitud == null ? "" : magnitud.trim().toUpperCase(Locale.ROOT);
    }
}
