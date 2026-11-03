package ec.edu.monster.banquito.dto;

import ec.edu.monster.banquito.entity.IdentificationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    private IdentificationType identificationType;

    private String identificationNumber;
    private String address;
    private String phoneNumber;
    private LocalDate birthDate;
    private String imageUrl;
}
