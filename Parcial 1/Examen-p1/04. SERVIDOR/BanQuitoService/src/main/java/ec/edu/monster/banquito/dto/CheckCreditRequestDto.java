package ec.edu.monster.banquito.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckCreditRequestDto {
    @NotEmpty
    private String identificationNumber;
    private BigDecimal amount;
}
