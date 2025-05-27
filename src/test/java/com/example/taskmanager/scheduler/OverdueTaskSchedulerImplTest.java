package com.example.taskmanager.scheduler;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class OverdueTaskSchedulerImplTest {

    @Mock
    private TaskRepository taskRepository;

    private OverdueTaskScheduler scheduler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        scheduler = new OverdueTaskSchedulerImpl(taskRepository);
    }

    @Test
    void checkOverdueTasks_ShouldFindAndLogOverdueTasks() {
        // Arrange
        Task overdueTask1 = new Task();
        overdueTask1.setId("1");
        overdueTask1.setTitle("Overdue Task 1");
        overdueTask1.setDueAt(LocalDateTime.now().minusDays(1));
        overdueTask1.setCompleted(false);
        overdueTask1.setDeleted(false);

        Task overdueTask2 = new Task();
        overdueTask2.setId("2");
        overdueTask2.setTitle("Overdue Task 2");
        overdueTask2.setDueAt(LocalDateTime.now().minusHours(2));
        overdueTask2.setCompleted(false);
        overdueTask2.setDeleted(false);

        List<Task> overdueTasks = Arrays.asList(overdueTask1, overdueTask2);

        when(taskRepository.findByDueAtBeforeAndCompletedFalseAndDeletedFalse(
            any(LocalDateTime.class)
        )).thenReturn(overdueTasks);

        // Act
        scheduler.checkOverdueTasks();

        // Assert
        verify(taskRepository).findByDueAtBeforeAndCompletedFalseAndDeletedFalse(
            any(LocalDateTime.class)
        );
    }
} 