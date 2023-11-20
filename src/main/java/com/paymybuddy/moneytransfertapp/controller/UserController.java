package com.paymybuddy.moneytransfertapp.controller;


import com.paymybuddy.moneytransfertapp.model.Transaction;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
@Slf4j
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage(@ModelAttribute("user") User user) {
        log.info("Showing login page");
        return "login";
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String processLoginForm(@ModelAttribute("user") User user, Model model, HttpServletRequest request) {
        User existingUser = userService.getUserByEmail(user.getEmail());

        if (existingUser != null && existingUser.getPassword().equals(user.getPassword())) {
            return "redirect:/transactions/transactionview";
        } else {
            model.addAttribute("loginError", true);
            return "login";
        }
    }
    @GetMapping("/register")
    public String showRegistrationPage(@ModelAttribute("user") User user) {
        log.info("Showing registration page");
        return "register";
    }
    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/users/login";
        } catch (Exception e) {
            // En cas d'échec, ajoutez un message d'erreur et renvoyez la vue de création de compte
            model.addAttribute("registrationError", "An error occurred while creating the account.");
            return "register";
        }
    }
    @GetMapping("/add-friend")
    public String showAddFriendPage(@ModelAttribute("user") User user) {
        return "addfriend";
    }

    @PostMapping("/add-friend")
    public String addFriend(@RequestParam String userEmail, @RequestParam String friendEmail) {
        log.info("Processing add friend request for user {} and friend {}", userEmail, friendEmail);
        User user = userService.getUserByEmail(userEmail);
        User friend = userService.getUserByEmail(friendEmail);

        if (user != null && friend != null && !user.getFriends().contains(friend)) {
            userService.addFriend(user, friend);
        }
        return "redirect:/transactions/transactionview";
    }


    @PutMapping("/update-profile")
    public User updateUserProfile(@RequestParam Long userId, @Valid @RequestBody User user) {
        return userService.updateUserProfile(userId, user);
    }

    @DeleteMapping("/delete/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}


