package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Cacheable(value = "tasks", key = "#userId", unless = "#result.isEmpty()")
    public List<Task> getAllTasks(String userId) {
        return taskRepository.findByUserIdAndDeletedFalse(userId);
    }

    @Cacheable(value = "pendingTasks", key = "#userId", unless = "#result.isEmpty()")
    public List<Task> getPendingTasks(String userId) {
        return taskRepository.findByUserIdAndCompletedFalseAndDeletedFalse(userId);
    }

    @CacheEvict(value = {"tasks", "pendingTasks"}, key = "#task.userId")
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    @CacheEvict(value = {"tasks", "pendingTasks"}, key = "#task.userId")
    public Task updateTask(Task task) {
        return taskRepository.save(task);
    }

    @CacheEvict(value = {"tasks", "pendingTasks"}, allEntries = true)
    public void deleteTask(String id) {
        taskRepository.findById(id).ifPresent(task -> {
            task.setDeleted(true);
            taskRepository.save(task);
        });
    }

    @CacheEvict(value = {"tasks", "pendingTasks"}, allEntries = true)
    public Task completeTask(String id) {
        return taskRepository.findById(id)
            .map(task -> {
                task.setCompleted(true);
                return taskRepository.save(task);
            })
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }
} 