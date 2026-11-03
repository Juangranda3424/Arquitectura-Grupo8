package ec.edu.monster.phoneshop.common;

import ec.edu.monster.phoneshop.dto.ExceptionMessageDto;

public class ApiServerException extends RuntimeException {
    private final ExceptionMessageDto error;

    public ApiServerException(ExceptionMessageDto error) {
        super(error.getDetail());
        this.error = error;
    }

    public ExceptionMessageDto getError() {
        return error;
    }
}
