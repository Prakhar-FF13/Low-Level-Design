package com.springmicroservice.lowleveldesignproblems.onlineauction.repositories;

import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.onlineauction.models.User;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
}
