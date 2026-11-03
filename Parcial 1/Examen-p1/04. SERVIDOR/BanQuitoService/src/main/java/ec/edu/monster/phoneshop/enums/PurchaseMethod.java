package ec.edu.monster.phoneshop.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PurchaseMethod {
    CASH("EFECTIVO"),
    CREDIT("CREDITO");

    private final String label;
}
