package com.springmicroservice.lowleveldesignproblems.ruleengine;

import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.ExpenseEvaluator;
import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.MultiExpenseRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.SingleExpenseRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.interfaces.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.models.Expense;
import com.springmicroservice.lowleveldesignproblems.ruleengine.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/rule-engine")
public class RuleEngineHandler {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private ExpenseEvaluator  expenseEvaluator;

    @PostMapping("/evaluate-expense")
    List<Violations> evaluateExpense(
            @RequestBody List<Expense> expenses
    ) {
        List<SingleExpenseRule> rules = new ArrayList<>();
        List<MultiExpenseRule> multiRules = new ArrayList<>();
        return expenseEvaluator.validate(expenses, rules, multiRules);
    }
}
