package ec.edu.espe.banquito.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

    private final String codigo;
    private final HttpStatus status;

    protected ApiException(HttpStatus status, String codigo, String mensaje) {
        super(mensaje);
        this.status = status;
        this.codigo = codigo;
    }

    public String getCodigo() { return codigo; }
    public HttpStatus getStatus() { return status; }
}
