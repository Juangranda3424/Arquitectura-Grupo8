package ec.edu.monster.controlador;

import ec.edu.monster.modelo.Constantes;
import ec.edu.monster.util.ConfigLoader;
import ec.edu.monster.util.HeaderTokenSOAPHandler;

import javax.xml.ws.BindingProvider;

public class ConversorUnidades {

    public double ejecutarConversion(Double valorOrigen, String unidadOrigen, String unidadDestino, String tipoMagnitud) throws Exception {

        ec.edu.monster.webservice.WSConversorUnidades_Service service = new ec.edu.monster.webservice.WSConversorUnidades_Service();
        ec.edu.monster.webservice.WSConversorUnidades port = service.getWSConversorUnidadesPort();

        // 🔹 Inyectar URL dinámica desde el archivo config.properties
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, ConfigLoader.getEndpointConversor());

        // Inyección del Token en el Header SOAP
        HeaderTokenSOAPHandler.adjuntarTokenHandler(bp, Constantes.token);

        ec.edu.monster.webservice.ConversionDTO conversionDTO = new ec.edu.monster.webservice.ConversionDTO();
        conversionDTO.setTipoMagnitud(tipoMagnitud);
        conversionDTO.setValorOrigen(valorOrigen);
        conversionDTO.setUnidadOrigen(unidadOrigen);
        conversionDTO.setUnidadDestino(unidadDestino);

        return port.convertirUnidades(conversionDTO);
    }
}