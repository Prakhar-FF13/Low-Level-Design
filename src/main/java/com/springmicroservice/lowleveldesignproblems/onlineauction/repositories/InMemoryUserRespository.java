package com.springmicroservice.lowleveldesignproblems.onlineauction.repositories;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.User;

public class InMemoryUserRespository implements UserRepository {
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
