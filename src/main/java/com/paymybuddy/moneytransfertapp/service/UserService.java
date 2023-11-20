package com.paymybuddy.moneytransfertapp.service;

import com.paymybuddy.moneytransfertapp.exception.UserAlreadyExistsException;
import com.paymybuddy.moneytransfertapp.exception.UserNotFoundException;
import com.paymybuddy.moneytransfertapp.model.Transaction;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.repository.TransactionRepository;
import com.paymybuddy.moneytransfertapp.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

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

        Optional<User> existingUserOptional = userRepository.findById(userId);  // Get existing user from database

        if (existingUserOptional.isEmpty()) {
            throw new UserNotFoundException("The user was not found.");
        }

        User existingUser = existingUserOptional.get();

        existingUser.setAddress(user.getAddress());// Updated user profile fields (address)
        existingUser.setPhoneNumber(user.getPhoneNumber());// Updated user profile fields (phone number)

        return userRepository.save(existingUser);// Save changes to database
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
