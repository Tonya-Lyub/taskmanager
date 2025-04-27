package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import java.util.List;

public interface TaskService {
    List<Task> getAllTasks();
    Task createTask(Task task);
    void deleteTask(Long id);
}
