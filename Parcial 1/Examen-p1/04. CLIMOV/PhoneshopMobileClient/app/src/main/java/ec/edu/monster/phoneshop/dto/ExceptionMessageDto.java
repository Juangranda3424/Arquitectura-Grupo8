package ec.edu.monster.phoneshop.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class ExceptionMessageDto {
    private String title;
    private String type;
    private int status;
    private String detail;
    private String instance;
}
