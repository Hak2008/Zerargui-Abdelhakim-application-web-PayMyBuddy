package com.paymybuddy.moneytransfertapp.service;

import com.paymybuddy.moneytransfertapp.exception.UserAlreadyExistsException;
import com.paymybuddy.moneytransfertapp.exception.UserNotFoundException;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.repository.UserRepository;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User registerUser(@Valid User user) {
        validateUserFields(user);

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new UserAlreadyExistsException("The email address is already registered.");
        }

        user.setPassword(user.getPassword());

        return userRepository.save(user);
    }


    @Transactional
    public void addFriend(User user, User friend) {
        if (user.getFriends() == null) {
            user.setFriends(new ArrayList<>());
        }

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

        // Update password only if the new password field is not empty
        if (StringUtils.isNotBlank(user.getNewPassword())) {
            // Check if the confirmPassword matches the newPassword
            if (user.getNewPassword().equals(user.getConfirmPassword())) {

                existingUser.setPassword(user.getNewPassword());
            } else {
                // Handle the case where newPassword and confirmPassword do not match
                throw new IllegalArgumentException("New password and confirm password do not match.");
            }
        }

        // Save the updated user to the database
        return userRepository.save(existingUser);
    }


    @Transactional
    public void deleteUser(Long userId) {
        Optional<User> existingUserOptional = userRepository.findById(userId);

        if (existingUserOptional.isEmpty()) {
            throw new UserNotFoundException("The user was not found.");
        }

        User existingUser = existingUserOptional.get();

        // Remove friendships associated with the user
        userRepository.deleteFriendshipsByUserId(userId);

        // Delete user
        userRepository.delete(existingUser);
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
