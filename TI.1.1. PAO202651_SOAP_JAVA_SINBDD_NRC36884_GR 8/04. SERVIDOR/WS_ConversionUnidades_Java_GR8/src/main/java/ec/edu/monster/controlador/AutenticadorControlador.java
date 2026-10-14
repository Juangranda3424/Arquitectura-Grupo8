package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.servicio.AutenticadorServicio;


public class AutenticadorControlador {
    
    private final AutenticadorServicio autenticador = new AutenticadorServicio();
        
    public String login(Usuario usuario){
        
        return autenticador.autenticadorLogin(usuario);
   
    }

    public boolean logout(String token) {
        
        return autenticador.autenticadorLogout(token);
        
    }
}
