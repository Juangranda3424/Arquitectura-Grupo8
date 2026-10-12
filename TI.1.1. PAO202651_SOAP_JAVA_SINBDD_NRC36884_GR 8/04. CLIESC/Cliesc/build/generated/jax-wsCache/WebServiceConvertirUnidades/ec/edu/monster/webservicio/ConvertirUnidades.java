
package ec.edu.monster.webservicio;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para convertirUnidades complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="convertirUnidades"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="num1" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="num2" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "convertirUnidades", propOrder = {
    "num1",
    "num2"
})
public class ConvertirUnidades {

    protected double num1;
    protected double num2;

    /**
     * Obtiene el valor de la propiedad num1.
     * 
     */
    public double getNum1() {
        return num1;
    }

    /**
     * Define el valor de la propiedad num1.
     * 
     */
    public void setNum1(double value) {
        this.num1 = value;
    }

    /**
     * Obtiene el valor de la propiedad num2.
     * 
     */
    public double getNum2() {
        return num2;
    }

    /**
     * Define el valor de la propiedad num2.
     * 
     */
    public void setNum2(double value) {
        this.num2 = value;
    }

}
