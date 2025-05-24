package com.example.taskmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;
import com.example.taskmanager.storage.TaskStorage;
import com.example.taskmanager.storage.UserStorage;
import com.example.taskmanager.storage.NotificationStorage;
import com.example.taskmanager.storage.InMemoryTaskStorage;
import com.example.taskmanager.storage.InMemoryUserStorage;
import com.example.taskmanager.storage.InMemoryNotificationStorage;

@Configuration
@Profile("inmemory")
public class InMemoryStorageConfig {
    
    @Bean
    public TaskStorage taskStorage() {
        return new InMemoryTaskStorage();
    }

    @Bean
    public UserStorage userStorage() {
        return new InMemoryUserStorage();
    }

    @Bean
    public NotificationStorage notificationStorage() {
        return new InMemoryNotificationStorage();
    }
} 