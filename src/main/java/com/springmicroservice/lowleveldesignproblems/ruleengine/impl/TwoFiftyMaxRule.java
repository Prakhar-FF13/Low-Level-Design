package com.springmicroservice.lowleveldesignproblems.ruleengine.impl;

import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.SingleExpenseRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.models.Expense;

import java.util.Optional;

public class TwoFiftyMaxRule implements SingleExpenseRule {
    @Override
    public Optional<Violations> validate(Expense expense) {
        if (expense.getAmount() > 250) {
            return Optional.of(new Violations(
                    "A single expense can be of max 250$"
            ));
        }
        return Optional.empty();
    }
}
