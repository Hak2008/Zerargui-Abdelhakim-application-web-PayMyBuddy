package com.paymybuddy.moneytransfertapp.service;

import com.paymybuddy.moneytransfertapp.exception.InsufficientBalanceException;
import com.paymybuddy.moneytransfertapp.model.Transaction;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.repository.TransactionRepository;
import com.paymybuddy.moneytransfertapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BankAccountService bankAccountService;


    @Transactional
    public Transaction createTransaction(User sender, User receiver, BigDecimal amount, String paymentReason) {
        log.info("Entering createTransaction method");
        // Check that the sender has sufficient funds
        BigDecimal senderBalance = sender.getBankAccount().getBalance();
        if (senderBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to complete the transaction.");
        }

        // Calculate fees (0.5% of amount)
        double feePercentage = 0.005;
        double fee = amount.doubleValue() * feePercentage;

        BigDecimal totalAmount = amount.add(BigDecimal.valueOf(fee)); // Total amount to transfer (amount + fees)

        // Create the transaction
        Transaction transaction = new Transaction();
        transaction.setSender(sender.getBankAccount());
        transaction.setReceiver(receiver.getBankAccount());
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transaction.setTotalAmount(totalAmount);
        transaction.setDate(new Date());
        transaction.setStatus("Completed");
        transaction.setPaymentReason(paymentReason);

        // Update account balances
        BigDecimal newSenderBalance = senderBalance.subtract(totalAmount);
        BigDecimal newReceiverBalance = receiver.getBankAccount().getBalance().add(amount);

        sender.getBankAccount().setBalance(newSenderBalance);
        receiver.getBankAccount().setBalance(newReceiverBalance);

        log.info("Creating transaction. Sender: {}, Beneficiary: {}, Amount: {}, Payment Reason: {}", sender.getEmail(), receiver.getEmail(), amount, paymentReason);
        log.info("Creating transaction for sender: {}, beneficiary: {}, amount: {}, paymentReason: {}", sender.getEmail(), receiver.getEmail(), amount, paymentReason);

        transactionRepository.save(transaction);// Save transaction
        log.info("Transaction saved. ID: {}", transaction.getId());

        // Updating bank accounts
        bankAccountService.updateBankAccount(sender.getBankAccount());
        bankAccountService.updateBankAccount(receiver.getBankAccount());

        return transaction;
    }

    public List<Transaction> getUserTransactions(String userEmail) {
        log.info("Getting transactions for user with email: {}", userEmail);

        User user = userRepository.findByEmail(userEmail);
        if (user != null) {
            List<Transaction> transactions = transactionRepository.findBySenderUserOrReceiverUser(user, user);
            log.info("Number of transactions retrieved: {}", transactions.size());

            for (Transaction transaction : transactions) {
                log.info("Transaction details: {}", transaction);
            }

            return transactions;
        } else {
            log.warn("User not found with email: {}", userEmail);
            return Collections.emptyList();
        }
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll(); //
    }

}
