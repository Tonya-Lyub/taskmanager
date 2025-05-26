package com.example.taskmanager.service;

import com.example.taskmanager.model.Notification;
import com.example.taskmanager.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository);
    }

    @Test
    void createNotification_ShouldSaveAndReturnNotification() {
        // Arrange
        Notification notification = Notification.builder()
            .message("Test notification")
            .userId("user1")
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        Notification result = notificationService.createNotification(notification);

        // Assert
        assertNotNull(result);
        assertEquals("Test notification", result.getMessage());
        verify(notificationRepository).save(notification);
    }

    @Test
    void getAllNotifications_ShouldReturnAllNotificationsForUser() {
        // Arrange
        String userId = "user1";
        List<Notification> expectedNotifications = Arrays.asList(
            Notification.builder().message("Notification 1").userId(userId).read(false).createdAt(LocalDateTime.now()).build(),
            Notification.builder().message("Notification 2").userId(userId).read(false).createdAt(LocalDateTime.now()).build()
        );
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(expectedNotifications);

        // Act
        List<Notification> result = notificationService.getAllNotifications(userId);

        // Assert
        assertEquals(2, result.size());
        verify(notificationRepository).findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void getPendingNotifications_ShouldReturnOnlyUnreadNotifications() {
        // Arrange
        String userId = "user1";
        List<Notification> expectedNotifications = Arrays.asList(
            Notification.builder().message("Notification 1").userId(userId).read(false).createdAt(LocalDateTime.now()).build(),
            Notification.builder().message("Notification 2").userId(userId).read(false).createdAt(LocalDateTime.now()).build()
        );
        when(notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId)).thenReturn(expectedNotifications);

        // Act
        List<Notification> result = notificationService.getPendingNotifications(userId);

        // Assert
        assertEquals(2, result.size());
        verify(notificationRepository).findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    @Test
    void markAsRead_ShouldMarkNotificationAsRead() {
        // Arrange
        String notificationId = "notification1";
        Notification notification = Notification.builder()
            .message("Test notification")
            .userId("user1")
            .read(false)
            .createdAt(LocalDateTime.now())
            .build();
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        // Act
        notificationService.markAsRead(notificationId);

        // Assert
        assertTrue(notification.isRead());
        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_ShouldDoNothing_WhenNotificationNotFound() {
        // Arrange
        String notificationId = "notification1";
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // Act
        notificationService.markAsRead(notificationId);

        // Assert
        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
} 