package ec.edu.monster.phoneshop.dto;

import ec.edu.monster.phoneshop.enums.IdentificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto implements Serializable {
    private UUID id;
    private String firstName;
    private String lastName;
    private IdentificationType identificationType;
    private String identificationNumber;
    private String address;
    private String phoneNumber;
    private LocalDate birthDate;
    private String imageUrl;
}
