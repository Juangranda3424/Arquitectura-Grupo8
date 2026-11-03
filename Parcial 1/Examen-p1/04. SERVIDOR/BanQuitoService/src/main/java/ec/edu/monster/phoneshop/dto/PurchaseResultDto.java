package ec.edu.monster.phoneshop.dto;

import ec.edu.monster.banquito.dto.CreditApplicationResponseDto;
import ec.edu.monster.banquito.dto.ResponseStatus;
import ec.edu.monster.banquito.dto.UserDto;
import ec.edu.monster.phoneshop.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResultDto {
    private String message;
    private CreditApplicationResponseDto credit;
    private ResponseStatus status;
    private Product product;
    UserDto user;
}
