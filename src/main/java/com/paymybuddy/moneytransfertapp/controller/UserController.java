package com.paymybuddy.moneytransfertapp.controller;


import com.paymybuddy.moneytransfertapp.config.SecurityUtils;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.service.BankAccountService;
import com.paymybuddy.moneytransfertapp.service.TransactionService;
import com.paymybuddy.moneytransfertapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final TransactionService transactionService;

    private final BankAccountService bankAccountService;

    @Autowired
    public UserController(UserService userService, TransactionService transactionService, BankAccountService bankAccountService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/login")
    public String showLoginPage(@ModelAttribute("user") User user) {
        return "login";
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String processLoginForm(@ModelAttribute("user") User user, Model model, HttpServletRequest request) {

        User existingUser = userService.getUserByEmail(user.getEmail());

        if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
            SecurityUtils.loginUser(request, existingUser.getEmail());

            return "redirect:/users/home";
        } else {
            model.addAttribute("loginError", true);
            return "login";
        }
    }

    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request) {
        if (SecurityUtils.isUserLoggedIn(request)) {
            String userEmail = SecurityUtils.getLoggedInUserEmail(request);
            User user = userService.getUserByEmail(userEmail);

            model.addAttribute("user", user);
        }
        return "home";
    }

    @PostMapping("/logout")
    public String logoutUser(HttpServletRequest request) {
        SecurityUtils.logoutUser(request);
        return "redirect:/users/login";
    }

    @GetMapping("/register")
    public String showRegistrationPage(@ModelAttribute("user") User user) {

        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, HttpSession session) {
        try {
            userService.registerUser(user);

            // Add a success message
            session.setAttribute("successMessage", "User registered successfully!");

            // Redirect to login page
            return "redirect:/users/login";
        } catch (Exception e) {
            // If it fails, add an error message
            session.setAttribute("registrationError", "An error occurred while creating the account.");

            return "redirect:/users/register";
        }
    }

    @GetMapping("/add-friend")
    public String showAddFriendForm(Model model, HttpServletRequest request) {
        if (SecurityUtils.isUserLoggedIn(request)) {

            return "addfriend";
        }

        return "redirect:/users/login";
    }

    @PostMapping("/add-friend")
    public String addFriend(@RequestParam String friendEmail, HttpServletRequest request, HttpSession session) {
        if (SecurityUtils.isUserLoggedIn(request)) {
            String userEmail = SecurityUtils.getLoggedInUserEmail(request);
            User user = userService.getUserByEmail(userEmail);
            User friend = userService.getUserByEmail(friendEmail);

            if (user != null && friend != null && !user.getFriends().contains(friend)) {
                userService.addFriend(user, friend);
                // Add a success message
                session.setAttribute("successMessage", "Friend added successfully!");
            } else {
                // If it fails, add an error message
                session.setAttribute("errorMessage", "Unable to add friend. Please check the email addresses.");
            }

            return "redirect:/users/home";
        }

        return "redirect:/users/login";
    }

    @GetMapping("/update")
    public String showUpdateProfileForm(Model model, HttpServletRequest request) {
        if (SecurityUtils.isUserLoggedIn(request)) {
            String userEmail = SecurityUtils.getLoggedInUserEmail(request);
            User user = userService.getUserByEmail(userEmail);
            model.addAttribute("user", user);
            return "update";
        } else {

            return "redirect:/users/login";
        }
    }

    @PostMapping("/update")
    public String updateUserProfile(
            @RequestParam(name = "id") Long userId,
            @Valid @ModelAttribute User user,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userService.updateUserProfile(userId, user);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            return "redirect:/users/home";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile. Please try again.");
            return "redirect:/users/update";
        }
    }


    @PostMapping("/delete/{userId}")
    public String deleteProfile(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        // Delete the bank account associated with the user
        User user = userService.getUserById(userId);
        if (user != null && user.getBankAccount() != null) {
            bankAccountService.deleteBankAccount(user.getBankAccount().getAccountNumber());
        }
        // Delete user
        userService.deleteUser(userId);

        redirectAttributes.addFlashAttribute("successMessage", "Profile deleted successfully!");
        return "redirect:/users/login";
    }

}


