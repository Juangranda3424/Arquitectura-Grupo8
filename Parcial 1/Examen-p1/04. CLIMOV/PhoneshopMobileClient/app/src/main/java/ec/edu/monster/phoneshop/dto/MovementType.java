package ec.edu.monster.phoneshop.dto;

import lombok.Getter;

@Getter
public enum MovementType {
    DEPOSIT("DEPOSITO"),
    WITHDRAWAL("RETIRO"),
    TRANSFER("TRANSFERENCIA");

    private final String label;

    MovementType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
