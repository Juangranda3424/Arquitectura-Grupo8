package ec.edu.espe.banquito.exception;

import org.springframework.http.HttpStatus;

public class SolicitudInvalidaException extends ApiException {

    public SolicitudInvalidaException(String codigo, String mensaje) {
        super(HttpStatus.BAD_REQUEST, codigo, mensaje);
    }
}
