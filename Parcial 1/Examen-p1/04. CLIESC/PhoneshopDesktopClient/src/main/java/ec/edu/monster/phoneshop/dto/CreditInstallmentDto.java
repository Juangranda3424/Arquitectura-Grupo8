package ec.edu.monster.phoneshop.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditInstallmentDto {
    private int number;
    private BigDecimal amount;
    private BigDecimal interest;
    private BigDecimal capital;
    private BigDecimal balance;
}
