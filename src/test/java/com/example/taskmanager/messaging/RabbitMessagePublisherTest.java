package com.example.taskmanager.messaging;

import com.example.taskmanager.config.RabbitMQConfig;
import com.example.taskmanager.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RabbitMessagePublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private RabbitMessagePublisher messagePublisher;

    @BeforeEach
    void setUp() {
        messagePublisher = new RabbitMessagePublisher(rabbitTemplate);
    }

    @Test
    void publishTaskCreated_ShouldSendMessageToRabbitMQ() {
        // Arrange
        Task task = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), "user1");

        // Act
        messagePublisher.publishTaskCreated(task);

        // Assert
        verify(rabbitTemplate).convertAndSend(
            RabbitMQConfig.EXCHANGE_NAME,
            RabbitMQConfig.ROUTING_KEY,
            task
        );
    }
} 