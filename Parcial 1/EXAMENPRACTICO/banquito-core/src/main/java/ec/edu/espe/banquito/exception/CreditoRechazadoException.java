package ec.edu.espe.banquito.exception;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;

import ec.edu.espe.banquito.dto.Motivo;

/** The request is valid but the bank does not approve it (422). */
public class CreditoRechazadoException extends ApiException {

    private final List<Motivo> motivos;
    private final BigDecimal montoMaximo;

    public CreditoRechazadoException(String codigo, String mensaje, List<Motivo> motivos, BigDecimal montoMaximo) {
        super(HttpStatus.UNPROCESSABLE_CONTENT, codigo, mensaje);
        this.motivos = List.copyOf(motivos);
        this.montoMaximo = montoMaximo;
    }

    public List<Motivo> getMotivos() { return motivos; }
    public BigDecimal getMontoMaximo() { return montoMaximo; }
}
