package com.example.taskmanager.service;

import com.example.taskmanager.model.Notification;
import com.example.taskmanager.storage.NotificationStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationStorage notificationStorage;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationStorage);
    }

    @Test
    void createNotification_ShouldSaveAndReturnNotification() {
        // Arrange
        Notification notification = new Notification("Test notification", "user1");
        when(notificationStorage.save(any(Notification.class))).thenReturn(notification);

        // Act
        Notification result = notificationService.createNotification(notification);

        // Assert
        assertNotNull(result);
        assertEquals("Test notification", result.getMessage());
        verify(notificationStorage).save(notification);
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotificationsForUser() {
        // Arrange
        String userId = "user1";
        List<Notification> expectedNotifications = Arrays.asList(
            new Notification("Notification 1", userId),
            new Notification("Notification 2", userId)
        );
        when(notificationStorage.findByUserId(userId)).thenReturn(expectedNotifications);

        // Act
        List<Notification> result = notificationService.getAllNotifications(userId);

        // Assert
        assertEquals(2, result.size());
        verify(notificationStorage).findByUserId(userId);
    }

    @Test
    void getPendingNotifications_ShouldReturnOnlyUnreadNotifications() {
        // Arrange
        String userId = "user1";
        List<Notification> expectedNotifications = Arrays.asList(
            new Notification("Notification 1", userId),
            new Notification("Notification 2", userId)
        );
        when(notificationStorage.findPendingByUserId(userId)).thenReturn(expectedNotifications);

        // Act
        List<Notification> result = notificationService.getPendingNotifications(userId);

        // Assert
        assertEquals(2, result.size());
        verify(notificationStorage).findPendingByUserId(userId);
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        // Arrange
        String notificationId = "notification1";

        // Act
        notificationService.markAsRead(notificationId);

        // Assert
        verify(notificationStorage).markAsRead(notificationId);
    }
} 