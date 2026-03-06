package com.springmicroservice.lowleveldesignproblems.ruleengine.impl;

import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.SingleExpenseRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.models.Expense;
import lombok.NonNull;

import java.util.Optional;

public class RestaurantExceedsRule implements SingleExpenseRule {

    @Override
    public Optional<Violations> validate(@NonNull Expense expense) {
        if (expense.getAmount() > 75) {
            return Optional.of(new Violations(
                    "Restaurant expense cannot exceed 75$"
            ));
        }
        return Optional.empty();
    }
}
