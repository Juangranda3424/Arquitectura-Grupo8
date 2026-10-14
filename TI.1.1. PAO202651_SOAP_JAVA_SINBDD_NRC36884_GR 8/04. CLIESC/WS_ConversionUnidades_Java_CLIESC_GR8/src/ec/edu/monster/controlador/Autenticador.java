package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Usuario;
import ec.edu.monster.util.ConfigLoader;
import ec.edu.monster.util.HeaderTokenSOAPHandler;

import javax.xml.ws.BindingProvider;

public class Autenticador {

    public String login(Usuario usuario) {
        ec.edu.monster.webservice.WSAutenticador_Service service = new ec.edu.monster.webservice.WSAutenticador_Service();
        ec.edu.monster.webservice.WSAutenticador port = service.getWSAutenticadorPort();

        // 🔹 Inyectar URL dinámica desde el archivo config.properties
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ConfigLoader.getEndpointAutenticador());

        ec.edu.monster.webservice.Usuario usuarioSoap = new ec.edu.monster.webservice.Usuario();
        usuarioSoap.setUsername(usuario.getUsuario());
        usuarioSoap.setPassword(usuario.getContrasena());

        return port.login(usuarioSoap);
    }

    public boolean logout(String token) {
        ec.edu.monster.webservice.WSAutenticador_Service service = new ec.edu.monster.webservice.WSAutenticador_Service();
        ec.edu.monster.webservice.WSAutenticador port = service.getWSAutenticadorPort();

        // 🔹 Inyectar URL dinámica desde el archivo config.properties
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ConfigLoader.getEndpointAutenticador());

        // Inyección reutilizable de la cabecera SOAP
        HeaderTokenSOAPHandler.adjuntarTokenHandler(bp, token);

        return port.logout(token);
    }
}