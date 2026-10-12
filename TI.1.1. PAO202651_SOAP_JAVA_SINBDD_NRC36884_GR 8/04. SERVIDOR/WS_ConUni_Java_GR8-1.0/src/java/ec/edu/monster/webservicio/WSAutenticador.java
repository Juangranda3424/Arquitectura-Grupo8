package ec.edu.monster.webservicio;

import ec.edu.monster.controlador.AutenticadorControlador;
import ec.edu.monster.modelo.Usuario;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;


@WebService(serviceName = "WSAutenticador")
public class WSAutenticador {

    private AutenticadorControlador autenticador = new AutenticadorControlador();
    
    /**
     * Operación del Web Service SOAP para realizar la autenticacion del Login.
     */
    
    @WebMethod(operationName = "login")
    public String login(@WebParam(name = "login") Usuario usuario) {
        return autenticador.login(usuario);
    }
}
