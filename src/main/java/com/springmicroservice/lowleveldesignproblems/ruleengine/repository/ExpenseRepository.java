package com.springmicroservice.lowleveldesignproblems.ruleengine.repository;

import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
}
