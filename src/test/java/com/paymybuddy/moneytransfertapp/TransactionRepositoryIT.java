package com.paymybuddy.moneytransfertapp;

import com.paymybuddy.moneytransfertapp.model.Transaction;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.repository.TransactionRepository;
import com.paymybuddy.moneytransfertapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class TransactionRepositoryIT {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndRetrieveTransaction() {
        // Create a sender user
        User sender = new User();
        sender.setEmail("sender@example.com");
        sender.setPassword("password");
        sender.setFirstName("Sender");
        sender.setLastName("User");
        sender.setDateOfBirth(new Date());
        userRepository.save(sender);

        // Create a receiver user
        User receiver = new User();
        receiver.setEmail("receiver@example.com");
        receiver.setPassword("password");
        receiver.setFirstName("Receiver");
        receiver.setLastName("User");
        receiver.setDateOfBirth(new Date());
        userRepository.save(receiver);

        // Create a transaction
        Transaction transaction = new Transaction();
        transaction.setSender(sender.getBankAccount());
        transaction.setReceiver(receiver.getBankAccount());
        transaction.setAmount(new BigDecimal("100.00"));
        transaction.setFee(0.5);
        transaction.setTotalAmount(new BigDecimal("100.50"));
        transaction.setDate(new Date());
        transaction.setStatus("Completed");
        transaction.setPaymentReason("Test payment");

        transactionRepository.save(transaction);

        // Retrieve the saved transaction
        Transaction savedTransaction = transactionRepository.findById(transaction.getId()).orElse(null);

        assertThat(savedTransaction).isNotNull();
        assertThat(savedTransaction.getAmount()).isEqualTo(new BigDecimal("100.00"));
        assertThat(savedTransaction.getFee()).isEqualTo(0.5);
        assertThat(savedTransaction.getTotalAmount()).isEqualTo(new BigDecimal("100.50"));
        assertThat(savedTransaction.getStatus()).isEqualTo("Completed");
        assertThat(savedTransaction.getPaymentReason()).isEqualTo("Test payment");
    }

    @After
    public void cleanup() {
        // Cleanup logic
        transactionRepository.deleteAll();
        userRepository.deleteAll();
    }
}
