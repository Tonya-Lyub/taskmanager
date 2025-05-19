package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.TaskStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskStorage taskStorage;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskStorage);
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        // Arrange
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        when(taskStorage.save(any(Task.class))).thenReturn(task);

        // Act
        Task result = taskService.createTask(task);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskStorage).save(task);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasksForUser() {
        // Arrange
        String userId = "user1";
        List<Task> expectedTasks = Arrays.asList(
            new Task("Task 1", "Desc 1", LocalDateTime.now().plusDays(1), userId),
            new Task("Task 2", "Desc 2", LocalDateTime.now().plusDays(2), userId)
        );
        when(taskStorage.findByUserId(userId)).thenReturn(expectedTasks);

        // Act
        List<Task> result = taskService.getAllTasks(userId);

        // Assert
        assertEquals(2, result.size());
        verify(taskStorage).findByUserId(userId);
    }

    @Test
    void getPendingTasks_ShouldReturnOnlyPendingTasks() {
        // Arrange
        String userId = "user1";
        List<Task> expectedTasks = Arrays.asList(
            new Task("Task 1", "Desc 1", LocalDateTime.now().plusDays(1), userId),
            new Task("Task 2", "Desc 2", LocalDateTime.now().plusDays(2), userId)
        );
        when(taskStorage.findPendingByUserId(userId)).thenReturn(expectedTasks);

        // Act
        List<Task> result = taskService.getPendingTasks(userId);

        // Assert
        assertEquals(2, result.size());
        verify(taskStorage).findPendingByUserId(userId);
    }

    @Test
    void deleteTask_ShouldMarkTaskAsDeleted() {
        // Arrange
        String taskId = "task1";

        // Act
        taskService.deleteTask(taskId);

        // Assert
        verify(taskStorage).delete(taskId);
    }
} 