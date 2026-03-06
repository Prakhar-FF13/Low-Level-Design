package com.springmicroservice.lowleveldesignproblems.ruleengine.domain.rule;

import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Expense;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Violations;

import java.util.Optional;

public interface SingleExpenseRule {
    Optional<Violations> validate(Expense expense);
}
