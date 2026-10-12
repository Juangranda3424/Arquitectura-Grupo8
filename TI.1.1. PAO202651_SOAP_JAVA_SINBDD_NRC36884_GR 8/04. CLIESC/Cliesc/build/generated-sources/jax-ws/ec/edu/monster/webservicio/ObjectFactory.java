
package ec.edu.monster.webservicio;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ec.edu.monster.webservicio package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ConvertirUnidades_QNAME = new QName("http://webservice.monster.edu.ec/", "convertirUnidades");
    private final static QName _ConvertirUnidadesResponse_QNAME = new QName("http://webservice.monster.edu.ec/", "convertirUnidadesResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ec.edu.monster.webservicio
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ConvertirUnidades }
     * 
     */
    public ConvertirUnidades createConvertirUnidades() {
        return new ConvertirUnidades();
    }

    /**
     * Create an instance of {@link ConvertirUnidadesResponse }
     * 
     */
    public ConvertirUnidadesResponse createConvertirUnidadesResponse() {
        return new ConvertirUnidadesResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConvertirUnidades }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConvertirUnidades }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservice.monster.edu.ec/", name = "convertirUnidades")
    public JAXBElement<ConvertirUnidades> createConvertirUnidades(ConvertirUnidades value) {
        return new JAXBElement<ConvertirUnidades>(_ConvertirUnidades_QNAME, ConvertirUnidades.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ConvertirUnidadesResponse }{@code >}
     * 
     * @param value
     *     Java instance representing xml element's value.
     * @return
     *     the new instance of {@link JAXBElement }{@code <}{@link ConvertirUnidadesResponse }{@code >}
     */
    @XmlElementDecl(namespace = "http://webservice.monster.edu.ec/", name = "convertirUnidadesResponse")
    public JAXBElement<ConvertirUnidadesResponse> createConvertirUnidadesResponse(ConvertirUnidadesResponse value) {
        return new JAXBElement<ConvertirUnidadesResponse>(_ConvertirUnidadesResponse_QNAME, ConvertirUnidadesResponse.class, null, value);
    }

}
