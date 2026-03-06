package com.springmicroservice.lowleveldesignproblems.ruleengine.domain.rule;

import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Expense;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.ExpenseType;
import lombok.NonNull;

import java.util.Optional;

public class AirfareExpenseRule implements SingleExpenseRule {
    @Override
    public Optional<Violations> validate(@NonNull Expense expense) {
        if (expense.getExpenseType() == ExpenseType.AIRFARE) {
            return Optional.of(new Violations(
                    "Airfare Expense not allowed"));
        }
        return Optional.empty();
    }
}
