package ec.edu.monster.controlador;

import ec.edu.monster.dto.ConversionDTO;
import ec.edu.monster.servicio.ConversorServicio;


public class ConversorUnidadesControlador {
    
    private final ConversorServicio conversorServicio = new ConversorServicio();
    
    public double conversor(ConversionDTO dto) {
        return conversorServicio.procesarConversion(dto).getValorResultado();
    }
    
}
