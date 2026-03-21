package com.springmicroservice.lowleveldesignproblems.moviecms.services;

import java.util.UUID;

import com.springmicroservice.lowleveldesignproblems.moviecms.models.User;
import com.springmicroservice.lowleveldesignproblems.moviecms.repositories.UserRepository;

/**
 * UserService handles user registration.
 * Responsibilities:
 * - Create users with unique IDs
 * - Validate input (e.g., non-empty name)
 * - Persist via UserRepository
 */
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Registers a new user.
     * @param name User's display name
     * @return The created User with generated ID
     */
    public User registerUser(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("User name cannot be null or empty");
        }
        User user = new User(UUID.randomUUID().toString(), name);
        return userRepository.save(user);
    }
}
