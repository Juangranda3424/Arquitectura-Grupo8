package ec.edu.monster.banquito.service;

import ec.edu.monster.banquito.dto.TransactionDto;

public interface TransactionService {
    void transfer(TransactionDto transaction);
}
