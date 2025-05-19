package com.example.taskmanager.storage;

import com.example.taskmanager.model.User;
import java.util.Optional;
import java.util.List;

public interface UserStorage {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    void delete(String id);
} 