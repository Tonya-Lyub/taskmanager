package com.example.taskmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@Profile("db")
@EnableJpaRepositories(basePackages = "com.example.taskmanager.repository")
public class StorageConfig {
    // Конфигурация для профиля с базой данных
} 