package ec.edu.monster.banquito.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    public interface Create {}
    public interface Update {}

    @NotNull(groups = { Update.class })
    private UUID id;

    @NotEmpty(groups = { Create.class, Update.class })
    @Pattern(regexp = "^[a-zA-Z0-9]{3,}$")
    private String username;

    @NotEmpty(groups = { Create.class, Update.class })
    @Pattern(regexp = "^[0-9]{10}$")
    private String identificationNumber;

    @NotEmpty(groups = { Create.class, Update.class })
    @Email
    private String email;

    @NotEmpty(groups = { Create.class, Update.class })
    @JsonIgnore
    private String password;

    @NotEmpty(groups = { Create.class, Update.class })
    private String passwordConfirmation;

    private UserProfileDto profile;

    @Builder.Default
    private List<BankAccountDto> bankAccounts = new ArrayList<>();
}
