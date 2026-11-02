package ec.edu.espe.banquito.exception;

import org.springframework.http.HttpStatus;

public class RecursoNoEncontradoException extends ApiException {

    public RecursoNoEncontradoException(String codigo, String mensaje) {
        super(HttpStatus.NOT_FOUND, codigo, mensaje);
    }
}
