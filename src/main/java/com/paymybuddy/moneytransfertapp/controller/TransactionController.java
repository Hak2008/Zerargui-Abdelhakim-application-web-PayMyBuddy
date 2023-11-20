package com.paymybuddy.moneytransfertapp.controller;

import com.paymybuddy.moneytransfertapp.model.Transaction;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.service.TransactionService;
import com.paymybuddy.moneytransfertapp.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Slf4j
@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
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

    @GetMapping("/transactionview")
    public String showTransactionView(Model model, Principal principal) {
        if (principal != null) {
            String userEmail = principal.getName();
            log.info("User email from Principal: {}", userEmail);

            User user = userService.getUserByEmail(userEmail);
            List<Transaction> transactions = transactionService.getUserTransactions(user);

            model.addAttribute("transactions", transactions);
            log.info("Number of transactions for user {}: {}", userEmail, transactions.size());
        }

        return "transactionview";
    }
    @Data
    static class CreateTransactionRequest {
        private User sender;
        private User receiver;
        private BigDecimal amount;
        private String paymentReason;

    }
}
