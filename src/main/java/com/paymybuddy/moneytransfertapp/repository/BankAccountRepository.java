package com.paymybuddy.moneytransfertapp.repository;

import com.paymybuddy.moneytransfertapp.model.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {

}

