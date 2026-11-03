package ec.edu.monster.banquito.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditApplicationRequestDto {
    private String identificationNumber;
    private BigDecimal amount;
    private int months;
}
