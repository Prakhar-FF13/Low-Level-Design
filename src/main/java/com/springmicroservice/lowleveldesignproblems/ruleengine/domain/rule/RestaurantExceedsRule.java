package com.springmicroservice.lowleveldesignproblems.ruleengine.domain.rule;

import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Expense;
import lombok.NonNull;

import java.util.Optional;

public class RestaurantExceedsRule implements SingleExpenseRule {

    @Override
    public Optional<Violations> validate(@NonNull Expense expense) {
        if (expense
                .getExpenseType() == com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.ExpenseType.RESTAURANT
                && expense.getAmount() > 75) {
            return Optional.of(new Violations(
                    "Restaurant expense cannot exceed 75$"));
        }
        return Optional.empty();
    }
}
