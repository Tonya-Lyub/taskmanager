package com.example.taskmanager.service;

import com.example.taskmanager.model.User;
import com.example.taskmanager.storage.UserStorage;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User registerUser(User user) {
        if (userStorage.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        return userStorage.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userStorage.findByUsername(username);
    }
} 