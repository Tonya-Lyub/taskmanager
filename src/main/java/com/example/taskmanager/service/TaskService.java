package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
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

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks(String userId) {
        return taskRepository.findByUserIdAndDeletedFalse(userId);
    }

    public List<Task> getPendingTasks(String userId) {
        return taskRepository.findByUserIdAndCompletedFalseAndDeletedFalse(userId);
    }

    public void deleteTask(String id) {
        taskRepository.findById(id).ifPresent(task -> {
            task.setDeleted(true);
            taskRepository.save(task);
        });
    }

    public Task completeTask(String id) {
        return taskRepository.findById(id)
            .map(task -> {
                task.setCompleted(true);
                return taskRepository.save(task);
            })
            .orElseThrow(() -> new RuntimeException("Task not found"));
    }
} 