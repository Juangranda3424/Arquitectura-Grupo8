package ec.edu.monster.banquito.service;

import ec.edu.monster.banquito.dto.CheckCreditRequestDto;
import ec.edu.monster.banquito.dto.CheckCreditResultDto;
import ec.edu.monster.banquito.dto.CreditApplicationRequestDto;
import ec.edu.monster.banquito.dto.CreditApplicationResponseDto;

public interface CreditService {
    CheckCreditResultDto checkCredit(CheckCreditRequestDto request);
    CreditApplicationResponseDto applyForCredit(CreditApplicationRequestDto request);
}
