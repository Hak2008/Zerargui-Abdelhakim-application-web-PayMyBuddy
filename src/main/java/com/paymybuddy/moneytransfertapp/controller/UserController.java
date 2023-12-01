package com.paymybuddy.moneytransfertapp.controller;

import com.paymybuddy.moneytransfertapp.config.SecurityUtils;
import com.paymybuddy.moneytransfertapp.exception.UserAlreadyExistsException;
import com.paymybuddy.moneytransfertapp.exception.UserNotFoundException;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.service.BankAccountService;
import com.paymybuddy.moneytransfertapp.service.TransactionService;
import com.paymybuddy.moneytransfertapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final BankAccountService bankAccountService;

    @Autowired
    public UserController(UserService userService, BankAccountService bankAccountService) {
        this.userService = userService;
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
    public String logoutUser(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        SecurityUtils.logoutUser(request);

        redirectAttributes.addFlashAttribute("logoutMessage", "logout successfully!");

        return "redirect:/users/login";
    }

    @GetMapping("/register")
    public String showRegistrationPage(@ModelAttribute("user") User user) {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, Model model, RedirectAttributes redirectAttributes) {
        try {
            // Attempt to register the user
            userService.registerUser(user);
            // Create a BankAccount for the registered user
            bankAccountService.createBankAccount(user);

            redirectAttributes.addFlashAttribute("successMessage", "User registered successfully!");
            // Redirect to login page
            return "redirect:/users/login";
        } catch (UserAlreadyExistsException e) {
            model.addAttribute("registrationErrorMessage", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("registrationErrorMessage", "An error occurred while creating the account.");
        }
        // Return the registration page directly on error
        return "register";
    }

    @GetMapping("/add-friend")
    public String showAddFriendForm(HttpServletRequest request) {
        if (SecurityUtils.isUserLoggedIn(request)) {
            return "addFriend";
        }
        return "redirect:/users/login";
    }

    @PostMapping("/add-friend")
    public String addFriend(@RequestParam String friendEmail, HttpServletRequest request, Model model) {
        if (SecurityUtils.isUserLoggedIn(request)) {
            String userEmail = SecurityUtils.getLoggedInUserEmail(request);

            User user = userService.getUserByEmail(userEmail);
            User friend = userService.getUserByEmail(friendEmail);

            if (user != null && friend != null && !user.getFriends().contains(friend)) {
                userService.addFriend(user, friend);

                model.addAttribute("addFriendSuccessMessage", "Friend added successfully!");
            } else {
                // If it fails, error message
                model.addAttribute("addFriendErrorMessage", "Unable to add friend. Please check the email addresses.");
            }
            return "addFriend";
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
            Model model
    ) {
        try {
            userService.updateUserProfile(userId, user);
            // Success message
            model.addAttribute("successMessage", "Profile updated successfully!");
        } catch (UserNotFoundException e) {
            // Error message for UserNotFoundException
            model.addAttribute("errorMessage", e.getMessage());
        } catch (IllegalArgumentException e) {
            // Error message for IllegalArgumentException
            model.addAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            // Generic error message
            model.addAttribute("errorMessage", "Failed to update profile. Please try again.");
        }
        // Return to the "update" page after updating the profile
        return "update";
    }

    @PostMapping("/delete/{userId}")
    public String deleteProfile(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            // Delete the bank account associated with the user
            User user = userService.getUserById(userId);

            if (user != null && user.getBankAccount() != null) {
                bankAccountService.deleteBankAccount(user.getBankAccount().getAccountNumber());
            }
            // Delete user
            userService.deleteUser(userId);

            redirectAttributes.addFlashAttribute("successMessage", "Profile deleted successfully!");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users/login";
    }
}


