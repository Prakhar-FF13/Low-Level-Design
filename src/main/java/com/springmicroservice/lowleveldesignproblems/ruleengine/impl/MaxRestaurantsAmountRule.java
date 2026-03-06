package com.springmicroservice.lowleveldesignproblems.ruleengine.impl;

import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.MultiExpenseRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.models.Expense;
import com.springmicroservice.lowleveldesignproblems.ruleengine.models.ExpenseType;

import java.util.List;
import java.util.Optional;

public class MaxRestaurantsAmountRule implements MultiExpenseRule {
    @Override
    public Optional<Violations> validate(List<Expense> expense) {
        Optional<Double> sum = expense.
                stream().
                filter(e -> e.getExpenseType() == ExpenseType.RESTAURANT).
                map(Expense::getAmount).
                reduce(Double::sum);
        if (sum.isPresent() && sum.get() > 1000.0) {
            return Optional.of(new Violations(
                    "Max restaurant expense per trip can be 1000$"
            ));
        }
        return Optional.empty();
    }
}
