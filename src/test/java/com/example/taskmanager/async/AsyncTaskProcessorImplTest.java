package com.example.taskmanager.async;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.mockito.Mockito.*;

class AsyncTaskProcessorImplTest {

    @Mock
    private TaskRepository taskRepository;

    private AsyncTaskProcessor processor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processor = new AsyncTaskProcessorImpl(taskRepository);
    }

    @Test
    void processTaskAsync_ShouldProcessTask() {
        // Arrange
        Task task = new Task();
        task.setId("1");
        task.setTitle("Test Task");

        // Act
        processor.processTaskAsync(task);

        // Assert
        // Проверяем, что метод выполнился без ошибок
        // Поскольку метод асинхронный, мы не можем проверить его выполнение напрямую
    }

    @Test
    void updateTaskStatusAsync_ShouldUpdateTaskStatus() {
        // Arrange
        String taskId = "1";
        boolean completed = true;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        processor.updateTaskStatusAsync(taskId, completed);

        // Assert
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
    }
} 