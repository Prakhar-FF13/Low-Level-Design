package com.springmicroservice.lowleveldesignproblems.ruleengine.api;

import com.springmicroservice.lowleveldesignproblems.ruleengine.application.RuleEngineService;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Expense;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Violations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rule-engine")
public class RuleEngineHandler {

    private final RuleEngineService ruleEngineService;

    public RuleEngineHandler(RuleEngineService ruleEngineService) {
        this.ruleEngineService = ruleEngineService;
    }

    @PostMapping("/evaluate-expense")
    public List<Violations> evaluateExpense(@RequestBody List<Expense> expenses) {
        return ruleEngineService.execute(expenses);
    }
}
