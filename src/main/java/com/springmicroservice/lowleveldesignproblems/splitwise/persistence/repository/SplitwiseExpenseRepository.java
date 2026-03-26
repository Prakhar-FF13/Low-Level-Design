package com.springmicroservice.lowleveldesignproblems.splitwise.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springmicroservice.lowleveldesignproblems.splitwise.persistence.entity.ExpenseEntity;

public interface SplitwiseExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    @Query("SELECT e FROM ExpenseEntity e WHERE e.group.id = :groupId ORDER BY e.createdAt DESC")
    List<ExpenseEntity> findByGroupIdOrderByCreatedAtDesc(@Param("groupId") Long groupId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ExpenseEntity e JOIN e.payers p WHERE e.group.id = :groupId AND p.user.id = :userId")
    boolean existsExpenseWithPayerInGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM ExpenseEntity e JOIN e.splits s WHERE e.group.id = :groupId AND s.user.id = :userId")
    boolean existsExpenseWithSplitInGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);

    @Query("SELECT DISTINCT e FROM ExpenseEntity e JOIN e.splits s WHERE s.user.id = :userId ORDER BY e.createdAt DESC")
    List<ExpenseEntity> findExpensesWhereUserOwes(@Param("userId") Long userId);

    @Query("SELECT DISTINCT e FROM ExpenseEntity e JOIN e.payers p WHERE p.user.id = :userId ORDER BY e.createdAt DESC")
    List<ExpenseEntity> findExpensesPaidByUser(@Param("userId") Long userId);
}
