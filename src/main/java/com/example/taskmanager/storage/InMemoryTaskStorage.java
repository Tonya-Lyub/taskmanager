package com.example.taskmanager.storage;

import com.example.taskmanager.model.Task;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryTaskStorage implements TaskStorage {
    private final Map<String, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public List<Task> findByUserId(String userId) {
        return tasks.values().stream()
            .filter(task -> task.getUserId().equals(userId) && !task.isDeleted())
            .toList();
    }

    @Override
    public List<Task> findPendingByUserId(String userId) {
        return tasks.values().stream()
            .filter(task -> task.getUserId().equals(userId) && !task.isCompleted() && !task.isDeleted())
            .toList();
    }

    @Override
    public void delete(String id) {
        tasks.computeIfPresent(id, (key, task) -> {
            task.setDeleted(true);
            return task;
        });
    }
} 