package ec.edu.monster.seguridad;

import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.servicio.TokenServicio;

import javax.xml.namespace.QName;
import jakarta.xml.soap.SOAPConstants;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFactory;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.soap.SOAPHeader;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.ProtocolException;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import jakarta.xml.ws.soap.SOAPFaultException;

import org.w3c.dom.NodeList;

import java.util.Collections;
import java.util.Set;

public class ValidadorToken implements SOAPHandler<SOAPMessageContext> {

    private final TokenServicio validadorToken = new TokenServicio();

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean salida = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        // Si el mensaje va de salida hacia el cliente, no se valida token
        if (Boolean.TRUE.equals(salida)) {
            return true;
        }

        try {
            SOAPMessage mensaje = context.getMessage();
            if (mensaje == null || mensaje.getSOAPPart() == null) {
                return true;
            }

            SOAPEnvelope envelope = mensaje.getSOAPPart().getEnvelope();

            // Omitir validación de token si es una petición de Login
            if (esOperacionLogin(envelope, context)) {
                System.out.println(">>> Operación de login detectada, omitiendo token.");
                return true;
            }

            // OBTENER LA CABECERA (SOAP HEADER)
            SOAPHeader header = mensaje.getSOAPHeader();

            if (header == null) {
                rechazar("Acceso denegado: Falta el encabezado SOAP (SOAPHeader) en la petición.");
            }

            // EXTRAER EL ELEMENTO <Token> o <token> DENTRO DE LA CABECERA
            String token = extraerTokenDeHeader(header);

            if (token == null || token.trim().isEmpty()) {
                rechazar("Acceso denegado: No se encontró la etiqueta <Token> o estaba vacía en la cabecera SOAP.");
            }

            // VALIDAR EL TOKEN
            if (!validadorToken.validarToken(token)) {
                rechazar("Acceso denegado: Token inválido o sesión expirada.");
            }

            //System.out.println(">>> Token validado exitosamente.");
            return true;

        } catch (SOAPFaultException e) {
            throw e;
        } catch (Exception e) {
            //System.err.println("Error en ValidadorToken: " + e.getMessage());
            rechazar("Error procesando autenticación: " + e.getMessage());
        }

        return false;
    }

    /**
     * Busca la etiqueta Token o token dentro del SOAPHeader, probando
     * tanto con el Namespace configurado como de forma agnóstica a Namespaces.
     */
    private String extraerTokenDeHeader(SOAPHeader header) {
        // Opción A: Búsqueda por NodeList sin importar Namespace ("*", "Token")
        NodeList nodos = header.getElementsByTagNameNS("*", "Token");
        if (nodos.getLength() == 0) {
            nodos = header.getElementsByTagNameNS("*", "token");
        }

        if (nodos.getLength() > 0) {
            return nodos.item(0).getTextContent();
        }

        // Opción B: Recorrido manual de elementos SOAP en el Header
        java.util.Iterator<?> elementos = header.getChildElements();
        while (elementos.hasNext()) {
            Object obj = elementos.next();
            if (obj instanceof SOAPElement) {
                SOAPElement elem = (SOAPElement) obj;
                if ("Token".equalsIgnoreCase(elem.getLocalName())) {
                    return elem.getValue();
                }
            }
        }

        return null;
    }

    private boolean esOperacionLogin(SOAPEnvelope envelope, SOAPMessageContext context) {
        try {
            // Verificación vía WSDL Operation
            QName operacion = (QName) context.get(MessageContext.WSDL_OPERATION);
            if (operacion != null) {
                String opName = operacion.getLocalPart();
                if ("login".equalsIgnoreCase(opName) || "autenticadorLogin".equalsIgnoreCase(opName)) {
                    return true;
                }
            }

            // Verificación vía SOAP Body
            if (envelope != null && envelope.getBody() != null) {
                java.util.Iterator<?> it = envelope.getBody().getChildElements();
                while (it.hasNext()) {
                    Object obj = it.next();
                    if (obj instanceof SOAPElement) {
                        SOAPElement elem = (SOAPElement) obj;
                        String localName = elem.getLocalName();
                        if ("login".equalsIgnoreCase(localName) || "autenticadorLogin".equalsIgnoreCase(localName)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Continuar con la validación si falla la inspección del body
        }
        return false;
    }

    private void rechazar(String mensaje) {
        try {
            SOAPFault fault = SOAPFactory.newInstance().createFault(
                    mensaje,
                    new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Client")
            );
            throw new SOAPFaultException(fault);
        } catch (SOAPException e) {
            throw new ProtocolException(mensaje);
        }
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
        // Le indica al servidor SOAP que este handler requiere y procesa esta cabecera específica
        QName tokenHeader = new QName(Constantes.NAMESPACE, "Token");
        return Collections.singleton(tokenHeader);
    }
}