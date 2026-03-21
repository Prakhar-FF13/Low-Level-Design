package com.springmicroservice.lowleveldesignproblems.moviecms.repositories;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.springmicroservice.lowleveldesignproblems.moviecms.models.User;

/**
 * In-memory implementation of UserRepository.
 * Uses ConcurrentHashMap for thread-safe storage.
 */
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> usersById = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        usersById.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(usersById.get(id));
    }
}
