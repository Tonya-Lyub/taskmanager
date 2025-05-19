package com.example.taskmanager.storage.impl;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.storage.TaskStorage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@Profile("inmemory")
public class InMemoryTaskStorage implements TaskStorage {
    private final Map<String, Task> tasks = new HashMap<>();

    @Override
    public Task save(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public List<Task> findByUserId(String userId) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<Task> findPendingByUserId(String userId) {
        return tasks.values().stream()
                .filter(task -> task.getUserId().equals(userId))
                .filter(task -> !task.isCompleted() && !task.isDeleted())
                .toList();
    }

    @Override
    public void delete(String id) {
        Task task = tasks.get(id);
        if (task != null) {
            task.setDeleted(true);
            tasks.put(id, task);
        }
    }
} 