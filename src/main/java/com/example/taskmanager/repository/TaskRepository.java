package com.example.taskmanager.repository;

import com.example.taskmanager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByUserIdAndDeletedFalse(String userId);
    List<Task> findByUserIdAndCompletedFalseAndDeletedFalse(String userId);
} 