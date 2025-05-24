package com.example.taskmanager.storage;

import com.example.taskmanager.model.User;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
            .filter(user -> user.getUsername().equals(username))
            .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(String id) {
        users.remove(id);
    }
} 