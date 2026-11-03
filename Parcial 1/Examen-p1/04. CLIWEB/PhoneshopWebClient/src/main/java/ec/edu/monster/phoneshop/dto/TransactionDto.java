package ec.edu.monster.phoneshop.dto;

import ec.edu.monster.phoneshop.enums.MovementType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
    private String bankAccountReference;
    private BigDecimal amount;
    private MovementType type;
}
