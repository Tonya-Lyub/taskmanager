package com.example.taskmanager.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "due_at")
    private LocalDateTime dueAt;

    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false)
    private boolean deleted;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.completed = false;
        this.deleted = false;
    }

    public Task(String title, String description, LocalDateTime dueAt, String userId) {
        this.title = title;
        this.description = description;
        this.dueAt = dueAt;
        this.userId = userId;
    }
} 