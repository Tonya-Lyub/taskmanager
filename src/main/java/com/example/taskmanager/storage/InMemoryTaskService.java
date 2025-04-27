package com.example.taskmanager.storage;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InMemoryTaskService implements TaskService {

    private final Map<Long, Task> tasks = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task createTask(Task task) {
        long id = idCounter.incrementAndGet();
        task.setId(id);
        tasks.put(id, task);
        return task;
    }

    @Override
    public void deleteTask(Long id) {
        tasks.remove(id);
    }
}
