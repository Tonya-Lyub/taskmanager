package com.example.taskmanager.async;

import com.example.taskmanager.model.Task;

public interface AsyncTaskProcessor {
    /**
     * Асинхронно обрабатывает задачу
     * @param task задача для обработки
     */
    void processTaskAsync(Task task);

    /**
     * Асинхронно обновляет статус задачи
     * @param taskId ID задачи
     * @param completed новый статус завершения
     */
    void updateTaskStatusAsync(String taskId, boolean completed);
} 