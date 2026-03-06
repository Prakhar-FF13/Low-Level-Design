package com.springmicroservice.lowleveldesignproblems.ruleengine.domain.engine;

import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Expense;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.rule.MultiExpenseRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.rule.SingleExpenseRule;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ExpenseEvaluatorEngine {

  private final List<SingleExpenseRule> singleExpenseRules;
  private final List<MultiExpenseRule> multiExpenseRules;

  public List<Violations> validate(List<Expense> expenses) {
    List<Violations> violations = expenses.stream()
        .flatMap(e -> singleExpenseRules.stream()
            .map(se -> se.validate(e))
            .filter(Optional::isPresent)
            .map(Optional::get))
        .collect(Collectors.toList());

    multiExpenseRules.stream()
        .map(rule -> rule.validate(expenses))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(violations::add);

    return violations;
  }
}
