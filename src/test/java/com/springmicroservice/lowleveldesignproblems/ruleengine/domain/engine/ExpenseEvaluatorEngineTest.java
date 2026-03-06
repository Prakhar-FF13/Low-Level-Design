package com.springmicroservice.lowleveldesignproblems.ruleengine.domain.engine;

import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Expense;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.ExpenseType;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.model.Violations;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.rule.AirfareExpenseRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.rule.MaxTripAmountRule;
import com.springmicroservice.lowleveldesignproblems.ruleengine.domain.rule.RestaurantExceedsRule;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpenseEvaluatorEngineTest {

  @Test
  void testAirfareViolation() {
    // Arrange: Inject only the rules we want to test (No Spring needed!)
    ExpenseEvaluatorEngine engine = new ExpenseEvaluatorEngine(
        List.of(new AirfareExpenseRule()),
        List.of());

    Expense airfare = Expense.builder()
        .expenseType(ExpenseType.AIRFARE)
        .amount(500.0)
        .build();

    // Act
    List<Violations> violations = engine.validate(List.of(airfare));

    // Assert
    assertFalse(violations.isEmpty());
    assertEquals("Airfare Expense not allowed", violations.get(0).getDescription());
  }

  @Test
  void testRestaurantExpenseViolation() {
    ExpenseEvaluatorEngine engine = new ExpenseEvaluatorEngine(
        List.of(new RestaurantExceedsRule()),
        List.of());

    Expense dinner = Expense.builder()
        .expenseType(ExpenseType.RESTAURANT)
        .amount(80.0)
        .build();

    List<Violations> violations = engine.validate(List.of(dinner));

    assertFalse(violations.isEmpty());
    assertEquals("Restaurant expense cannot exceed 75$", violations.get(0).getDescription());
  }

  @Test
  void testMaxTripAmountViolation() {
    ExpenseEvaluatorEngine engine = new ExpenseEvaluatorEngine(
        List.of(),
        List.of(new MaxTripAmountRule()));

    Expense cab = Expense.builder().expenseType(ExpenseType.JEWELLERY).amount(1000.0).build();
    Expense hotel = Expense.builder().expenseType(ExpenseType.JEWELLERY).amount(1500.0).build();

    List<Violations> violations = engine.validate(List.of(cab, hotel));

    // It exceeds 2000 combined
    assertFalse(violations.isEmpty());
    assertEquals("Max trip expense can be 2000$", violations.get(0).getDescription());
  }

  @Test
  void testNoViolations() {
    ExpenseEvaluatorEngine engine = new ExpenseEvaluatorEngine(
        List.of(new RestaurantExceedsRule(), new AirfareExpenseRule()),
        List.of(new MaxTripAmountRule()));

    Expense smallDinner = Expense.builder().expenseType(ExpenseType.RESTAURANT).amount(50.0).build();
    Expense someHotel = Expense.builder().expenseType(ExpenseType.JEWELLERY).amount(500.0).build();

    List<Violations> violations = engine.validate(List.of(smallDinner, someHotel));

    assertTrue(violations.isEmpty());
  }
}
