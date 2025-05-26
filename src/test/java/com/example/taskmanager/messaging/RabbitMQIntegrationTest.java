package com.example.taskmanager.messaging;

import com.example.taskmanager.config.RabbitMQConfig;
import com.example.taskmanager.model.Notification;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.NotificationRepository;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("rabbit")
@Testcontainers
class RabbitMQIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("taskdb")
            .withUsername("postgres")
            .withPassword("postgres")
            .withInitScript("init.sql")
            .withReuse(true);

    @Container
    static GenericContainer<?> rabbitmq = new GenericContainer<>("rabbitmq:3-management")
            .withExposedPorts(5672, 15672)
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getFirstMappedPort);
        registry.add("spring.rabbitmq.username", () -> "guest");
        registry.add("spring.rabbitmq.password", () -> "guest");
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        notificationRepository.deleteAll();
    }

    @Test
    void shouldCreateNotificationWhenTaskIsCreated() throws InterruptedException {
        // Given
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        task = taskRepository.save(task);

        // When
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            task
        );

        // Wait for message processing
        Thread.sleep(2000);

        // Then
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getUserId()).isEqualTo(task.getUserId());
        assertThat(notifications.get(0).getMessage()).contains("New task created");
        assertThat(notifications.get(0).getTaskId()).isEqualTo(task.getId());
    }
} 