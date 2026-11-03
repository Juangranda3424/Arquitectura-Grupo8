package ec.edu.monster.phoneshop.service;

import ec.edu.monster.phoneshop.dto.CheckCreditRequestDto;
import ec.edu.monster.phoneshop.dto.CheckCreditResultDto;

public interface CreditService {
    CheckCreditResultDto checkCredit(CheckCreditRequestDto request);
}
