package com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces;

import com.springmicroservice.lowleveldesignproblems.ruleengine.models.Expense;

import java.util.List;
import java.util.Optional;

public interface MultiExpenseRule {
    Optional<Violations> validate(List<Expense> expense);
}
