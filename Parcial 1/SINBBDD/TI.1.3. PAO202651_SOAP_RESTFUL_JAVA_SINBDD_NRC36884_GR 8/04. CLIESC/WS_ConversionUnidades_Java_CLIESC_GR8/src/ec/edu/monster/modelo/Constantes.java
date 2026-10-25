package ec.edu.monster.modelo;

/**
 * Constantes del cliente de escritorio REST (TI.1.3).
 *
 * <p>El catálogo de unidades ya <b>no</b> vive aquí: se obtiene del servidor mediante
 * {@code GET /api/catalogo}. Esta clase solo conserva los nombres de magnitud, el token de
 * sesión en memoria y los ayudantes para dar formato a las etiquetas de unidad del menú.</p>
 */
public class Constantes {

    /** Nombres de las magnitudes soportadas, en el mismo orden que publica el servidor. */
    public static final String[] MAGNITUD = {
        "LONGITUD",
        "MASA",
        "TEMPERATURA"
    };

    public static final String LONGITUD = "LONGITUD";
    public static final String MASA = "MASA";
    public static final String TEMPERATURA = "TEMPERATURA";

    /** Token de sesión vigente; lo fija el login y lo limpia el logout. */
    public static String token = "";

    /**
     * Formatea la etiqueta de una unidad del catálogo tal como se muestra en los menús:
     * {@code CLAVE (Etiqueta, símbolo)}.
     *
     * @param clave   nombre de la unidad usado por la API (por ejemplo {@code CENTIMETRO})
     * @param etiqueta nombre humano publicado por el servidor (por ejemplo {@code Centímetro})
     * @param simbolo símbolo corto publicado por el servidor (por ejemplo {@code cm})
     * @return texto listo para imprimir en el menú
     */
    public static String formatearUnidad(String clave, String etiqueta, String simbolo) {
        if (etiqueta == null || etiqueta.isBlank()) {
            return clave == null ? "" : clave;
        }
        if (simbolo == null || simbolo.isBlank()) {
            return clave + " (" + etiqueta + ")";
        }
        return clave + " (" + etiqueta + ", " + simbolo + ")";
    }

    /**
     * Etiqueta de respaldo cuando solo se conoce la clave de la unidad (catálogo local de
     * emergencia).
     *
     * @param clave nombre de la unidad
     * @return la propia clave, o cadena vacía si es nula
     */
    public static String formatearUnidad(String clave) {
        return clave == null ? "" : clave;
    }
}
