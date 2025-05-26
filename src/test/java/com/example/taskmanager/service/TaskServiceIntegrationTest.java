package com.example.taskmanager.service;

import com.example.taskmanager.config.RabbitMQConfig;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Notification;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.NotificationRepository;
import com.example.taskmanager.messaging.MessagePublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
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
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles({"redis", "rabbit"})
@Testcontainers
class TaskServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("taskdb")
            .withUsername("postgres")
            .withPassword("postgres");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @Container
    static GenericContainer<?> rabbitmq = new GenericContainer<>("rabbitmq:3-management")
            .withExposedPorts(5672, 15672);

    @MockBean
    private MessagePublisher messagePublisher;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private CacheManager cacheManager;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getFirstMappedPort);
    }

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        notificationRepository.deleteAll();
        if (cacheManager.getCache("tasks") != null) {
            cacheManager.getCache("tasks").clear();
        }
        if (cacheManager.getCache("pendingTasks") != null) {
            cacheManager.getCache("pendingTasks").clear();
        }
    }

    @Test
    void shouldCacheTasks() {
        // Given
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        taskService.createTask(task);

        // When
        List<Task> firstCall = taskService.getAllTasks("user1");
        List<Task> secondCall = taskService.getAllTasks("user1");

        // Then
        assertThat(firstCall).hasSize(1);
        assertThat(secondCall).hasSize(1);
        verify(messagePublisher).publishTaskCreated(task);
    }

    @Test
    void shouldCachePendingTasks() {
        // Given
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        taskService.createTask(task);

        // When
        List<Task> firstCall = taskService.getPendingTasks("user1");
        List<Task> secondCall = taskService.getPendingTasks("user1");

        // Then
        assertThat(firstCall).hasSize(1);
        assertThat(secondCall).hasSize(1);
        verify(messagePublisher).publishTaskCreated(task);
    }

    @Test
    void shouldEvictCacheOnTaskUpdate() {
        // Given
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        task = taskService.createTask(task);

        // When
        taskService.completeTask(task.getId());
        List<Task> pendingTasks = taskService.getPendingTasks("user1");

        // Then
        assertThat(pendingTasks).isEmpty();
        verify(messagePublisher).publishTaskCreated(task);
    }

    @Test
    void shouldNotCacheEmptyResults() {
        // Given
        String userId = "user1";
        taskRepository.deleteAll();

        // When
        List<Task> firstCall = taskService.getAllTasks(userId);
        List<Task> secondCall = taskService.getAllTasks(userId);

        // Then
        assertThat(firstCall).isEmpty();
        assertThat(secondCall).isEmpty();
    }

    @Test
    void shouldCreateNotificationOnlyThroughQueue() {
        // Given
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        
        // When - создаем задачу напрямую через репозиторий
        task = taskRepository.save(task);
        
        // Then - уведомление не должно создаться
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).isEmpty();
        
        // When - отправляем сообщение через очередь
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            task
        );
        
        // Wait for message processing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Then - уведомление должно создаться
        notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(1);
        assertThat(notifications.get(0).getUserId()).isEqualTo(task.getUserId());
        assertThat(notifications.get(0).getMessage()).contains("New task created");
    }

    @Test
    void shouldNotDuplicateNotifications() {
        // Given
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        task = taskRepository.save(task);
        
        // When - отправляем сообщение дважды
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            task
        );
        
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            task
        );
        
        // Wait for message processing
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Then - должно быть только одно уведомление
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).hasSize(1);
    }

    @Test
    void shouldNotCreateNotificationWhenTaskIsUpdated() {
        // Given
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        task = taskRepository.save(task);

        // When
        task.setTitle("Updated Task");
        taskRepository.save(task);

        // Then
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).isEmpty();
    }

    @Test
    void shouldNotCreateNotificationWhenTaskIsDeleted() {
        // Given
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");
        task = taskRepository.save(task);

        // When
        taskService.deleteTask(task.getId());

        // Then
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).isEmpty();
    }
} 