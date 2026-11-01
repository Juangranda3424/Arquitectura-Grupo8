package ec.edu.monster.vista;

/**
 * Utilidades de consola para la identidad visual de EurekaBank (Grupo 8).
 *
 * <p>Dibuja el banner ASCII, cajas de menú y aplica color ANSI cuando la
 * terminal lo soporta (Windows Terminal, VS Code, Linux/macOS). En consolas que
 * no lo soportan se degrada a texto plano con las mismas cajas ASCII.</p>
 */
public final class Consola {

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001B[1m";
    public static final String NAVY = "\u001B[38;2;11;31;58m";
    public static final String TEAL = "\u001B[38;2;15;163;177m";
    public static final String AMBER = "\u001B[38;2;242;165;65m";
    public static final String MUTED = "\u001B[38;2;100;116;139m";
    public static final String SUCCESS = "\u001B[38;2;22;163;74m";
    public static final String ERROR = "\u001B[38;2;220;38;38m";

    private static final boolean COLOR = detectarColor();

    private Consola() {
    }

    private static boolean detectarColor() {
        String forzar = System.getenv("EUREKABANK_COLOR");
        if ("1".equals(forzar)) {
            return true;
        }
        if ("0".equals(forzar) || System.getenv("NO_COLOR") != null) {
            return false;
        }
        if (System.getenv("ANSICON") != null || System.getenv("WT_SESSION") != null) {
            return true;
        }
        String term = System.getenv("TERM");
        if (term != null && !term.isEmpty() && !"dumb".equalsIgnoreCase(term)) {
            return true;
        }
        return !System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    public static boolean colorActivo() {
        return COLOR;
    }

    /** Pinta un texto con un código ANSI si la terminal lo soporta. */
    public static String c(String texto, String codigo) {
        return COLOR ? codigo + texto + RESET : texto;
    }

    /** Banner de la aplicación: marca, grupo y materia. */
    public static void banner() {
        String linea = repetir('=', 62);
        System.out.println();
        System.out.println(c("+" + linea + "+", TEAL));
        System.out.println(c("|", TEAL) + c(centrar("[E]  EUREKABANK · GR8  [E]", 62), BOLD + AMBER) + c("|", TEAL));
        System.out.println(c("|", TEAL) + c(centrar("Grupo 8 · Arquitectura de Software", 62), MUTED) + c("|", TEAL));
        System.out.println(c("|", TEAL) + c(centrar("Banca por internet sobre servicios web SOAP", 62), MUTED) + c("|", TEAL));
        System.out.println(c("+" + linea + "+", TEAL));
        System.out.println();
    }

    /**
     * Dibuja una caja ASCII con título centrado.
     *
     * @param titulo  texto de la cabecera
     * @param lineas  contenido, una entrada por línea
     * @return la caja lista para imprimir
     */
    public static String caja(String titulo, String... lineas) {
        int ancho = titulo.length();
        for (String linea : lineas) {
            if (linea != null && linea.length() > ancho) {
                ancho = linea.length();
            }
        }
        ancho = Math.max(ancho, 12);

        StringBuilder sb = new StringBuilder();
        String borde = repetir('-', ancho + 4);
        sb.append(c("+" + borde + "+", TEAL)).append('\n');
        sb.append(c("|", TEAL)).append(c(centrar(" " + titulo + " ", ancho + 4), BOLD + NAVY)).append(c("|", TEAL)).append('\n');
        sb.append(c("+" + borde + "+", TEAL)).append('\n');
        if (lineas.length == 0) {
            sb.append(c("|", TEAL)).append(repetir(' ', ancho + 4)).append(c("|", TEAL)).append('\n');
        }
        for (String linea : lineas) {
            sb.append(c("|", TEAL)).append(izquierda(" " + (linea == null ? "" : linea), ancho + 4)).append(c("|", TEAL)).append('\n');
        }
        sb.append(c("+" + borde + "+", TEAL));
        return sb.toString();
    }

    public static String ok(String texto) {
        return c("[OK] ", SUCCESS) + texto;
    }

    public static String error(String texto) {
        return c("[!] ", ERROR) + texto;
    }

    public static String info(String texto) {
        return c("[i] ", AMBER) + texto;
    }

    public static String etiqueta(String texto) {
        return c(texto, MUTED);
    }

    public static String repetir(char ch, int veces) {
        StringBuilder sb = new StringBuilder(veces);
        for (int i = 0; i < veces; i++) {
            sb.append(ch);
        }
        return sb.toString();
    }

    public static String izquierda(String texto, int ancho) {
        StringBuilder sb = new StringBuilder(texto);
        while (sb.length() < ancho) {
            sb.append(' ');
        }
        return sb.toString();
    }

    public static String centrar(String texto, int ancho) {
        if (texto.length() >= ancho) {
            return texto;
        }
        int total = ancho - texto.length();
        int izq = total / 2;
        int der = total - izq;
        return repetir(' ', izq) + texto + repetir(' ', der);
    }

    /** Encabezado de sección reutilizable. */
    public static void seccion(String titulo) {
        System.out.println();
        System.out.println(c(" " + titulo, BOLD + NAVY));
        System.out.println(c(" " + repetir('-', titulo.length()), TEAL));
    }
}
