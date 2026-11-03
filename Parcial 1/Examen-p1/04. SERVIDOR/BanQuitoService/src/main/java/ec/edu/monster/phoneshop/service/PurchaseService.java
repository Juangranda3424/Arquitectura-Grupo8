package ec.edu.monster.phoneshop.service;

import ec.edu.monster.phoneshop.dto.PurchaseRequestDto;
import ec.edu.monster.phoneshop.dto.PurchaseResultDto;

public interface PurchaseService {
    PurchaseResultDto purchase(PurchaseRequestDto request);
}
