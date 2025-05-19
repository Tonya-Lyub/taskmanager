package com.example.taskmanager.storage;

import com.example.taskmanager.model.Notification;
import java.util.List;

public interface NotificationStorage {
    Notification save(Notification notification);
    List<Notification> findByUserId(String userId);
    List<Notification> findPendingByUserId(String userId);
    void markAsRead(String id);
} 