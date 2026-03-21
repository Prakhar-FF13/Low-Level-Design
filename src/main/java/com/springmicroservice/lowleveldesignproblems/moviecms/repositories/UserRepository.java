package com.springmicroservice.lowleveldesignproblems.moviecms.repositories;

import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.moviecms.models.User;

/**
 * Repository interface for User data access.
 * Abstraction allows swapping in-memory impl with DB later.
 */
public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
}
