package ec.edu.monster.phoneshop.dto;

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

    private BigDecimal amount;

    private BigInteger number;

    private String reference;

    private BankAccountDto targetAccount;

    private UserDto sender;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
