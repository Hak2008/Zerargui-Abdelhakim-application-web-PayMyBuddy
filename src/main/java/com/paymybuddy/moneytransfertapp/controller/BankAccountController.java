package com.paymybuddy.moneytransfertapp.controller;

import com.paymybuddy.moneytransfertapp.config.SecurityUtils;
import com.paymybuddy.moneytransfertapp.model.BankAccount;
import com.paymybuddy.moneytransfertapp.service.BankAccountService;
import com.paymybuddy.moneytransfertapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bankaccounts")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/bankaccount")
    public String viewBankAccount(Model model, HttpServletRequest request) {
        // Obtain authentication of logged-in user
        if (SecurityUtils.isUserLoggedIn(request)) {
            String userEmail = SecurityUtils.getLoggedInUserEmail(request);
            // Recover the logged-in user's bank account
            BankAccount userBankAccount = bankAccountService.getUserBankAccount(userEmail);
            // Add the account to the model so that it is accessible in the view
            model.addAttribute("userBankAccount", userBankAccount);

            return "bankAccount";
        }
        // Redirect to login page if user is not authenticated
        return "redirect:/users/login";
    }
}



