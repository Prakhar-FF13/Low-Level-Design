package com.springmicroservice.lowleveldesignproblems.splitwise.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity.GroupEntity;

public interface SplitwiseGroupRepository extends JpaRepository<GroupEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM GroupEntity g JOIN g.members m WHERE g.id = :groupId AND m.id = :userId")
    boolean isMember(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Query("SELECT DISTINCT g FROM GroupEntity g JOIN FETCH g.members WHERE g.id = :id")
    Optional<GroupEntity> findByIdWithMembers(@Param("id") Long id);
}
