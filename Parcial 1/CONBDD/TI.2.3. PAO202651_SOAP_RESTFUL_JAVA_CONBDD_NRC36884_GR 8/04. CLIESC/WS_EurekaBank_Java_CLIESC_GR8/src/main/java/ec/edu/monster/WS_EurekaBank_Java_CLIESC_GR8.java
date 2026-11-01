package ec.edu.monster;

import ec.edu.monster.view.LoginView;

import javax.swing.SwingUtilities;

/**
 * Cliente de escritorio de EurekaBank (GR8) para el servicio RESTful Java.
 * Punto de entrada: pantalla de inicio de sesión.
 */
public class WS_EurekaBank_Java_CLIESC_GR8 {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));
    }
}
