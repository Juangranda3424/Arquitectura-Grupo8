package ec.edu.monster.vista;

import ec.edu.monster.controlador.Autenticador;
import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.Usuario;

import java.util.Scanner;

/**
 * Vista de consola para el inicio de sesión contra el servicio REST.
 *
 * <p>Se conserva el flujo de TI.1.1: pedir usuario y contraseña, validar que no estén vacíos,
 * autenticar, guardar el token en {@link Constantes#token} y abrir {@link ConversorVista}.</p>
 */
public class Login {

    private final Scanner sc = new Scanner(System.in);
    private final Autenticador autenticador = new Autenticador();

    public void iniciar() {
        System.out.println("=====================================");
        System.out.println("   CONVERSOR DE UNIDADES - REST");
        System.out.println("=====================================");

        while (true) {
            System.out.println("\n--- INICIO DE SESIÓN (escribe 0 como usuario para salir) ---");
            System.out.print("Usuario: ");
            String username = sc.nextLine().trim();

            if (username.equals("0")) {
                break;
            }

            String password = leerPassword();

            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("[!] Por favor, completa todos los campos.");
                continue;
            }

            System.out.println("Conectando...");
            try {
                String respuesta = autenticador.login(new Usuario(username, password));

                if (respuestaValida(respuesta)) {
                    Constantes.token = respuesta;
                    System.out.println("[OK] Sesión iniciada con éxito.");

                    // Abre la vista principal; devuelve true si el usuario quiere salir del programa
                    boolean salir = new ConversorVista(Constantes.token, sc).mostrar();
                    if (salir) {
                        break;
                    }
                    // si cerró sesión, el ciclo vuelve a pedir login
                } else {
                    System.out.println("[X] Error de autenticación: "
                            + (respuesta == null ? "Sin respuesta del servidor." : respuesta));
                }
            } catch (Exception ex) {
                System.out.println("[X] " + ex.getMessage());
            }
        }

        System.out.println("\nHasta luego.");
        sc.close();
    }

    private boolean respuestaValida(String respuesta) {
        if (respuesta == null || respuesta.isBlank()) {
            return false;
        }
        String r = respuesta.toLowerCase();
        return !r.contains("error") && !r.contains("inválido") && !r.contains("invalido");
    }

    private String leerPassword() {
        java.io.Console console = System.console();
        if (console != null) {
            char[] pass = console.readPassword("Contraseña: ");
            return pass == null ? "" : new String(pass);
        }
        // En el IDE (NetBeans) no hay consola real, se lee normal
        System.out.print("Contraseña: ");
        return sc.nextLine();
    }
}
