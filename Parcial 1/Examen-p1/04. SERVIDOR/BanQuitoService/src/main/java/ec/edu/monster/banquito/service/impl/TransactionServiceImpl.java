package ec.edu.monster.banquito.service.impl;

import ec.edu.monster.banquito.dto.TransactionDto;
import ec.edu.monster.banquito.entity.*;
import ec.edu.monster.banquito.repository.BankAccountRepository;
import ec.edu.monster.banquito.repository.MovementRepository;
import ec.edu.monster.banquito.service.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final BankAccountRepository bankAccountRepository;
    private final MovementRepository movementRepository;

    @Override
    @Transactional
    public void transfer(TransactionDto transaction) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        BankAccount sourceAccount = bankAccountRepository.findFirstByUserIdAndType(currentUser.getId(), BankAccountType.SAVINGS)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        BankAccount targetAccount = bankAccountRepository.findFirstByReference(transaction.getBankAccountReference())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

        if (transaction.getType() == MovementType.WITHDRAWAL) {
            if (!targetAccount.getUser().getId().equals(currentUser.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes retirar de una cuenta que no es tuya");
            }

            if (targetAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fondos insuficientes");
            }

            targetAccount.setBalance(targetAccount.getBalance().subtract(transaction.getAmount()));
            sourceAccount = null;
        } else if (transaction.getType() == MovementType.TRANSFER
            || (transaction.getType() == MovementType.DEPOSIT && !targetAccount.getUser().getId().equals(currentUser.getId()))) {
            if (transaction.getType() == MovementType.TRANSFER && sourceAccount.getId().equals(targetAccount.getId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se puede transferir a la misma cuenta");
            }

            if (sourceAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fondos insuficientes");
            }

            sourceAccount.setBalance(sourceAccount.getBalance().subtract(transaction.getAmount()));
            targetAccount.setBalance(targetAccount.getBalance().add(transaction.getAmount()));
        } else {
            targetAccount.setBalance(targetAccount.getBalance().add(transaction.getAmount()));
            sourceAccount = null;
        }

        Movement movement = Movement.builder()
                .amount(transaction.getAmount())
                .targetAccount(targetAccount)
                .sourceAccount(sourceAccount)
                .sender(currentUser)
                .type(transaction.getType())
                .build();

        bankAccountRepository.save(targetAccount);

        if (sourceAccount != null) {
            bankAccountRepository.save(sourceAccount);
        }

        movementRepository.save(movement);
    }
}
