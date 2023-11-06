package com.paymybuddy.moneytransfertapp.repository;

import com.paymybuddy.moneytransfertapp.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}

