package com.example.taskmanager.controller;

import com.example.taskmanager.model.Notification;
import com.example.taskmanager.service.NotificationService;
import com.example.taskmanager.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications(@RequestParam(required = false) String user) {
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.findByUsername(user)
                .map(u -> ResponseEntity.ok(notificationService.getAllNotifications(u.getId())))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Notification>> getPendingNotifications(@RequestParam(required = false) String user) {
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return userService.findByUsername(user)
                .map(u -> ResponseEntity.ok(notificationService.getPendingNotifications(u.getId())))
                .orElse(ResponseEntity.notFound().build());
    }
} 