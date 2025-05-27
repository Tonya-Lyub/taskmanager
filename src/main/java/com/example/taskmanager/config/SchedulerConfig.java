package com.example.taskmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@Profile("scheduler")
@EnableScheduling
public class SchedulerConfig {
    // Конфигурация для профиля планировщика
} 