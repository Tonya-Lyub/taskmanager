package com.example.taskmanager.controller;

import com.example.taskmanager.model.User;
import com.example.taskmanager.service.UserService;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final TaskService taskService;
    private final NotificationService notificationService;

    public UserController(UserService userService, TaskService taskService, NotificationService notificationService) {
        this.userService = userService;
        this.taskService = taskService;
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.registerUser(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable String username) {
        return userService.findByUsername(username)
                .map(user -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("user", user);
                    response.put("tasks", taskService.getAllTasks(user.getId()));
                    response.put("notifications", notificationService.getAllNotifications(user.getId()));
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
} 