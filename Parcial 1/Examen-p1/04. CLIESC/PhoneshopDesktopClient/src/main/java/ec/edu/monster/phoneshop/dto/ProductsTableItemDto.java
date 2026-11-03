package ec.edu.monster.phoneshop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductsTableItemDto {
    private String code;
    private String name;
    private String price;
}
