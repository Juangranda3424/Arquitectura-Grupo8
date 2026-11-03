package ec.edu.monster.phoneshop.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {
    public interface Create {}
    public interface Update {}

    @NotNull(groups = { Update.class })
    private UUID id;

    @NotEmpty(groups = { Create.class, Update.class })
    @Pattern(regexp = "^[a-zA-Z0-9]{3,}$")
    private String username;

    @NotEmpty(groups = { Create.class, Update.class })
    @Email
    private String email;

    @NotEmpty(groups = { Create.class, Update.class })
    private String password;

    @NotEmpty(groups = { Create.class, Update.class })
    private String passwordConfirmation;

    private UserProfileDto profile;
}
