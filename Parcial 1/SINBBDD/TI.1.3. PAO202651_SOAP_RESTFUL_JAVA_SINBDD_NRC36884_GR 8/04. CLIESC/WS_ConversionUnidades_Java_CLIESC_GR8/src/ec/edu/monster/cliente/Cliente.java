package ec.edu.monster.cliente;

import ec.edu.monster.vista.Login;
import javax.swing.SwingUtilities;

public class Cliente {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
    
}
