package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.messaging.MessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private MessagePublisher messagePublisher;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(taskRepository, messagePublisher);
    }

    @Test
    void createTask_ShouldSaveAndReturnTask() {
        // Arrange
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        Task result = taskService.createTask(task);

        // Assert
        assertNotNull(result);
        assertEquals("Test Task", result.getTitle());
        verify(taskRepository).save(task);
        verify(messagePublisher).publishTaskCreated(task);
    }

    @Test
    void getAllTasks_ShouldReturnAllTasksForUser() {
        // Arrange
        String userId = "user1";
        List<Task> expectedTasks = Arrays.asList(
            new Task("Task 1", "Desc 1", LocalDateTime.now().plusDays(1), userId),
            new Task("Task 2", "Desc 2", LocalDateTime.now().plusDays(2), userId)
        );
        when(taskRepository.findByUserIdAndDeletedFalse(userId)).thenReturn(expectedTasks);

        // Act
        List<Task> result = taskService.getAllTasks(userId);

        // Assert
        assertEquals(2, result.size());
        verify(taskRepository).findByUserIdAndDeletedFalse(userId);
    }

    @Test
    void getPendingTasks_ShouldReturnOnlyPendingTasks() {
        // Arrange
        String userId = "user1";
        List<Task> expectedTasks = Arrays.asList(
            new Task("Task 1", "Desc 1", LocalDateTime.now().plusDays(1), userId),
            new Task("Task 2", "Desc 2", LocalDateTime.now().plusDays(2), userId)
        );
        when(taskRepository.findByUserIdAndCompletedFalseAndDeletedFalse(userId)).thenReturn(expectedTasks);

        // Act
        List<Task> result = taskService.getPendingTasks(userId);

        // Assert
        assertEquals(2, result.size());
        verify(taskRepository).findByUserIdAndCompletedFalseAndDeletedFalse(userId);
    }

    @Test
    void deleteTask_ShouldMarkTaskAsDeleted() {
        // Arrange
        String taskId = "task1";
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        taskService.deleteTask(taskId);

        // Assert
        assertTrue(task.isDeleted());
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    @Test
    void completeTask_ShouldMarkTaskAsCompleted() {
        // Arrange
        String taskId = "task1";
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        Task result = taskService.completeTask(taskId);

        // Assert
        assertTrue(result.isCompleted());
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(task);
    }

    @Test
    void completeTask_ShouldThrowException_WhenTaskNotFound() {
        // Arrange
        String taskId = "task1";
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> taskService.completeTask(taskId));
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
    }
} 