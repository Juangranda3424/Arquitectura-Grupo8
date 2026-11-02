package ec.edu.espe.banquito.exception;

import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import ec.edu.espe.banquito.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErrorResponse> api(ApiException e) {
        ErrorResponse body = e instanceof CreditoRechazadoException r
                ? new ErrorResponse(r.getCodigo(), r.getMessage(), r.getMotivos().isEmpty() ? null : r.getMotivos(),
                        r.getMontoMaximo())
                : new ErrorResponse(e.getCodigo(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> invalid(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return badRequest(mensaje);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class})
    ResponseEntity<ErrorResponse> unreadable(Exception e) {
        return badRequest("Solicitud mal formada");
    }

    private static ResponseEntity<ErrorResponse> badRequest(String mensaje) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("SOLICITUD_INVALIDA", mensaje));
    }
}
