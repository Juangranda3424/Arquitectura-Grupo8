package ec.edu.monster.phoneshop.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
public class AmortizationTableItemDto {
    private String number;
    private String installmentValue;
    private String interest;
    private String capital;
    private String balance;
}
