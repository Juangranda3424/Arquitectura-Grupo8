package ec.edu.monster.util;

import ec.edu.monster.modelo.Constantes;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HeaderTokenSOAPHandler implements SOAPHandler<SOAPMessageContext> {

    private final String token;

    // Constructor que usa el token global de Constantes por defecto
    public HeaderTokenSOAPHandler() {
        this.token = null; 
    }

    // Constructor opcional para pasar un token específico
    public HeaderTokenSOAPHandler(String token) {
        this.token = token;
    }

    /**
     * Método de conveniencia estático para inyectar el Handler en cualquier BindingProvider
     */
    public static void adjuntarTokenHandler(BindingProvider bindingProvider, String token) {
        List<Handler> handlerChain = bindingProvider.getBinding().getHandlerChain();
        if (handlerChain == null) {
            handlerChain = new ArrayList<>();
        }
        handlerChain.add(new HeaderTokenSOAPHandler(token));
        bindingProvider.getBinding().setHandlerChain(handlerChain);
    }

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean isOutbound = (Boolean) context.get(SOAPMessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (Boolean.TRUE.equals(isOutbound)) {
            try {
                SOAPMessage message = context.getMessage();
                SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();

                SOAPHeader header = envelope.getHeader();
                if (header == null) {
                    header = envelope.addHeader();
                }

                String namespaceURI = "http://webservicio.monster.edu.ec/";
                QName tokenQName = new QName(namespaceURI, "Token", "web");

                SOAPHeaderElement tokenElement = header.addHeaderElement(tokenQName);
                
                // Si se pasó un token al constructor se usa ese; si no, se usa Constantes.token
                String tokenAInyectar = (this.token != null) ? this.token : Constantes.token;
                tokenElement.addTextNode(tokenAInyectar);

                message.saveChanges();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }
}