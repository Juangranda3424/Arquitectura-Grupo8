package ec.edu.monster.servicio;

import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.modelo.Usuario;
import java.util.UUID;

public class TokenServicio {

    public String generarToken(Usuario usuario) {

        String token = UUID.randomUUID().toString()+usuario.getUsername();
        Constantes.tokensActivos.add(token);

        return token;
    }

    public boolean validarToken(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        return Constantes.tokensActivos.contains(token);
    }
}
