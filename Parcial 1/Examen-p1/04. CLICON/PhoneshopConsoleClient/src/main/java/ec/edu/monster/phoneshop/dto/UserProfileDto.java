package ec.edu.monster.phoneshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private IdentificationType identificationType;
    private String identificationNumber;
    private String address;
    private String phoneNumber;
    private String birthDate;
    private String imageUrl;
}
