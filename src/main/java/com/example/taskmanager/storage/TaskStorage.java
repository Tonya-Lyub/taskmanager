package com.example.taskmanager.storage;

import com.example.taskmanager.model.Task;
import java.util.List;

public interface TaskStorage {
    Task save(Task task);
    List<Task> findByUserId(String userId);
    List<Task> findPendingByUserId(String userId);
    void delete(String id);
} 