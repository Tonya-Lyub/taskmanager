package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TaskControllerTest {

    @InjectMocks
    private com.example.taskmanager.controller.TaskController taskController;

    @Mock
    private TaskService taskService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task(1L, "Test Task", "Test Description");
    }

    @Test
    void testCreateTask() {
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        ResponseEntity<Task> response = taskController.createTask(task);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(task, response.getBody());
        verify(taskService, times(1)).createTask(any(Task.class)); // проверяем, что метод был вызван 1 раз
    }

    @Test
    void testGetAllTasks() {
        when(taskService.getAllTasks()).thenReturn(List.of(task));

        var tasks = taskController.getAllTasks();

        assertFalse(tasks.isEmpty());
        assertEquals(1, tasks.size());
    }

    @Test
    void testDeleteTask() {
        doNothing().when(taskService).deleteTask(anyLong());

        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService, times(1)).deleteTask(anyLong());  // проверяем, что метод был вызван 1 раз
    }
}
