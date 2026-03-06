package com.springmicroservice.lowleveldesignproblems.ruleengine.impl;

import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.SingleExpenseRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.models.Expense;
import com.springmicroservice.lowleveldesignproblems.ruleengine.models.ExpenseType;
import lombok.NonNull;

import java.util.Optional;

public class EntertainmentExpenseRule implements SingleExpenseRule {
    @Override
    public Optional<Violations> validate(@NonNull Expense expense) {
        if (expense.getExpenseType() == ExpenseType.AIRFARE) {
            return Optional.of(new Violations(
               "Entertainment Expenses not allowed"
            ));
        }
        return Optional.empty();
    }
}
