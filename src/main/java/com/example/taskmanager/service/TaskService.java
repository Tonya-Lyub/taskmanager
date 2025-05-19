package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.TaskStorage;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TaskService {
    private final TaskStorage taskStorage;

    public TaskService(TaskStorage taskStorage) {
        this.taskStorage = taskStorage;
    }

    public Task createTask(Task task) {
        return taskStorage.save(task);
    }

    public List<Task> getAllTasks(String userId) {
        return taskStorage.findByUserId(userId);
    }

    public List<Task> getPendingTasks(String userId) {
        return taskStorage.findPendingByUserId(userId);
    }

    public void deleteTask(String id) {
        taskStorage.delete(id);
    }
} 