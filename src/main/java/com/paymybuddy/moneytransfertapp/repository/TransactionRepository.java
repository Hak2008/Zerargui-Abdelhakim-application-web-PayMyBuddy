package com.paymybuddy.moneytransfertapp.repository;

import com.paymybuddy.moneytransfertapp.model.Transaction;
import com.paymybuddy.moneytransfertapp.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySenderUserOrReceiverUser(User sender, User receiver);
    @Modifying
    @Transactional
    void deleteBySenderUser_AccountNumberOrReceiverUser_AccountNumber(String senderAccountNumber, String receiverAccountNumber);
}
