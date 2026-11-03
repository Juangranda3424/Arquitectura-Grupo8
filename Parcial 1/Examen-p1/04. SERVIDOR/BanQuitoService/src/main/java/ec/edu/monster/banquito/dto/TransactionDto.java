package ec.edu.monster.banquito.dto;

import ec.edu.monster.banquito.entity.MovementType;
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
    @NotEmpty
    private String bankAccountReference;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private MovementType type;
}
