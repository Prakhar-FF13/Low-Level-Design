package com.springmicroservice.lowleveldesignproblems.ruleengine.services;

import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.ExpenseEvaluator;
import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.MultiExpenseRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.SingleExpenseRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.models.Expense;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SimpleExpenseEvaluator implements ExpenseEvaluator {
    @Override
    public List<Violations> validate(
            List<Expense> expenses,
            List<SingleExpenseRule> singleExpenseRules,
            List<MultiExpenseRule> multiExpenseRules
    ) {
        List<Violations> violations = expenses.
                stream().
                map(e ->
                        singleExpenseRules.
                                stream().
                                map(se -> se.validate(e)).
                                filter(Optional::isPresent).
                                map(Optional::get).
                                toList()
                ).flatMap(List::stream).collect(Collectors.toList());

        multiExpenseRules.stream().
                map(rule -> rule.validate(expenses)).
                filter(Optional::isPresent).
                map(Optional::get).
                forEach(violations::add);

        return violations;
    }
}
