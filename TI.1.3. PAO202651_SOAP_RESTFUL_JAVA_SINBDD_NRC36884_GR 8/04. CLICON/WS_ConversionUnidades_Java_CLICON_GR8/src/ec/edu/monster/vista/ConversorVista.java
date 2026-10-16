package ec.edu.monster.vista;

import ec.edu.monster.controlador.Autenticador;
import ec.edu.monster.controlador.ConversorUnidades;
import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.servicio.ClienteREST;
import ec.edu.monster.servicio.RespuestaAPI;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Vista de consola del conversor de unidades.
 *
 * <p>Conserva el flujo de TI.1.1 (elegir magnitud, unidad origen, unidad destino y valor; imprimir
 * {@code >>> %.4f X = %.4f Y}), pero el catálogo de unidades se carga dinámicamente desde
 * {@code GET /api/catalogo} tras el login. Si el servidor no responde, se usa un catálogo local de
 * respaldo para no dejar la sesión inutilizable.</p>
 */
public class ConversorVista {

    private final Scanner sc;
    private final ConversorUnidades controlador;
    private final Autenticador autenticador;
    private final ClienteREST clienteREST;
    private final String tokenUsuario;

    private Map<String, List<UnidadCatalogo>> catalogo;
    private boolean catalogoDesdeServidor;

    /**
     * Entrada del catálogo publicada por el servidor: clave (nombre de enum), etiqueta humana y
     * símbolo.
     */
    public record UnidadCatalogo(String clave, String etiqueta, String simbolo) {

        /** Texto del menú: {@code CLAVE (Etiqueta, símbolo)}. */
        public String etiquetaMenu() {
            return Constantes.formatearUnidad(clave, etiqueta, simbolo);
        }
    }

    /** Catálogo local de último recurso: solo claves, sin etiquetas ni símbolos. */
    private static final String[] RESPALDO_LONGITUD = {
        "CENTIMETRO", "METRO", "PIE", "YARDA", "MILLA"
    };
    private static final String[] RESPALDO_MASA = {
        "GRAMO", "KILOGRAMO", "ONZA", "LIBRA", "QUINTAL"
    };
    private static final String[] RESPALDO_TEMPERATURA = {
        "CELSIUS", "FAHRENHEIT", "KELVIN", "NEWTON", "REAUMUR"
    };

    public ConversorVista(String tokenUsuario, Scanner sc) {
        this.tokenUsuario = tokenUsuario;
        this.sc = sc;
        this.clienteREST = new ClienteREST();
        this.controlador = new ConversorUnidades(clienteREST);
        this.autenticador = new Autenticador(clienteREST);
        cargarCatalogo();
    }

    /**
     * Muestra el menú principal.
     * Devuelve true si el usuario quiere salir del programa,
     * false si solo cerró sesión (vuelve al login).
     */
    public boolean mostrar() {
        while (true) {
            System.out.println("\n========== MENÚ PRINCIPAL ==========");
            System.out.println("(catálogo: " + (catalogoDesdeServidor ? "servidor REST" : "local de respaldo") + ")");
            System.out.println("1. Convertir unidades");
            System.out.println("2. Cerrar sesión");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            switch (sc.nextLine().trim()) {
                case "1" -> realizarConversion();
                case "2" -> {
                    if (realizarLogout()) {
                        return false;
                    }
                }
                case "0" -> {
                    return true;
                }
                default -> System.out.println("[!] Opción no válida.");
            }
        }
    }

    // ------------------------------------------------------------------
    // Catálogo
    // ------------------------------------------------------------------

    private void cargarCatalogo() {
        System.out.println("Cargando catálogo de unidades...");
        try {
            RespuestaAPI respuesta = clienteREST.catalogo();
            if (!respuesta.esExitosa()) {
                throw new Exception(respuesta.getMensaje());
            }
            JsonObject datos = respuesta.getResultado();
            if (datos == null || datos.isEmpty()) {
                throw new Exception("El servidor no devolvió ninguna unidad.");
            }

            Map<String, List<UnidadCatalogo>> desdeServidor = parsearCatalogo(datos);
            if (desdeServidor.isEmpty()) {
                throw new Exception("El catálogo recibido está vacío.");
            }

            this.catalogo = desdeServidor;
            this.catalogoDesdeServidor = true;
            System.out.println("[OK] Catálogo cargado desde el servidor ("
                    + catalogo.size() + " magnitudes).");
        } catch (Exception ex) {
            System.out.println("[X] No se pudo cargar el catálogo: " + ex.getMessage());
            System.out.println("[!] Usando catálogo local de respaldo.");
            this.catalogo = catalogoRespaldo();
            this.catalogoDesdeServidor = false;
        }
    }

    private Map<String, List<UnidadCatalogo>> parsearCatalogo(JsonObject datos) {
        Map<String, List<UnidadCatalogo>> resultado = new LinkedHashMap<>();

        for (Map.Entry<String, JsonValue> magnitud : datos.entrySet()) {
            if (magnitud.getValue().getValueType() != JsonValue.ValueType.ARRAY) {
                continue;
            }
            JsonArray arreglo = magnitud.getValue().asJsonArray();
            List<UnidadCatalogo> unidades = new ArrayList<>();
            for (JsonValue valor : arreglo) {
                if (valor.getValueType() != JsonValue.ValueType.OBJECT) {
                    continue;
                }
                JsonObject unidad = valor.asJsonObject();
                unidades.add(new UnidadCatalogo(
                        unidad.getString("clave", ""),
                        unidad.getString("etiqueta", ""),
                        unidad.getString("simbolo", "")));
            }
            if (!unidades.isEmpty()) {
                resultado.put(magnitud.getKey(), unidades);
            }
        }

        return resultado;
    }

    private Map<String, List<UnidadCatalogo>> catalogoRespaldo() {
        Map<String, List<UnidadCatalogo>> resultado = new LinkedHashMap<>();
        resultado.put(Constantes.LONGITUD, respaldo(RESPALDO_LONGITUD));
        resultado.put(Constantes.MASA, respaldo(RESPALDO_MASA));
        resultado.put(Constantes.TEMPERATURA, respaldo(RESPALDO_TEMPERATURA));
        return resultado;
    }

    private List<UnidadCatalogo> respaldo(String[] claves) {
        List<UnidadCatalogo> unidades = new ArrayList<>();
        for (String clave : claves) {
            unidades.add(new UnidadCatalogo(clave, "", ""));
        }
        return unidades;
    }

    // ------------------------------------------------------------------
    // Conversión
    // ------------------------------------------------------------------

    private void realizarConversion() {
        if (catalogo == null || catalogo.isEmpty()) {
            System.out.println("[!] No hay magnitudes disponibles.");
            return;
        }

        String magnitud = elegirDeLista("Tipo de magnitud", catalogo.keySet().toArray(new String[0]));
        if (magnitud == null) return;

        List<UnidadCatalogo> unidades = catalogo.get(magnitud);
        if (unidades == null || unidades.isEmpty()) {
            System.out.println("[!] No hay unidades para esa magnitud.");
            return;
        }

        UnidadCatalogo origen = elegirUnidad("Unidad origen", unidades);
        if (origen == null) return;

        UnidadCatalogo destino = elegirUnidad("Unidad destino", unidades);
        if (destino == null) return;

        Double valor = leerNumero("Valor a convertir: ");
        if (valor == null) return;

        System.out.println("Convirtiendo...");
        try {
            Double resultado = controlador.ejecutarConversion(
                    valor, origen.clave(), destino.clave(), magnitud);
            System.out.printf("%n>>> %.4f %s = %.4f %s%n",
                    valor, origen.clave(), resultado, destino.clave());
        } catch (Exception ex) {
            System.out.println("[X] Error al realizar la conversión: " + ex.getMessage());
        }
    }

    /** Lista numerada; devuelve la opción elegida o null si cancela con 0. */
    private String elegirDeLista(String titulo, String[] opciones) {
        while (true) {
            System.out.println("\n" + titulo + ":");
            for (int i = 0; i < opciones.length; i++) {
                System.out.println("  " + (i + 1) + ". " + opciones[i]);
            }
            System.out.println("  0. Cancelar");
            System.out.print("Elige: ");

            try {
                int n = Integer.parseInt(sc.nextLine().trim());
                if (n == 0) return null;
                if (n >= 1 && n <= opciones.length) return opciones[n - 1];
            } catch (NumberFormatException ignored) {}

            System.out.println("[!] Opción no válida.");
        }
    }

    /** Lista numerada de unidades del catálogo; devuelve la elegida o null si cancela. */
    private UnidadCatalogo elegirUnidad(String titulo, List<UnidadCatalogo> opciones) {
        while (true) {
            System.out.println("\n" + titulo + ":");
            for (int i = 0; i < opciones.size(); i++) {
                System.out.println("  " + (i + 1) + ". " + opciones.get(i).etiquetaMenu());
            }
            System.out.println("  0. Cancelar");
            System.out.print("Elige: ");

            try {
                int n = Integer.parseInt(sc.nextLine().trim());
                if (n == 0) return null;
                if (n >= 1 && n <= opciones.size()) return opciones.get(n - 1);
            } catch (NumberFormatException ignored) {}

            System.out.println("[!] Opción no válida.");
        }
    }

    private Double leerNumero(String mensaje) {
        while (true) {
            System.out.print(mensaje + "(c para cancelar) ");
            String texto = sc.nextLine().trim().replace(',', '.');

            if (texto.isEmpty()) {
                System.out.println("[!] Ingresa un valor numérico.");
                continue;
            }
            if (texto.equalsIgnoreCase("c")) return null;

            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException ex) {
                System.out.println("[!] Ingresa un número válido.");
            }
        }
    }

    /** Devuelve true si la sesión se cerró correctamente. */
    private boolean realizarLogout() {
        System.out.print("¿Seguro que deseas cerrar sesión? (s/n): ");
        if (!sc.nextLine().trim().equalsIgnoreCase("s")) {
            return false;
        }

        System.out.println("Cerrando sesión...");
        try {
            boolean exito = autenticador.logout();
            if (exito) {
                System.out.println("[OK] Sesión cerrada correctamente.");
                return true;
            }
            System.out.println("[X] No se pudo cerrar la sesión en el servidor.");
        } catch (Exception ex) {
            System.out.println("[X] Error al cerrar sesión: " + ex.getMessage());
        }
        return false;
    }
}
