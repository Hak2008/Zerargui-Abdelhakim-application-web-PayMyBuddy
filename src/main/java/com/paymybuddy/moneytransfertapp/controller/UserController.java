package com.paymybuddy.moneytransfertapp.controller;

import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public User registerUser(@Valid @RequestBody User user) {
        return userService.registerUser(user);
    }

    @PostMapping("/add-friend")
    public void addFriend(@RequestParam Long userId, @RequestParam Long friendId) {
        User user = userService.getUserById(userId);
        User friend = userService.getUserById(friendId);
        userService.addFriend(user, friend);
    }

    @GetMapping("/friends")
    public List<User> getAllFriends(@RequestBody User user) {
        return userService.getAllFriends(user);
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


