package ec.edu.monster.phoneshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    public interface Create {}
    public interface Update {}

    private UUID id;
    private String username;
    private String identificationNumber;
    private String email;
    private UserProfileDto profile;
}
