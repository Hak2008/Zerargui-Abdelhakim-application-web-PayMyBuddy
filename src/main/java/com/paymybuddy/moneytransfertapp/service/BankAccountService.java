package com.paymybuddy.moneytransfertapp.service;

import com.paymybuddy.moneytransfertapp.model.BankAccount;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.repository.BankAccountRepository;
import com.paymybuddy.moneytransfertapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    @Transactional
    public BankAccount createBankAccount(User user, BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }

        BankAccount bankAccount = new BankAccount();
        bankAccount.setUser(user);
        bankAccount.setBalance(initialBalance);

        return bankAccountRepository.save(bankAccount);
    }

    @Transactional
    public void transferToBankAccount(User user, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid transfer amount.");
        }

        BankAccount bankAccount = user.getBankAccount();

        if (bankAccount == null) {
            throw new IllegalArgumentException("User does not have a bank account.");
        }

        BigDecimal currentBalance = bankAccount.getBalance();
        BigDecimal newBalance = currentBalance.add(amount);
        bankAccount.setBalance(newBalance);

        bankAccountRepository.save(bankAccount);
    }

    @Transactional
    public BankAccount updateBankAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }

}