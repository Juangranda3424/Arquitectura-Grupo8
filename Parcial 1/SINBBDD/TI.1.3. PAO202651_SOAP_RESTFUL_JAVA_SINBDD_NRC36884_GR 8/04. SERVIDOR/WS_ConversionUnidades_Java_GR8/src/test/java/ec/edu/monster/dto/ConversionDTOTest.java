package ec.edu.monster.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ConversionDTOTest {

    @Test
    void constructorVacioDejaTodosLosCamposNulos() {
        ConversionDTO dto = new ConversionDTO();

        assertNull(dto.getValorOrigen());
        assertNull(dto.getValorResultado());
        assertNull(dto.getUnidadOrigen());
        assertNull(dto.getUnidadDestino());
        assertNull(dto.getTipoMagnitud());
    }

    @Test
    void constructorCompletoYAccesoresConservanLosValores() {
        ConversionDTO dto = new ConversionDTO(1.0, 1760.0, "MILLA", "YARDA", "LONGITUD");

        assertEquals(1.0, dto.getValorOrigen());
        assertEquals(1760.0, dto.getValorResultado());
        assertEquals("MILLA", dto.getUnidadOrigen());
        assertEquals("YARDA", dto.getUnidadDestino());
        assertEquals("LONGITUD", dto.getTipoMagnitud());
    }

    @Test
    void losSettersSobrescribenLosValores() {
        ConversionDTO dto = new ConversionDTO();
        dto.setValorOrigen(0.0);
        dto.setValorResultado(0.0);
        dto.setUnidadOrigen("CELSIUS");
        dto.setUnidadDestino("KELVIN");
        dto.setTipoMagnitud("TEMPERATURA");

        assertEquals(0.0, dto.getValorOrigen());
        assertEquals(0.0, dto.getValorResultado());
        assertEquals("CELSIUS", dto.getUnidadOrigen());
        assertEquals("KELVIN", dto.getUnidadDestino());
        assertEquals("TEMPERATURA", dto.getTipoMagnitud());
    }
}
