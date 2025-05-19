package com.example.taskmanager.service;

import com.example.taskmanager.model.User;
import com.example.taskmanager.storage.UserStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserStorage userStorage;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userStorage);
    }

    @Test
    void registerUser_ShouldSaveAndReturnUser() {
        // Arrange
        User user = new User("testuser", "test@example.com");
        when(userStorage.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userStorage.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.registerUser(user);

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userStorage).save(user);
    }

    @Test
    void registerUser_ShouldThrowException_WhenUsernameExists() {
        // Arrange
        User user = new User("testuser", "test@example.com");
        when(userStorage.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.registerUser(user));
        verify(userStorage, never()).save(any(User.class));
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenExists() {
        // Arrange
        User expectedUser = new User("testuser", "test@example.com");
        when(userStorage.findByUsername("testuser")).thenReturn(Optional.of(expectedUser));

        // Act
        Optional<User> result = userService.findByUsername("testuser");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenUserDoesNotExist() {
        // Arrange
        when(userStorage.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findByUsername("nonexistent");

        // Assert
        assertTrue(result.isEmpty());
    }
} 