package ec.edu.monster.webservicio;

import ec.edu.monster.controlador.AutenticadorControlador;
import ec.edu.monster.modelo.Usuario;
import jakarta.annotation.Resource;
import jakarta.jws.HandlerChain;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.NodeList;

import java.util.Iterator;

@WebService(serviceName = "WSAutenticador")
@HandlerChain(file = "handlers.xml")
public class WSAutenticador {

    @Resource
    private WebServiceContext context;

    private final AutenticadorControlador autenticador = new AutenticadorControlador();

    @WebMethod(operationName = "login")
    public String login(@WebParam(name = "login") Usuario usuario) {
        return autenticador.login(usuario);
    }

    @WebMethod(operationName = "logout")
    public boolean logout(@WebParam(name = "token") String token) {
        // Si no viene en el body, lo extraemos del SOAP Header
        if (token == null || token.isBlank()) {
            token = obtenerTokenDesdeHeader();
        }
        return autenticador.logout(token);
    }

    private String obtenerTokenDesdeHeader() {
        try {
            MessageContext mc = context.getMessageContext();
            if (mc instanceof SOAPMessageContext smc) {
                SOAPHeader header = smc.getMessage().getSOAPHeader();
                if (header != null) {
                    
                    // Búsqueda Agnóstica mediante NodeList (Funciona en Jakarta)
                    NodeList nodos = header.getElementsByTagNameNS("*", "Token");
                    if (nodos.getLength() == 0) {
                        nodos = header.getElementsByTagNameNS("*", "token");
                    }
                    if (nodos.getLength() > 0) {
                        return nodos.item(0).getTextContent();
                    }

                    // Recorrido iterativo manual de los elementos hijos
                    Iterator<?> iterator = header.getChildElements();
                    while (iterator.hasNext()) {
                        Object obj = iterator.next();
                        if (obj instanceof SOAPElement elem) {
                            if ("Token".equalsIgnoreCase(elem.getLocalName())) {
                                return elem.getValue();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}