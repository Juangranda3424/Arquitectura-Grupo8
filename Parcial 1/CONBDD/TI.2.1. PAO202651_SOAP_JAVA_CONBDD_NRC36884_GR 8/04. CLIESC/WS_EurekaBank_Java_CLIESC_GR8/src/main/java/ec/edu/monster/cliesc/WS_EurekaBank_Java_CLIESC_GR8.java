/*
 * Punto de entrada del cliente de escritorio (Swing) de EurekaBank - Grupo 8.
 */

package ec.edu.monster.cliesc;
import ec.edu.monster.vista.LoginView;

/**
 * Cliente de escritorio SOAP: abre la pantalla de inicio de sesión.
 */
public class WS_EurekaBank_Java_CLIESC_GR8 {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            LoginView lv = new LoginView();
            lv.setLocationRelativeTo(null);
            lv.setVisible(true);
        });
    }
}
