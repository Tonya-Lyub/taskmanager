package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
@ActiveProfiles("redis")
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

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        cacheManager.getCache("tasks").clear();
        cacheManager.getCache("pendingTasks").clear();
    }

    @Test
    void shouldCacheTasks() {
        // Given
        String userId = "test-user";
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), userId);
        taskRepository.save(task);

        // When
        List<Task> firstCall = taskService.getAllTasks(userId);
        List<Task> secondCall = taskService.getAllTasks(userId);

        // Then
        assertThat(firstCall).hasSize(1);
        assertThat(secondCall).hasSize(1);
        assertThat(cacheManager.getCache("tasks").get(userId)).isNotNull();
    }

    @Test
    void shouldCachePendingTasks() {
        // Given
        String userId = "test-user";
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), userId);
        taskRepository.save(task);

        // When
        List<Task> firstCall = taskService.getPendingTasks(userId);
        List<Task> secondCall = taskService.getPendingTasks(userId);

        // Then
        assertThat(firstCall).hasSize(1);
        assertThat(secondCall).hasSize(1);
        assertThat(cacheManager.getCache("pendingTasks").get(userId)).isNotNull();
    }

    @Test
    void shouldNotCacheEmptyResults() {
        // Given
        String userId = "non-existent-user";

        // When
        List<Task> firstCall = taskService.getAllTasks(userId);
        List<Task> secondCall = taskService.getAllTasks(userId);

        // Then
        assertThat(firstCall).isEmpty();
        assertThat(secondCall).isEmpty();
        assertThat(cacheManager.getCache("tasks").get(userId)).isNull();
    }

    @Test
    void shouldEvictCacheOnTaskUpdate() {
        // Given
        String userId = "test-user";
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), userId);
        task = taskRepository.save(task);

        // When
        List<Task> firstCall = taskService.getAllTasks(userId);
        task.setTitle("Updated Task");
        taskService.updateTask(task);
        List<Task> secondCall = taskService.getAllTasks(userId);

        // Then
        assertThat(firstCall).hasSize(1);
        assertThat(secondCall).hasSize(1);
        assertThat(secondCall.get(0).getTitle()).isEqualTo("Updated Task");
    }
} 