package com.example.taskmanager.messaging;

import com.example.taskmanager.model.Task;

public interface MessagePublisher {
    void publishTaskCreated(Task task);
} 