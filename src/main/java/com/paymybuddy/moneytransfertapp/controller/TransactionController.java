package com.paymybuddy.moneytransfertapp.controller;

import com.paymybuddy.moneytransfertapp.model.Transaction;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.service.TransactionService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/create")
    public Transaction createTransaction(@RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(
                request.getSender(),
                request.getReceiver(),
                request.getAmount(),
                request.getPaymentReason()
        );
    }

    @GetMapping("/all")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @Data
    static class CreateTransactionRequest {
        private User sender;
        private User receiver;
        private BigDecimal amount;
        private String paymentReason;

    }
}
