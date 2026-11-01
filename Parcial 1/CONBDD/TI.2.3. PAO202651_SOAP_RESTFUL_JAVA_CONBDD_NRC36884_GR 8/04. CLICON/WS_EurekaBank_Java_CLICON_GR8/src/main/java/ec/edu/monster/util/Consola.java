package ec.edu.monster.util;

/**
 * Utilidades de presentación para el cliente de consola (CLICON).
 *
 * <p>Identidad visual compartida de EurekaBank · Grupo 8:
 * banner ASCII, cajas de menú y colores ANSI (el stack lo permite porque es
 * una aplicación de consola Java ejecutada sobre JDK 21).</p>
 */
public final class Consola {

    // ---- Paleta ANSI (equivalente a la paleta de la marca) ----
    public static final String RESET = "\u001B[0m";
    public static final String NEGRITA = "\u001B[1m";
    public static final String NAVY = "\u001B[36m";     // títulos y marcos (cian)
    public static final String TEAL = "\u001B[96m";     // acentos
    public static final String AMBER = "\u001B[93m";    // etiquetas
    public static final String VERDE = "\u001B[92m";    // éxito
    public static final String ROJO = "\u001B[91m";     // error
    public static final String GRIS = "\u001B[90m";     // texto atenuado

    private static final String MARCA = "EUREKABANK · GR8";
    private static final String SUBTITULO = "Grupo 8 · Arquitectura de Software";

    private Consola() {
    }

    /** Banner ASCII de la marca. */
    public static String banner() {
        String texto = MARCA + " · " + SUBTITULO;
        String borde = "+" + "=".repeat(texto.length() + 2) + "+";
        StringBuilder sb = new StringBuilder();
        sb.append(NAVY).append(borde).append(RESET).append('\n');
        sb.append(NAVY).append('|').append(RESET)
          .append(NEGRITA).append(AMBER).append(' ').append(texto).append(' ').append(RESET)
          .append(NAVY).append('|').append(RESET).append('\n');
        sb.append(NAVY).append(borde).append(RESET);
        return sb.toString();
    }

    /** Caja con título centrado y contenido multilínea. */
    public static String caja(String titulo, String contenido) {
        String[] lineas = contenido.split("\\R", -1);
        int ancho = titulo.length();
        for (String linea : lineas) {
            ancho = Math.max(ancho, linea.length());
        }
        String horizontal = "=".repeat(ancho + 2);

        StringBuilder sb = new StringBuilder();
        sb.append(NAVY).append('+').append(horizontal).append('+').append(RESET).append('\n');
        sb.append(NAVY).append('|').append(RESET)
          .append(NEGRITA).append(TEAL).append(centrar(" " + titulo + " ", ancho + 2)).append(RESET)
          .append(NAVY).append('|').append(RESET).append('\n');
        sb.append(NAVY).append('+').append(horizontal).append('+').append(RESET).append('\n');
        for (String linea : lineas) {
            sb.append(NAVY).append('|').append(RESET)
              .append(' ').append(izquierda(linea, ancho)).append(' ')
              .append(NAVY).append('|').append(RESET).append('\n');
        }
        sb.append(NAVY).append('+').append(horizontal).append('+').append(RESET);
        return sb.toString();
    }

    public static String exito(String texto) {
        return VERDE + "✔ " + texto + RESET;
    }

    public static String error(String texto) {
        return ROJO + "✖ " + texto + RESET;
    }

    public static String etiqueta(String texto) {
        return AMBER + NEGRITA + texto + RESET;
    }

    public static String atenuado(String texto) {
        return GRIS + texto + RESET;
    }

    private static String izquierda(String texto, int ancho) {
        if (texto.length() >= ancho) {
            return texto;
        }
        return texto + " ".repeat(ancho - texto.length());
    }

    private static String centrar(String texto, int ancho) {
        if (texto.length() >= ancho) {
            return texto;
        }
        int total = ancho - texto.length();
        int izquierda = total / 2;
        int derecha = total - izquierda;
        return " ".repeat(izquierda) + texto + " ".repeat(derecha);
    }
}
