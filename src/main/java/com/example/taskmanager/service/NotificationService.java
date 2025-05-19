package com.example.taskmanager.service;

import com.example.taskmanager.model.Notification;
import com.example.taskmanager.storage.NotificationStorage;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationStorage notificationStorage;

    public NotificationService(NotificationStorage notificationStorage) {
        this.notificationStorage = notificationStorage;
    }

    public Notification createNotification(Notification notification) {
        return notificationStorage.save(notification);
    }

    public List<Notification> getAllNotifications(String userId) {
        return notificationStorage.findByUserId(userId);
    }

    public List<Notification> getPendingNotifications(String userId) {
        return notificationStorage.findPendingByUserId(userId);
    }

    public void markAsRead(String id) {
        notificationStorage.markAsRead(id);
    }
} 