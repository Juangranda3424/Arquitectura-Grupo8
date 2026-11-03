package ec.edu.monster.banquito.dto;

import ec.edu.monster.banquito.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDto {
    private String token;
    private User user;
    private String message;
    private int code;

    @Builder.Default
    private ResponseStatus status = ResponseStatus.SUCCESS;
}
