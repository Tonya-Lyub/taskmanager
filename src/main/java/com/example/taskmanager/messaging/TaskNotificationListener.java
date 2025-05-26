package com.example.taskmanager.messaging;

import com.example.taskmanager.config.RabbitMQConfig;
import com.example.taskmanager.model.Notification;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.NotificationService;
import com.example.taskmanager.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("rabbit")
@RequiredArgsConstructor
public class TaskNotificationListener {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleTaskCreated(Task task) {
        log.info("Received task created notification for task: {}", task.getId());
        
        // Проверяем, существует ли уже уведомление для этой задачи
        boolean notificationExists = notificationRepository.findByUserIdOrderByCreatedAtDesc(task.getUserId())
            .stream()
            .anyMatch(n -> n.getTaskId().equals(task.getId()));
            
        if (!notificationExists) {
            Notification notification = Notification.builder()
                .userId(task.getUserId())
                .taskId(task.getId())
                .message("New task created: " + task.getTitle())
                .build();
                
            notificationService.createNotification(notification);
        } else {
            log.info("Notification for task {} already exists, skipping", task.getId());
        }
    }
} 