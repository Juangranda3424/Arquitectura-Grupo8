package ec.edu.monster.banquito.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditApplicationResponseDto {
    private int totalInstallments;
    private BigDecimal monthlyInstallment;
    private BigDecimal totalAmount;
    private BigDecimal annualInterestRate;

    @Builder.Default
    private List<CreditInstallmentDto> installments = new ArrayList<>();
}
