package com.paymybuddy.moneytransfertapp.controller;

import com.paymybuddy.moneytransfertapp.model.BankAccount;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.service.BankAccountService;
import com.paymybuddy.moneytransfertapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/bank-accounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private UserService userService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService, UserService userService) {
        this.bankAccountService = bankAccountService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public BankAccount createBankAccount(@RequestParam Long userId, @RequestParam BigDecimal initialBalance) {
        User user = userService.getUserById(userId);
        return bankAccountService.createBankAccount(user, initialBalance);
    }

    @PostMapping("/transfer")
    public void transferToBankAccount(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        User user = userService.getUserById(userId);
        bankAccountService.transferToBankAccount(user, amount);
    }

    @PutMapping("/update")
    public BankAccount updateBankAccount(@Valid @RequestBody BankAccount bankAccount) {
        return bankAccountService.updateBankAccount(bankAccount);
    }
}

