package com.springmicroservice.lowleveldesignproblems.ruleengine.domain.rule;

import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Expense;

import java.util.Optional;

public class TwoFiftyMaxRule implements SingleExpenseRule {
    @Override
    public Optional<Violations> validate(Expense expense) {
        if (expense.getAmount() > 250) {
            return Optional.of(new Violations(
                    "A single expense can be of max 250$"));
        }
        return Optional.empty();
    }
}
