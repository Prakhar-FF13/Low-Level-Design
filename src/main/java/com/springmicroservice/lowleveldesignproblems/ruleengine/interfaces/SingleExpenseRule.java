package com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces;

import com.springmicroservice.lowleveldesignproblems.ruleengine.models.Expense;

import java.util.Optional;

public interface SingleExpenseRule {
    Optional<Violations> validate(Expense expense);
}
