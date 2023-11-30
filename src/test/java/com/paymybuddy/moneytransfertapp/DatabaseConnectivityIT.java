package com.paymybuddy.moneytransfertapp;

import com.paymybuddy.moneytransfertapp.model.BankAccount;
import com.paymybuddy.moneytransfertapp.model.Transaction;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.repository.BankAccountRepository;
import com.paymybuddy.moneytransfertapp.repository.TransactionRepository;
import com.paymybuddy.moneytransfertapp.repository.UserRepository;
import com.paymybuddy.moneytransfertapp.service.BankAccountService;
import com.paymybuddy.moneytransfertapp.service.TransactionService;
import com.paymybuddy.moneytransfertapp.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DatabaseConnectivityIT {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;


    @Test
    public void testDatabaseConnectivity() {
        assertThat(jdbcTemplate).isNotNull();


        int userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user", Integer.class);
        assertThat(userCount).isGreaterThanOrEqualTo(2);

        int transactionCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM transaction", Integer.class);
        assertThat(transactionCount).isGreaterThanOrEqualTo(2);

        int bankAccountCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM bankaccount", Integer.class);
        assertThat(bankAccountCount).isGreaterThanOrEqualTo(2);

        // Check relationships between entities
        List<User> users = userRepository.findAll();
        for (User user : users) {
            assertThat(user.getBankAccount()).isNotNull();
        }

        List<Transaction> transactions = transactionRepository.findAll();
        for (Transaction transaction : transactions) {
            assertThat(transaction.getSender()).isNotNull();
            assertThat(transaction.getReceiver()).isNotNull();
        }

        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        for (BankAccount bankAccount : bankAccounts) {
            assertThat(bankAccount.getUser()).isNotNull();
        }

    }
}

