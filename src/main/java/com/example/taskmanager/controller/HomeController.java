package com.example.taskmanager.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Task Manager API is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
} 