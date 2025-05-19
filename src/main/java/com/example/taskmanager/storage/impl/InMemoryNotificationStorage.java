package com.example.taskmanager.storage.impl;

import com.example.taskmanager.model.Notification;
import com.example.taskmanager.storage.NotificationStorage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@Profile("inmemory")
public class InMemoryNotificationStorage implements NotificationStorage {
    private final Map<String, Notification> notifications = new HashMap<>();

    @Override
    public Notification save(Notification notification) {
        notifications.put(notification.getId(), notification);
        return notification;
    }

    @Override
    public List<Notification> findByUserId(String userId) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<Notification> findPendingByUserId(String userId) {
        return notifications.values().stream()
                .filter(notification -> notification.getUserId().equals(userId))
                .filter(notification -> !notification.isRead())
                .toList();
    }

    @Override
    public void markAsRead(String id) {
        Notification notification = notifications.get(id);
        if (notification != null) {
            notification.setRead(true);
            notifications.put(id, notification);
        }
    }
} 