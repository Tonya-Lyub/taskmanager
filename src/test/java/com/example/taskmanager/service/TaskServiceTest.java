package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.InMemoryTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class TaskServiceTest {

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new InMemoryTaskService();  // Используем InMemoryTaskService для тестов
    }

    @Test
    void testCreateTask() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");

        Task createdTask = taskService.createTask(task);

        assertNotNull(createdTask.getId());
        assertEquals("Test Task", createdTask.getTitle());
        assertEquals("Test Description", createdTask.getDescription());
    }

    @Test
    void testGetAllTasks() {
        Task task1 = new Task(null, "Task 1", "Description 1");
        Task task2 = new Task(null, "Task 2", "Description 2");

        taskService.createTask(task1);
        taskService.createTask(task2);

        List<Task> tasks = taskService.getAllTasks();

        assertEquals(2, tasks.size());
    }

    @Test
    void testDeleteTask() {
        Task task = new Task(null, "Task to delete", "Description");
        Task createdTask = taskService.createTask(task);

        taskService.deleteTask(createdTask.getId());

        List<Task> tasks = taskService.getAllTasks();
        assertEquals(0, tasks.size());  // После удаления задача должна быть в списке
    }
}

