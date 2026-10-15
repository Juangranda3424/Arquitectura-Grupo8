package ec.edu.monster.vista;

import ec.edu.monster.controlador.Autenticador;
import ec.edu.monster.controlador.ConversorUnidades;
import ec.edu.monster.modelo.Constantes;

import java.util.Scanner;

public class ConversorVista {

    private final Scanner sc;
    private final ConversorUnidades controlador;
    private final Autenticador autenticador;
    private final String tokenUsuario;

    public ConversorVista(String tokenUsuario, Scanner sc) {
        this.tokenUsuario = tokenUsuario;
        this.sc = sc;
        this.controlador = new ConversorUnidades();
        this.autenticador = new Autenticador();
    }

    /**
     * Muestra el menú principal.
     * Devuelve true si el usuario quiere salir del programa,
     * false si solo cerró sesión (vuelve al login).
     */
    public boolean mostrar() {
        while (true) {
            System.out.println("\n========== MENÚ PRINCIPAL ==========");
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

    private void realizarConversion() {
        String magnitud = elegirDeLista("Tipo de magnitud", Constantes.MAGNITUD);
        if (magnitud == null) return;

        String[] unidades = obtenerUnidades(magnitud);
        if (unidades.length == 0) {
            System.out.println("[!] No hay unidades para esa magnitud.");
            return;
        }

        String origen = elegirDeLista("Unidad origen", unidades);
        if (origen == null) return;

        String destino = elegirDeLista("Unidad destino", unidades);
        if (destino == null) return;

        Double valor = leerNumero("Valor a convertir: ");
        if (valor == null) return;

        System.out.println("Convirtiendo...");
        try {
            Double resultado = controlador.ejecutarConversion(valor, origen, destino, magnitud);
            System.out.printf("%n>>> %.4f %s = %.4f %s%n", valor, origen, resultado, destino);
        } catch (Exception ex) {
            System.out.println("[X] Error al realizar la conversión: " + ex.getMessage());
        }
    }

    private String[] obtenerUnidades(String magnitud) {
        return switch (magnitud) {
            case "LONGITUD" -> Constantes.LONGITUD;
            case "MASA" -> Constantes.MASA;
            case "TEMPERATURA" -> Constantes.TEMPERATURA;
            default -> new String[]{};
        };
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
            boolean exito = autenticador.logout(tokenUsuario);
            if (exito) {
                Constantes.token = null;
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