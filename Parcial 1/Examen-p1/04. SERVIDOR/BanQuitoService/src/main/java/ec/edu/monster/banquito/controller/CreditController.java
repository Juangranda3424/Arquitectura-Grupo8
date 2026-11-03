package ec.edu.monster.banquito.controller;

import ec.edu.monster.banquito.dto.CheckCreditRequestDto;
import ec.edu.monster.banquito.dto.CheckCreditResultDto;
import ec.edu.monster.banquito.dto.CreditApplicationRequestDto;
import ec.edu.monster.banquito.dto.CreditApplicationResponseDto;
import ec.edu.monster.banquito.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public/credit")
@RequiredArgsConstructor
public class CreditController {
    private final CreditService creditService;

    @PostMapping("/check")
    public CheckCreditResultDto checkCredit(@RequestBody CheckCreditRequestDto request) {
        return creditService.checkCredit(request);
    }

    @PostMapping("/apply")
    public CreditApplicationResponseDto applyForCredit(@RequestBody CreditApplicationRequestDto request) {
        return creditService.applyForCredit(request);
    }
}
