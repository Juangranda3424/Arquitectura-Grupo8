package ec.edu.monster.phoneshop.dto;

import ec.edu.monster.phoneshop.enums.MovementType;
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

    private BankAccountDto targetAccount;

    private UserDto sender;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
