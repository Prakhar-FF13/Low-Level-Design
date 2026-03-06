package com.springmicroservice.lowleveldesignproblems.ruleengine.domain.rule;

import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Expense;

import java.util.List;
import java.util.Optional;

public class MaxTripAmountRule implements MultiExpenseRule {
    @Override
    public Optional<Violations> validate(List<Expense> expense) {
        Optional<Double> sum = expense.stream().map(Expense::getAmount).reduce((a, b) -> a + b);
        if (sum.isPresent() && sum.get() > 2000.0) {
            return Optional.of(new Violations(
                    "Max trip expense can be 2000$"));
        }
        return Optional.empty();
    }
}
