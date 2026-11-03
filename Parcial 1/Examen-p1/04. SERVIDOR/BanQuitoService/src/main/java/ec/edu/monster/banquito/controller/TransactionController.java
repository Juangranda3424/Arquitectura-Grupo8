package ec.edu.monster.banquito.controller;

import ec.edu.monster.banquito.dto.TransactionDto;
import ec.edu.monster.banquito.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public void transfer(@Validated @RequestBody TransactionDto transaction) {
        transactionService.transfer(transaction);
    }
}
