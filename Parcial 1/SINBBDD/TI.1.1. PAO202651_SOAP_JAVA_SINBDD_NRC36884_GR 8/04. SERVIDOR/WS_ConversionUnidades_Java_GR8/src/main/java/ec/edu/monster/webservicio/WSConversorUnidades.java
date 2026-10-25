package ec.edu.monster.webservicio;

import ec.edu.monster.controlador.ConversorUnidadesControlador;
import ec.edu.monster.dto.ConversionDTO;
import jakarta.jws.WebService;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.HandlerChain;

@WebService(serviceName = "WSConversorUnidades")
@HandlerChain(file = "handlers.xml")
public class WSConversorUnidades {

    private final ConversorUnidadesControlador controlador = new ConversorUnidadesControlador();

    /**
     * Operación del Web Service SOAP para realizar conversiones.
     */
    @WebMethod(operationName = "convertir-unidades")
    public double convertirUnidades(@WebParam(name = "peticionConversion") ConversionDTO dto) {
        return controlador.conversor(dto);
    }
}