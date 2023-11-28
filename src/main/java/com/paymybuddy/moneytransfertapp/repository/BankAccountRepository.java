package com.paymybuddy.moneytransfertapp.repository;

import com.paymybuddy.moneytransfertapp.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    void deleteByAccountNumber(String accountNumber);

    Optional<BankAccount> findByUser_Email(String email);
}

