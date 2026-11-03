package ec.edu.monster.phoneshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public enum MovementType {
    DEPOSIT("DEPOSITO"),
    WITHDRAWAL("RETIRO");

    private final String label;

    MovementType(String label) {
        this.label = label;
    }
}
