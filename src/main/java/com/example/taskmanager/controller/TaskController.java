package com.example.taskmanager.controller;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(@RequestParam(required = false) String user) {
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.findByUsername(user)
                .map(u -> ResponseEntity.ok(taskService.getAllTasks(u.getId())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingTasks(@RequestParam(required = false) String user) {
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.findByUsername(user)
                .map(u -> ResponseEntity.ok(taskService.getPendingTasks(u.getId())))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task, @RequestParam(required = false) String user) {
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.findByUsername(user)
                .map(u -> {
                    task.setUserId(u.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok().build();
    }
} 