package ec.edu.monster.phoneshop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {
    private String token;
    private UserDto user;
    private String message;
    private int code;

    @Builder.Default
    private ResponseStatus status = ResponseStatus.SUCCESS;
}
