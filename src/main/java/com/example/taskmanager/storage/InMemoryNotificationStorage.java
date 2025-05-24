package com.example.taskmanager.storage;

import com.example.taskmanager.model.Notification;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryNotificationStorage implements NotificationStorage {
    private final Map<String, Notification> notifications = new ConcurrentHashMap<>();

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
            .filter(notification -> notification.getUserId().equals(userId) && !notification.isRead())
            .toList();
    }

    @Override
    public void markAsRead(String id) {
        notifications.computeIfPresent(id, (key, notification) -> {
            notification.setRead(true);
            return notification;
        });
    }
} 