package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.TaskService;
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
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private UserService userService;

    private TaskController taskController;

    @BeforeEach
    void setUp() {
        taskController = new TaskController(taskService, userService);
    }

    @Test
    void getAllTasks_ShouldReturnTasks_WhenUserExists() {
        // Arrange
        String username = "testuser";
        User user = new User(username, "test@example.com");
        List<Task> expectedTasks = Arrays.asList(
            new Task("Task 1", "Desc 1", LocalDateTime.now().plusDays(1), user.getId()),
            new Task("Task 2", "Desc 2", LocalDateTime.now().plusDays(2), user.getId())
        );
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(taskService.getAllTasks(user.getId())).thenReturn(expectedTasks);

        // Act
        ResponseEntity<List<Task>> response = taskController.getAllTasks(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTasks, response.getBody());
    }

    @Test
    void getAllTasks_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        String username = "nonexistent";
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<Task>> response = taskController.getAllTasks(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getPendingTasks_ShouldReturnTasks_WhenUserExists() {
        // Arrange
        String username = "testuser";
        User user = new User(username, "test@example.com");
        List<Task> expectedTasks = Arrays.asList(
            new Task("Task 1", "Desc 1", LocalDateTime.now().plusDays(1), user.getId()),
            new Task("Task 2", "Desc 2", LocalDateTime.now().plusDays(2), user.getId())
        );
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(taskService.getPendingTasks(user.getId())).thenReturn(expectedTasks);

        // Act
        ResponseEntity<List<Task>> response = taskController.getPendingTasks(username);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTasks, response.getBody());
    }

    @Test
    void getPendingTasks_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        String username = "nonexistent";
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<Task>> response = taskController.getPendingTasks(username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void createTask_ShouldCreateTask_WhenUserExists() {
        // Arrange
        String username = "testuser";
        User user = new User(username, "test@example.com");
        Task task = new Task("New Task", "Description", LocalDateTime.now().plusDays(1), null);
        Task savedTask = new Task("New Task", "Description", LocalDateTime.now().plusDays(1), user.getId());
        when(userService.findByUsername(username)).thenReturn(Optional.of(user));
        when(taskService.createTask(any(Task.class))).thenReturn(savedTask);

        // Act
        ResponseEntity<Task> response = taskController.createTask(task, username);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedTask, response.getBody());
    }

    @Test
    void createTask_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        String username = "nonexistent";
        Task task = new Task("New Task", "Description", LocalDateTime.now().plusDays(1), null);
        when(userService.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Task> response = taskController.createTask(task, username);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteTask_ShouldDeleteTask() {
        // Arrange
        String taskId = "task1";

        // Act
        ResponseEntity<Void> response = taskController.deleteTask(taskId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(taskService).deleteTask(taskId);
    }
} 