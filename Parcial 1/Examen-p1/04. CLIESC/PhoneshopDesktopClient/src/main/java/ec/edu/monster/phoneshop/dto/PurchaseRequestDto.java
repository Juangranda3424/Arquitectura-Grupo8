package ec.edu.monster.phoneshop.dto;

import ec.edu.monster.phoneshop.dto.PurchaseMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDto {
    private UUID productId;
    private PurchaseMethod method;
    private int months;
    private String identificationNumber;
}
