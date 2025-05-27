package com.example.taskmanager.async;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncTaskProcessorImpl implements AsyncTaskProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskProcessorImpl.class);
    
    private final TaskRepository taskRepository;

    public AsyncTaskProcessorImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    @Async
    public void processTaskAsync(Task task) {
        logger.info("Starting async processing of task: {} at {}", task.getId(), java.time.LocalDateTime.now());
        try {
            // Здесь можно добавить любую асинхронную логику обработки задачи
            Thread.sleep(1000); // Имитация длительной обработки
            logger.info("Completed async processing of task: {} at {}", task.getId(), java.time.LocalDateTime.now());
        } catch (InterruptedException e) {
            logger.error("Async task processing was interrupted for task: {}", task.getId(), e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    @Async
    public void updateTaskStatusAsync(String taskId, boolean completed) {
        logger.info("Starting async status update for task: {} at {}", taskId, java.time.LocalDateTime.now());
        try {
            Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found: " + taskId));
            
            task.setCompleted(completed);
            taskRepository.save(task);
            
            logger.info("Completed async status update for task: {} at {}", taskId, java.time.LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Error during async status update for task: {} at {}", taskId, java.time.LocalDateTime.now(), e);
        }
    }
} 