package com.springmicroservice.lowleveldesignproblems.ruleengine.impl;

import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.MultiExpenseRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.models.Expense;

import java.util.List;
import java.util.Optional;

public class MaxTripAmountRule implements MultiExpenseRule {
    @Override
    public Optional<Violations> validate(List<Expense> expense) {
        Optional<Double> sum = expense.stream().map(Expense::getAmount).reduce(Double::sum);
        if (sum.isPresent() && sum.get() > 2000.0) {
            return Optional.of(new Violations(
                    "Max trip expense can be 2000$"
            ));
        }
        return Optional.empty();
    }
}
