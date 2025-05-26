package com.example.taskmanager.controller;

import com.example.taskmanager.model.Notification;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.NotificationService;
import com.example.taskmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private UserService userService;

    private NotificationController notificationController;

    @BeforeEach
    void setUp() {
        notificationController = new NotificationController(notificationService, userService);
    }

    @Test
    void getAllNotifications_ShouldReturnNotifications_WhenUserExists() {
        // Arrange
        String username = "testuser";
        User user = new User(username, "test@example.com");
        List<Notification> notifications = Arrays.asList(
            Notification.builder().message("Notification 1").userId(user.getId()).read(false).createdAt(LocalDateTime.now()).build(),
            Notification.builder().message("Notification 2").userId(user.getId()).read(false).createdAt(LocalDateTime.now()).build()
        );
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(notificationService.getAllNotifications(user.getId())).thenReturn(notifications);

        // Act
        ResponseEntity<List<Notification>> response = notificationController.getAllNotifications(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notifications, response.getBody());
    }

    @Test
    void getAllNotifications_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        String username = "nonexistent";
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<Notification>> response = notificationController.getAllNotifications(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getPendingNotifications_ShouldReturnPendingNotifications_WhenUserExists() {
        // Arrange
        String username = "testuser";
        User user = new User(username, "test@example.com");
        List<Notification> notifications = Arrays.asList(
            Notification.builder().message("Notification 1").userId(user.getId()).read(false).createdAt(LocalDateTime.now()).build(),
            Notification.builder().message("Notification 2").userId(user.getId()).read(false).createdAt(LocalDateTime.now()).build()
        );
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(notificationService.getPendingNotifications(user.getId())).thenReturn(notifications);

        // Act
        ResponseEntity<List<Notification>> response = notificationController.getPendingNotifications(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(notifications, response.getBody());
    }

    @Test
    void getPendingNotifications_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        String username = "nonexistent";
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<Notification>> response = notificationController.getPendingNotifications(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
} 