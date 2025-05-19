package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;
import com.example.taskmanager.service.NotificationService;
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
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @Mock
    private NotificationService notificationService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService, taskService, notificationService);
    }

    @Test
    void registerUser_ShouldCreateUser() {
        // Arrange
        User user = new User("testuser", "test@example.com");
        when(userService.registerUser(any(User.class))).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.registerUser(user);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(user, response.getBody());
        verify(userService).registerUser(user);
    }

    @Test
    void registerUser_ShouldReturnBadRequest_WhenRegistrationFails() {
        // Arrange
        User user = new User("testuser", "test@example.com");
        when(userService.registerUser(any(User.class))).thenThrow(new RuntimeException("Username exists"));

        // Act
        ResponseEntity<User> response = userController.registerUser(user);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getUserInfo_ShouldReturnUserWithTasksAndNotifications_WhenUserExists() {
        // Arrange
        String username = "testuser";
        User user = new User(username, "test@example.com");
        List<Task> tasks = Arrays.asList(
            new Task("Task 1", "Desc 1", LocalDateTime.now().plusDays(1), user.getId()),
            new Task("Task 2", "Desc 2", LocalDateTime.now().plusDays(2), user.getId())
        );
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(taskService.getAllTasks(user.getId())).thenReturn(tasks);
        when(notificationService.getAllNotifications(user.getId())).thenReturn(List.of());

        // Act
        ResponseEntity<Map<String, Object>> response = userController.getUserInfo(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(user, responseBody.get("user"));
        assertEquals(tasks, responseBody.get("tasks"));
    }

    @Test
    void getUserInfo_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        String username = "nonexistent";
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Map<String, Object>> response = userController.getUserInfo(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
} 