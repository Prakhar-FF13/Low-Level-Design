package com.springmicroservice.lowleveldesignproblems.splitwise.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity.UserEntity;

public interface SplitwiseUserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
