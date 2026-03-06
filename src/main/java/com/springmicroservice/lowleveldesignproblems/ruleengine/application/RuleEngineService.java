package com.springmicroservice.lowleveldesignproblems.ruleengine.application;

import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.engine.ExpenseEvaluatorEngine;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Expense;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.rule.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RuleEngineService {

  private final ExpenseEvaluatorEngine engine;

  public RuleEngineService() {
    this.engine = new ExpenseEvaluatorEngine(
        List.of(new AirfareExpenseRule(), new EntertainmentExpenseRule(), new TwoFiftyMaxRule(),
            new RestaurantExceedsRule()),
        List.of(new MaxTripAmountRule(), new MaxRestaurantsAmountRule()));
  }

  public List<Violations> execute(List<Expense> expenses) {
    // Evaluate the given expenses using the core engine
    return engine.validate(expenses);
  }
}
