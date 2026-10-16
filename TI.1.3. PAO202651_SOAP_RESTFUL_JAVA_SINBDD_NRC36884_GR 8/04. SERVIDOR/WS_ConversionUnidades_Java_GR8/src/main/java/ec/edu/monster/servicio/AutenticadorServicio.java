package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.Usuario;

public class AutenticadorServicio {

    private final TokenServicio token = new TokenServicio();

    public String autenticadorLogin(Usuario usuario) {

        if (usuario == null
                || usuario.getUsername() == null
                || usuario.getPassword() == null
                || usuario.getUsername().trim().isEmpty()
                || usuario.getPassword().isEmpty()) {

            return "Usuario invalido";
        }

        if (Constantes.USUARIO.equals(usuario.getUsername())
                && Constantes.CONTRASENA.equals(usuario.getPassword())) {

            return token.generarToken(usuario);

        } else {

            return "Usuario invalido";
        }
    }

    public boolean autenticadorLogout(String tokenUsuario) {

        try {

            return token.invalidar(tokenUsuario);
        } catch (Exception e) {

            return false;
        }
    }
}
