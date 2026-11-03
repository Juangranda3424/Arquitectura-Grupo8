package ec.edu.monster.phoneshop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {
    private String token;
    private String message;
    private int code;
    private UserDto user;

    @Builder.Default
    private ResponseStatus status = ResponseStatus.SUCCESS;
}
