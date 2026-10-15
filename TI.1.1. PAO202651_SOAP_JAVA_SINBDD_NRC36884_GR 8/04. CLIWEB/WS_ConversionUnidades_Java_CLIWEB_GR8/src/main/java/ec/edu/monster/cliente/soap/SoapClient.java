package ec.edu.monster.cliente.soap;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Cliente SOAP 1.1 mínimo (document/literal wrapped), sin dependencias externas.
 *
 * Arma el envelope a mano, lo envía con java.net.http y extrae el valor de
 * &lt;return&gt; o el faultstring. Así no hace falta generar stubs con wsimport
 * y el cliente compila aunque el servidor no esté levantado.
 */
public final class SoapClient {

    private static final String NS_ENVELOPE = "http://schemas.xmlsoap.org/soap/envelope/";

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    /** targetNamespace descubierto en el WSDL, por endpoint. */
    private final Map<String, String> namespaces = new ConcurrentHashMap<>();

    private final String namespaceTokenHeader;
    private final String namespacePorDefecto;

    /**
     * @param namespaceTokenHeader namespace del elemento &lt;Token&gt; del header SOAP
     * @param namespacePorDefecto  namespace de las operaciones si el WSDL no se puede leer
     */
    public SoapClient(String namespaceTokenHeader, String namespacePorDefecto) {
        this.namespaceTokenHeader = namespaceTokenHeader;
        this.namespacePorDefecto = namespacePorDefecto;
    }

    /**
     * Invoca una operación SOAP y devuelve el texto del elemento &lt;return&gt;.
     *
     * @param endpoint      URL del servicio (sin ?wsdl)
     * @param operacion     nombre de la operación (ej.: "login", "convertir-unidades")
     * @param token         token para el header SOAP, o null si la operación no lo requiere
     * @param parametrosXml hijos del elemento de la operación, ya serializados
     */
    public String invocar(String endpoint, String operacion, String token, String parametrosXml)
            throws SoapException {

        String ns = namespaceDe(endpoint);

        StringBuilder sb = new StringBuilder(256);
        sb.append("<soapenv:Envelope xmlns:soapenv=\"").append(NS_ENVELOPE).append("\">");
        sb.append("<soapenv:Header>");
        if (token != null && !token.isBlank()) {
            sb.append("<tk:Token xmlns:tk=\"").append(escapar(namespaceTokenHeader)).append("\">")
              .append(escapar(token)).append("</tk:Token>");
        }
        sb.append("</soapenv:Header><soapenv:Body>");
        sb.append("<ns:").append(operacion).append(" xmlns:ns=\"").append(escapar(ns)).append("\">")
          .append(parametrosXml)
          .append("</ns:").append(operacion).append(">");
        sb.append("</soapenv:Body></soapenv:Envelope>");

        HttpRequest peticion = HttpRequest.newBuilder(URI.create(endpoint))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", "\"\"")
                .POST(HttpRequest.BodyPublishers.ofString(sb.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<byte[]> respuesta = enviar(peticion);
        Document doc = parsear(respuesta.body());
        if (doc == null) {
            throw new SoapException(SoapException.Tipo.RESPUESTA_INVALIDA,
                    "El servidor respondió algo que no es SOAP (HTTP " + respuesta.statusCode()
                    + "). Verifica la URL del servicio: " + endpoint);
        }

        // ¿SOAP Fault?
        NodeList fallos = doc.getElementsByTagNameNS(NS_ENVELOPE, "Fault");
        if (fallos.getLength() > 0) {
            Element fallo = (Element) fallos.item(0);
            NodeList msg = fallo.getElementsByTagName("faultstring");
            String texto = msg.getLength() > 0
                    ? msg.item(0).getTextContent().trim()
                    : "Error desconocido del servicio.";
            throw new SoapException(SoapException.Tipo.FALLO_SERVICIO, texto);
        }

        NodeList retornos = doc.getElementsByTagNameNS("*", "return");
        if (retornos.getLength() == 0) {
            // Un String null llega sin <return>.
            if (respuesta.statusCode() == 200) {
                return "";
            }
            throw new SoapException(SoapException.Tipo.RESPUESTA_INVALIDA,
                    "Respuesta SOAP sin <return> (HTTP " + respuesta.statusCode() + ").");
        }
        return retornos.item(0).getTextContent().trim();
    }

    /** Escapa texto para colocarlo dentro de un nodo o atributo XML. */
    public static String escapar(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }

    /** Atajo: &lt;nombre&gt;valor&lt;/nombre&gt; con el valor escapado. */
    public static String elemento(String nombre, String valor) {
        return "<" + nombre + ">" + escapar(valor) + "</" + nombre + ">";
    }

    // ------------------------------------------------------------------ interno

    private HttpResponse<byte[]> enviar(HttpRequest peticion) throws SoapException {
        try {
            return http.send(peticion, HttpResponse.BodyHandlers.ofByteArray());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SoapException(SoapException.Tipo.SIN_CONEXION, "Petición interrumpida.", e);
        } catch (IOException e) {
            throw new SoapException(SoapException.Tipo.SIN_CONEXION,
                    "No se pudo conectar con el servidor SOAP (" + peticion.uri()
                    + "). Revisa que Payara esté encendido y el servicio desplegado.", e);
        }
    }

    /** Lee el targetNamespace del WSDL; si falla, usa el configurado. */
    private String namespaceDe(String endpoint) {
        String cacheado = namespaces.get(endpoint);
        if (cacheado != null) {
            return cacheado;
        }
        try {
            HttpRequest req = HttpRequest.newBuilder(URI.create(endpoint + "?wsdl"))
                    .timeout(Duration.ofSeconds(5)).GET().build();
            HttpResponse<byte[]> r = http.send(req, HttpResponse.BodyHandlers.ofByteArray());
            Document wsdl = r.statusCode() == 200 ? parsear(r.body()) : null;
            if (wsdl != null) {
                String tns = wsdl.getDocumentElement().getAttribute("targetNamespace");
                if (!tns.isBlank()) {
                    namespaces.put(endpoint, tns);
                    return tns;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IOException | RuntimeException e) {
            // se usa el namespace por defecto
        }
        return namespacePorDefecto;
    }

    private static Document parsear(byte[] xml) {
        if (xml == null || xml.length == 0) {
            return null;
        }
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setNamespaceAware(true);
            f.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            f.setXIncludeAware(false);
            f.setExpandEntityReferences(false);
            DocumentBuilder b = f.newDocumentBuilder();
            return b.parse(new ByteArrayInputStream(xml));
        } catch (Exception e) {
            return null;
        }
    }
}
