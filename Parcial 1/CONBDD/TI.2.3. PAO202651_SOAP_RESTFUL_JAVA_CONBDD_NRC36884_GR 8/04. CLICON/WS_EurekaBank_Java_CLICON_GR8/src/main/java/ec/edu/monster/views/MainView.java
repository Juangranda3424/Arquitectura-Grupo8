package ec.edu.monster.views;

import ec.edu.monster.controller.MainController;
import ec.edu.monster.util.Consola;

import java.util.Scanner;

/**
 * Cliente de consola de EurekaBank (GR8) consumiendo el servicio RESTful Java.
 *
 * Flujo: inicio de sesión -> menú principal -> operaciones -> cierre de sesión.
 */
public class MainView {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final MainController CONTROLADOR = new MainController();

    public static void main(String[] args) {
        System.out.println();
        System.out.println(Consola.banner());
        System.out.println(Consola.atenuado("Servicio RESTful: " + "http://localhost:8080/WS_EurekaBank_Java_GR8/resources"));
        System.out.println();

        while (true) {
            if (CONTROLADOR.getUsuarioActual() == null) {
                menuLogin();
            } else {
                menuPrincipal();
            }
        }
    }

    private static void menuLogin() {
        StringBuilder contenido = new StringBuilder();
        contenido.append("  1. Iniciar Sesión\n");
        contenido.append("  2. Salir\n\n");
        contenido.append("  Usuarios de prueba: MONSTER/MONSTER9 · admin/admin");
        System.out.println(Consola.caja("ACCESO", contenido.toString()));

        int opcion = leerOpcion();

        switch (opcion) {
            case 1 -> iniciarSesion();
            case 2 -> {
                System.out.println(Consola.exito("Gracias por usar nuestros servicios. ¡Hasta luego!"));
                System.exit(0);
            }
            default -> System.out.println(Consola.error("Opción inválida. Intente nuevamente."));
        }
    }

    private static void iniciarSesion() {
        System.out.print(Consola.etiqueta("Usuario: "));
        String username = SCANNER.nextLine();
        System.out.print(Consola.etiqueta("Contraseña: "));
        String password = SCANNER.nextLine();

        if (CONTROLADOR.iniciarSesion(username, password)) {
            System.out.println(Consola.exito("Inicio de sesión exitoso."));
        } else {
            System.out.println(Consola.error("Credenciales incorrectas. Intente nuevamente."));
        }
    }

    private static void menuPrincipal() {
        StringBuilder contenido = new StringBuilder();
        contenido.append("  1. Realizar Depósito\n");
        contenido.append("  2. Realizar Retiro\n");
        contenido.append("  3. Realizar Transferencia\n");
        contenido.append("  4. Ver Movimientos\n");
        contenido.append("  5. Ver Datos Cuenta\n");
        contenido.append("  6. Cerrar Sesión\n\n");
        contenido.append("  Sesión: " + CONTROLADOR.getUsuarioActual());
        System.out.println(Consola.caja("MENÚ PRINCIPAL", contenido.toString()));

        int opcion = leerOpcion();

        switch (opcion) {
            case 1 -> realizarDeposito();
            case 2 -> realizarRetiro();
            case 3 -> realizarTransferencia();
            case 4 -> verMovimientos();
            case 5 -> verDatosCuenta();
            case 6 -> cerrarSesion();
            default -> System.out.println(Consola.error("Opción inválida. Intente nuevamente."));
        }
    }

    private static void realizarDeposito() {
        System.out.print(Consola.etiqueta("Ingrese el número de cuenta: "));
        String cuenta = SCANNER.nextLine();
        System.out.print(Consola.etiqueta("Ingrese el monto a depositar: "));
        String monto = SCANNER.nextLine();

        if (CONTROLADOR.realizarDeposito(cuenta, monto, "DEP", null)) {
            System.out.println(Consola.exito("Depósito realizado con éxito."));
        } else {
            System.out.println(Consola.error("Error al realizar el depósito."));
        }
    }

    private static void realizarRetiro() {
        System.out.print(Consola.etiqueta("Ingrese el número de cuenta: "));
        String cuenta = SCANNER.nextLine();
        System.out.print(Consola.etiqueta("Ingrese el monto a retirar: "));
        String monto = SCANNER.nextLine();

        if (CONTROLADOR.realizarRetiro(cuenta, monto)) {
            System.out.println(Consola.exito("Retiro realizado con éxito."));
        } else {
            System.out.println(Consola.error("Error al realizar el retiro."));
        }
    }

    private static void realizarTransferencia() {
        System.out.print(Consola.etiqueta("Ingrese el número de cuenta origen: "));
        String cuentaOrigen = SCANNER.nextLine();
        System.out.print(Consola.etiqueta("Ingrese el número de cuenta destino: "));
        String cuentaDestino = SCANNER.nextLine();
        System.out.print(Consola.etiqueta("Ingrese el monto a transferir: "));
        String monto = SCANNER.nextLine();

        if (CONTROLADOR.realizarTransferencia(cuentaOrigen, cuentaDestino, monto)) {
            System.out.println(Consola.exito("Transferencia realizada con éxito."));
        } else {
            System.out.println(Consola.error("Error al realizar la transferencia."));
        }
    }

    private static void verMovimientos() {
        System.out.print(Consola.etiqueta("Ingrese el número de cuenta: "));
        String cuenta = SCANNER.nextLine();
        System.out.println(CONTROLADOR.verMovimientos(cuenta));
    }

    private static void verDatosCuenta() {
        System.out.print(Consola.etiqueta("Ingrese el número de cuenta: "));
        String cuenta = SCANNER.nextLine();
        System.out.println(CONTROLADOR.verDatosCuenta(cuenta));
    }

    private static void cerrarSesion() {
        CONTROLADOR.cerrarSesion();
        System.out.println(Consola.exito("Sesión cerrada exitosamente."));
    }

    private static int leerOpcion() {
        System.out.print(Consola.etiqueta("Seleccione una opción: "));
        String linea = SCANNER.nextLine().trim();
        try {
            return Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
