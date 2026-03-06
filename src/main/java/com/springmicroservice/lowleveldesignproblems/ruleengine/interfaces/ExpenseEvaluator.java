package com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces;

import com.springmicroservice.lowleveldesignproblems.ruleengine.models.Expense;

import java.util.List;

public interface ExpenseEvaluator {
    List<Violations> validate(
            List<Expense> expenses,
            List<SingleExpenseRule> singleExpenseRules,
            List<MultiExpenseRule> multiExpenseRules);
}
