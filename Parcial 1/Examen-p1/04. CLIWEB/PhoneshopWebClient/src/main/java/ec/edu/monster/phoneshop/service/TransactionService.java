package ec.edu.monster.phoneshop.service;

import ec.edu.monster.phoneshop.dto.TransactionDto;

public interface TransactionService {
    void transfer(TransactionDto transaction) throws Exception;
}
