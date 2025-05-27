package com.example.taskmanager.scheduler;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Profile("scheduler")
public class OverdueTaskSchedulerImpl implements OverdueTaskScheduler {
    private static final Logger logger = LoggerFactory.getLogger(OverdueTaskSchedulerImpl.class);
    
    private final TaskRepository taskRepository;

    public OverdueTaskSchedulerImpl(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        logger.info("OverdueTaskSchedulerImpl bean created!");
    }

    @Override
    @Scheduled(fixedRate = 60000) // Запуск каждую минуту
    public void checkOverdueTasks() {
        logger.info("Starting overdue tasks check at {}", LocalDateTime.now());
        
        List<Task> overdueTasks = taskRepository.findByDueAtBeforeAndCompletedFalseAndDeletedFalse(
            LocalDateTime.now()
        );

        logger.info("Found {} overdue tasks", overdueTasks.size());

        for (Task task : overdueTasks) {
            logger.info("Processing overdue task: {} (ID: {}), due at: {}", 
                task.getTitle(), 
                task.getId(),
                task.getDueAt()
            );
        }

        logger.info("Completed overdue tasks check at {}", LocalDateTime.now());
    }
} 