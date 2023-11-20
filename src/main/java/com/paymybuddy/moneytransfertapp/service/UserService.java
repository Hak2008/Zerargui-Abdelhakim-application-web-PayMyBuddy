package com.paymybuddy.moneytransfertapp.service;

import com.paymybuddy.moneytransfertapp.config.PasswordService;
import com.paymybuddy.moneytransfertapp.exception.UserAlreadyExistsException;
import com.paymybuddy.moneytransfertapp.exception.UserNotFoundException;
import com.paymybuddy.moneytransfertapp.model.Transaction;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.repository.TransactionRepository;
import com.paymybuddy.moneytransfertapp.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordService passwordService;

    @Transactional
    public User registerUser(@Valid User user) {
        validateUserFields(user);

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserAlreadyExistsException("The email address is already registered.");
        }

        return userRepository.save(user);
    }


    @Transactional
    public void addFriend(User user, User friend) {

        user.getFriends().add(friend);

        userRepository.saveAndFlush(user);
    }

    @Transactional
    public User updateUserProfile(Long userId, User user) {
        Optional<User> existingUserOptional = userRepository.findById(userId);

        if (existingUserOptional.isEmpty()) {
            throw new UserNotFoundException("The user was not found.");
        }

        User existingUser = existingUserOptional.get();

        existingUser.setAddress(user.getAddress());
        existingUser.setPhoneNumber(user.getPhoneNumber());

        // Update password only if new password field is not empty
        if (StringUtils.isNotBlank(user.getNewPassword()) && user.getNewPassword().equals(user.getConfirmPassword())) {
            existingUser.setPassword(passwordService.hashPassword(user.getNewPassword()));
        }

        // Save the updated user to the database
        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteUser(Long userId) {

        Optional<User> existingUserOptional = userRepository.findById(userId);// Get the existing user from the database

        if (existingUserOptional.isEmpty()) {
            throw new UserNotFoundException("The user was not found.");
        }

        User existingUser = existingUserOptional.get();

        userRepository.delete(existingUser);// Delete user and associated friendships
    }

    public User getUserByEmail(String userEmail) {
        return userRepository.findByEmail(userEmail);
    }

    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new UserNotFoundException("The user was not found.");
        }
    }



    private void validateUserFields(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Invalid user.");
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email address is required.");
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("The password is required.");
        }

        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("The first name is required.");
        }

        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name is required.");
        }

        if (user.getDateOfBirth() == null) {
            throw new IllegalArgumentException("The date of birth is required.");
        }
    }
}
