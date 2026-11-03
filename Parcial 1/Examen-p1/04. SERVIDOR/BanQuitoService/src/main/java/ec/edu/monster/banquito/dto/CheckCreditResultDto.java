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
public class CheckCreditResultDto {
    private BigDecimal maxAmount;
    private boolean eligible;
    private String message;
}
