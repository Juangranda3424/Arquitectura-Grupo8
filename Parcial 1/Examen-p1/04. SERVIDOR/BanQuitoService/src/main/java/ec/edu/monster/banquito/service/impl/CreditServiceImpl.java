package ec.edu.monster.banquito.service.impl;

import ec.edu.monster.banquito.dto.*;
import ec.edu.monster.banquito.entity.*;
import ec.edu.monster.banquito.repository.BankAccountRepository;
import ec.edu.monster.banquito.repository.CreditRepository;
import ec.edu.monster.banquito.repository.MovementRepository;
import ec.edu.monster.banquito.repository.UserRepository;
import ec.edu.monster.banquito.service.CreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreditServiceImpl implements CreditService {
    private final CreditRepository creditRepository;
    private final MovementRepository movementRepository;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    @Override
    public CheckCreditResultDto checkCredit(CheckCreditRequestDto request) {
        String identificationNumber = request.getIdentificationNumber();
        User user = userRepository.findFirstByIdentificationNumber(identificationNumber).orElse(null);

        if (user == null) {
            return CheckCreditResultDto.builder()
                    .maxAmount(BigDecimal.ZERO)
                    .message("El número de identificación no está registrado en el sistema")
                    .eligible(false)
                    .build();
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastMonth = now.minusMonths(1);
        UserProfile profile = user.getProfile();
        int age = Period.between(profile.getBirthDate(), now.toLocalDate()).getYears();

        if (age < 25) {
            return CheckCreditResultDto.builder()
                    .maxAmount(BigDecimal.ZERO)
                    .message(String.format("La edad del usuario (%d años) no cumple con el requisito mínimo de 25 años", age))
                    .eligible(false)
                    .build();
        }

        List<Movement> deposits = movementRepository.findAllBySenderIdentificationNumberAndCreatedAtAfterAndType(
                identificationNumber, lastMonth, MovementType.DEPOSIT);
        List<Movement> withdrawals = movementRepository.findAllBySenderIdentificationNumberAndCreatedAtAfterAndType(
                identificationNumber, lastMonth, MovementType.WITHDRAWAL);

        Optional<Credit> credit = creditRepository.findFirstByUserAndPaidIsFalse(user);

        if (credit.isPresent()) {
            return CheckCreditResultDto.builder()
                    .maxAmount(BigDecimal.ZERO)
                    .message("El usuario ya tiene un crédito activo")
                    .eligible(false)
                    .build();
        }

        if (deposits.isEmpty()) {
            return CheckCreditResultDto.builder()
                    .maxAmount(BigDecimal.ZERO)
                    .message("No se encontraron depósitos en el último mes")
                    .eligible(false)
                    .build();
        }

        BigDecimal averageDeposit = deposits.stream()
                .map(Movement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(deposits.size()), RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal averageWithdrawal = withdrawals.isEmpty()
                ? BigDecimal.ZERO
                :withdrawals.stream()
                    .map(Movement::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(withdrawals.size()), RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.HALF_UP);

        BigDecimal maxAmount = (averageDeposit.subtract(averageWithdrawal).multiply(BigDecimal.valueOf(0.35))).multiply(BigDecimal.valueOf(0.6))
                .setScale(2, RoundingMode.HALF_UP);

        if (maxAmount.compareTo(request.getAmount()) < 0) {
            return CheckCreditResultDto.builder()
                    .maxAmount(maxAmount)
                    .message(String.format("El crédito solicitado (%s) supera el máximo permitido (%s)",
                            request.getAmount(),
                            maxAmount.setScale(0, RoundingMode.CEILING).setScale(2, RoundingMode.UNNECESSARY)))
                    .eligible(false)
                    .build();
        }

        return CheckCreditResultDto.builder()
                .maxAmount(maxAmount.setScale(0, RoundingMode.CEILING).setScale(2, RoundingMode.UNNECESSARY))
                .eligible(true)
                .build();
    }

    @Override
    public CreditApplicationResponseDto applyForCredit(CreditApplicationRequestDto request) {
        CheckCreditResultDto checkCreditResult = checkCredit(CheckCreditRequestDto.builder()
                .identificationNumber(request.getIdentificationNumber())
                .amount(request.getAmount())
                .build());

        if (!checkCreditResult.isEligible()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, checkCreditResult.getMessage());
        }

        if (request.getMonths() < 3 || request.getMonths() > 18) {
            throw new IllegalArgumentException("El plazo debe ser mayor o igual a 3 meses y menor o igual a 18 meses.");
        }

        BigDecimal loanAmount = request.getAmount(); // Monto del préstamo
        int numInstallments = request.getMonths(); // Número de cuotas
        BigDecimal annualRate = new BigDecimal("16.5").divide(new BigDecimal("100")); // Tasa de interés anual
        BigDecimal monthlyRate = annualRate.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP); // Tasa de interés mensual

        // Fórmula para calcular la cuota fija
        // C = (P * i) / (1 - (1 + i)^(-n))

        // Calcular (1 + i)
        BigDecimal onePlusRate = BigDecimal.ONE.add(monthlyRate);

        // Calcular (1 + i)^n
        BigDecimal onePlusRatePowN = onePlusRate.pow(numInstallments);

        // Calcular el denominador: 1 - (1 + i)^(-n)
        BigDecimal denominator = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(onePlusRatePowN, 10, RoundingMode.HALF_UP));

        // Calcular la cuota fija
        BigDecimal installment = loanAmount.multiply(monthlyRate).divide(denominator, 10, RoundingMode.HALF_UP);

        // Variables for the amortization table
        BigDecimal balance = loanAmount;
        List<CreditInstallmentDto> installments = new ArrayList<>();

        for (int month = 1; month <= numInstallments; month++) {
            // Calculate interest and principal payment
            BigDecimal interest = balance.multiply(monthlyRate).setScale(10, RoundingMode.HALF_UP);
            BigDecimal principalPayment = installment.subtract(interest).setScale(10, RoundingMode.HALF_UP);
            BigDecimal finalBalance = balance.subtract(principalPayment).setScale(10, RoundingMode.HALF_UP);

            installments.add(CreditInstallmentDto.builder()
                    .number(month)
                    .amount(installment.setScale(2, RoundingMode.HALF_UP))
                    .interest(interest.setScale(2, RoundingMode.HALF_UP))
                    .capital(principalPayment.setScale(2, RoundingMode.HALF_UP))
                    .balance(finalBalance.setScale(2, RoundingMode.HALF_UP))
                    .build());

            balance = finalBalance;
        }

        Credit credit = Credit.builder()
                .amount(request.getAmount())
                .interestRate(annualRate)
                .totalInstallments(request.getMonths())
                .installmentAmount(installment)
                .user(userRepository.findFirstByIdentificationNumber(request.getIdentificationNumber()).orElse(null))
                .paid(false)
                .build();
        //creditRepository.save(credit);

        return CreditApplicationResponseDto.builder()
                .totalAmount(request.getAmount().setScale(2, RoundingMode.HALF_UP))
                .totalInstallments(request.getMonths())
                .annualInterestRate(annualRate.multiply(BigDecimal.valueOf(100)))
                .monthlyInstallment(installment.setScale(2, RoundingMode.HALF_UP))
                .installments(installments)
                .build();
    }
}
