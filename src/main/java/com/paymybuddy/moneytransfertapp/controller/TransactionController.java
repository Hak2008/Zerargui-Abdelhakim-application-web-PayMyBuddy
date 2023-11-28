package com.paymybuddy.moneytransfertapp.controller;

import com.paymybuddy.moneytransfertapp.config.SecurityUtils;
import com.paymybuddy.moneytransfertapp.model.Transaction;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.service.TransactionService;
import com.paymybuddy.moneytransfertapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

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

    @GetMapping("/create")
    public String showCreateTransactionPage(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "createTransaction";
    }

    @PostMapping("/create")
    public String createTransaction(
            @RequestParam(name = "receiverEmail") String receiverEmail,
            @RequestParam(name = "amount") BigDecimal amount,
            @RequestParam(name = "paymentReason") String paymentReason,
            Model model,
            HttpServletRequest request
    ) {
        // Check if user is logged in
        if (SecurityUtils.isUserLoggedIn(request)) {
            try {
                // Retrieve logged in user's email
                String senderEmail = SecurityUtils.getLoggedInUserEmail(request);

                User sender = userService.getUserByEmail(senderEmail);
                User receiver = userService.getUserByEmail(receiverEmail);

                // Create the transaction
                transactionService.createTransaction(sender, receiver, amount, paymentReason);

                model.addAttribute("successMessage", "Transaction created successfully!");
                // Stay on the createTransaction page after transaction creation

            } catch (Exception e) {
                model.addAttribute("errorMessage", "Error creating transaction.");
                // Stay on the createTransaction page after encountering an error
              }
            return "createTransaction";

        } else {
            // Redirect to login page on error
            return "redirect:/users/login";
        }
    }



    @GetMapping("/transactionview")
    public String transactionView(Model model, HttpServletRequest request) {
        if (SecurityUtils.isUserLoggedIn(request)) {
            String userEmail = SecurityUtils.getLoggedInUserEmail(request);
            User user = userService.getUserByEmail(userEmail);
            List<Transaction> transactions = transactionService.getUserTransactions(userEmail);

            model.addAttribute("user", user);
            model.addAttribute("transactions", transactions);

            return "transactionView";
        }

        // Redirect to login page if user is not authenticated
        return "redirect:/users/login";
    }


}
