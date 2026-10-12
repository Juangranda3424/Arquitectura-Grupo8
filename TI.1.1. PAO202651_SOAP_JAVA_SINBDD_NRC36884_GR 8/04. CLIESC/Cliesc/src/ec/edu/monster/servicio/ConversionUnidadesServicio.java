/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.monster.servicio;

/**
 *
 * @author JUAN
 */
public class ConversionUnidadesServicio {

    public double convertirUnidades(double num1, double num2) {
        ec.edu.monster.webservicio.WebServiceConvertirUnidades_Service service = new ec.edu.monster.webservicio.WebServiceConvertirUnidades_Service();
        ec.edu.monster.webservicio.WebServiceConvertirUnidades port = service.getWebServiceConvertirUnidadesPort();
        return port.convertirUnidades(num1, num2);
    }
    
}
