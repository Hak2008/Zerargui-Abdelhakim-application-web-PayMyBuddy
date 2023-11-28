package com.paymybuddy.moneytransfertapp.service;

import com.paymybuddy.moneytransfertapp.model.BankAccount;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.repository.BankAccountRepository;
import com.paymybuddy.moneytransfertapp.repository.TransactionRepository;
import com.paymybuddy.moneytransfertapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
@Slf4j
@Service
@RequiredArgsConstructor
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public BankAccount createBankAccount(User user) {
        // Generate a unique 9-digit account number randomly
        String accountNumber = generateAccountNumber();

        // Create a new BankAccount with an initial balance of 1000
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setUser(user);

        // Set initial balance to 1000
        bankAccount.setBalance(new BigDecimal("1000"));

        // Save the BankAccount to the database
        return bankAccountRepository.save(bankAccount);
    }

    private String generateAccountNumber() {
        Random random = new Random();
        int accountNumberLength = 9;
        StringBuilder accountNumber = new StringBuilder();

        // Generate account number digits
        for (int i = 0; i < accountNumberLength; i++) {
            int digit = random.nextInt(10);
            accountNumber.append(digit);
        }

        String generatedAccountNumber = accountNumber.toString();
        System.out.println("Generated Account Number: " + generatedAccountNumber);
        return generatedAccountNumber;
    }

    public BankAccount getUserBankAccount(String userEmail) {
        log.info("Getting bank account for user with email: {}", userEmail);

        User user = userRepository.findByEmail(userEmail);
        if (user != null) {
            Optional<BankAccount> optionalBankAccount = bankAccountRepository.findByUser_Email(userEmail);
            if (optionalBankAccount.isPresent()) {
                BankAccount bankAccount = optionalBankAccount.get();
                log.info("Bank account details: {}", bankAccount);
                return bankAccount;
            } else {
                log.warn("Bank account not found for user with email: {}", userEmail);
            }
        } else {
            log.warn("User not found with email: {}", userEmail);
        }

        return null;
    }


    @Transactional
    public BankAccount updateBankAccount(BankAccount bankAccount) {
        return bankAccountRepository.save(bankAccount);
    }


    @Transactional
    public void deleteBankAccount(String accountNumber) {
        // Delete associated transactions
        transactionRepository.deleteBySenderUser_AccountNumberOrReceiverUser_AccountNumber(accountNumber, accountNumber);

        // Delete bank account
        bankAccountRepository.deleteByAccountNumber(accountNumber);
    }

}