package ec.edu.monster.vista;

import ec.edu.monster.controlador.MainController;

import java.util.Scanner;

/**
 * Vista de consola del cliente SOAP de EurekaBank (Grupo 8).
 *
 * <p>Mismas pantallas y flujos que la versión de referencia: inicio de sesión,
 * menú principal, depósito, retiro, transferencia, movimientos y datos de
 * cuenta. La presentación usa el banner y las cajas de {@link Consola}.</p>
 */
public class MainView {

    private static final String ENDPOINT = "http://localhost:8080/WS_EurekaBank_Java_GR8/";

    private static Scanner scanner = new Scanner(System.in);
    private static MainController mainController = new MainController();

    public static void main(String[] args) {
        Consola.banner();
        System.out.println(" " + Consola.etiqueta("Servidor SOAP: " + ENDPOINT));
        while (true) {
            if (mainController.getUsuarioActual() == null) {
                menuLogin();
            } else {
                menuPrincipal();
            }
        }
    }

    private static void menuLogin() {
        System.out.println();
        System.out.println(Consola.caja("EUREKABANK · GR8 | ACCESO",
                "1. Iniciar Sesión",
                "2. Salir"));
        int opcion = leerOpcion();

        switch (opcion) {
            case 1 -> iniciarSesion();
            case 2 -> {
                System.out.println();
                System.out.println(Consola.ok("Gracias por usar nuestros servicios. ¡Hasta luego!"));
                System.exit(0);
            }
            default -> System.out.println(Consola.error("Opción inválida. Intente nuevamente."));
        }
    }

    private static void iniciarSesion() {
        Consola.seccion("INICIO DE SESIÓN");
        System.out.print(" Usuario     : ");
        String username = scanner.nextLine();
        System.out.print(" Contraseña  : ");
        String password = scanner.nextLine();

        try {
            if (mainController.iniciarSesion(username, password)) {
                System.out.println(Consola.ok("Inicio de sesión exitoso. Bienvenido, " + username + "."));
            } else {
                System.out.println(Consola.error("Credenciales incorrectas. Intente nuevamente."));
            }
        } catch (RuntimeException ex) {
            System.out.println(Consola.error("No se pudo conectar con el servidor SOAP en " + ENDPOINT
                    + ". Verifique que esté en ejecución."));
        }
    }

    private static void menuPrincipal() {
        System.out.println();
        System.out.println(Consola.caja("MENÚ PRINCIPAL | " + mainController.getUsuarioActual().toUpperCase(),
                "1. Realizar Depósito",
                "2. Realizar Retiro",
                "3. Realizar Transferencia",
                "4. Ver Movimientos",
                "5. Ver Datos Cuenta",
                "6. Cerrar Sesión"));
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
        Consola.seccion("DEPÓSITO");
        System.out.print(" Número de cuenta : ");
        String cuenta = scanner.nextLine();
        System.out.print(" Monto            : ");
        String monto = scanner.nextLine();

        if (mainController.realizarDeposito(cuenta, monto)) {
            System.out.println(Consola.ok("Depósito realizado con éxito."));
        } else {
            System.out.println(Consola.error("Error al realizar el depósito."));
        }
    }

    private static void realizarRetiro() {
        Consola.seccion("RETIRO");
        System.out.print(" Número de cuenta : ");
        String cuenta = scanner.nextLine();
        System.out.print(" Monto            : ");
        String monto = scanner.nextLine();

        if (mainController.realizarRetiro(cuenta, monto)) {
            System.out.println(Consola.ok("Retiro realizado con éxito."));
        } else {
            System.out.println(Consola.error("Error al realizar el retiro."));
        }
    }

    private static void realizarTransferencia() {
        Consola.seccion("TRANSFERENCIA");
        System.out.print(" Cuenta origen    : ");
        String cuentaOrigen = scanner.nextLine();
        System.out.print(" Cuenta destino   : ");
        String cuentaDestino = scanner.nextLine();
        System.out.print(" Monto            : ");
        String monto = scanner.nextLine();

        if (mainController.realizarTransferencia(cuentaOrigen, cuentaDestino, monto)) {
            System.out.println(Consola.ok("Transferencia realizada con éxito."));
        } else {
            System.out.println(Consola.error("Error al realizar la transferencia."));
        }
    }

    private static void verMovimientos() {
        Consola.seccion("MOVIMIENTOS");
        System.out.print(" Número de cuenta : ");
        String cuenta = scanner.nextLine();

        System.out.println(mainController.verMovimientos(cuenta));
    }

    private static void verDatosCuenta() {
        Consola.seccion("DATOS DE CUENTA");
        System.out.print(" Número de cuenta : ");
        String cuenta = scanner.nextLine();

        System.out.println(mainController.verDatosCuenta(cuenta));
    }

    private static void cerrarSesion() {
        mainController.cerrarSesion();
        System.out.println(Consola.ok("Sesión cerrada exitosamente."));
    }

    /** Lee una opción numérica tolerando entradas no numéricas. */
    private static int leerOpcion() {
        System.out.print(" " + Consola.etiqueta("Seleccione una opción:") + " ");
        String entrada = scanner.nextLine();
        try {
            return Integer.parseInt(entrada.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
