package ec.edu.monster.banquito.dto;

import ec.edu.monster.banquito.entity.BankAccountType;
import ec.edu.monster.banquito.entity.MovementType;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovementDto {
    private UUID id;

    private MovementType type;

    @Min(0)
    private BigDecimal amount;

    private BigInteger number;

    private String reference;

    private BankAccountDto sourceAccount;

    private BankAccountDto targetAccount;

    private UserDto sender;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
