package com.paymybuddy.moneytransfertapp;

import com.paymybuddy.moneytransfertapp.exception.UserAlreadyExistsException;
import com.paymybuddy.moneytransfertapp.exception.UserNotFoundException;
import com.paymybuddy.moneytransfertapp.model.User;
import com.paymybuddy.moneytransfertapp.repository.UserRepository;
import com.paymybuddy.moneytransfertapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    public void setUp() {
        user = createUser();
    }

    @Test
    public void testRegisterUser_Success() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(null);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User registeredUser = userService.registerUser(user);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(user.getEmail(), registeredUser.getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testRegisterUser_UserAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail(user.getEmail())).thenReturn(user);

        // Act and Assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(user));
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testAddFriend_Success() {
        // Arrange
        user.setEmail("Henri@example.com");
        user.setFriends(null);

        User friend = createUser();
        friend.setEmail("user4@example.com");

        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        // Act
        userService.addFriend(user, friend);

        // Assert
        assertEquals(1, user.getFriends().size());
        verify(userRepository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    public void testUpdateUserProfile_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = createUser();
        updatedUser.setAddress("New Address");
        updatedUser.setPhoneNumber("333 333 333");
        updatedUser.setNewPassword("newPassword");
        updatedUser.setConfirmPassword("newPassword");

        // Act
        User result = userService.updateUserProfile(userId, updatedUser);

        // Assert
        assertEquals("New Address", result.getAddress());
        assertEquals("333 333 333", result.getPhoneNumber());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUserProfile_UserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User updatedUser = createUser();
        updatedUser.setAddress("New Address");

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUserProfile(userId, updatedUser));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUserProfile_PasswordMismatch() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User updatedUser = createUser();
        updatedUser.setNewPassword("newPassword");
        updatedUser.setConfirmPassword("wrongPassword");

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserProfile(userId, updatedUser));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeleteUser_Success() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteFriendshipsByUserId(userId);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteFriendshipsByUserId(userId);
        verify(userRepository, never()).delete(any(User.class));
    }

    private User createUser() {
        User user = new User();
        user.setEmail("user4@example.com");
        user.setPassword("123");
        user.setFirstName("Tom");
        user.setLastName("Henri");
        user.setDateOfBirth(new Date());
        return user;
    }
}

