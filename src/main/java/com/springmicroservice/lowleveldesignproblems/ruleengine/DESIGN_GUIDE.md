# Rule Engine LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the Business Expense Rule Engine in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| Single vs multi-expense rules? | Both — SingleExpenseRule (per expense), MultiExpenseRule (trip-level). |
| Rule priority? | Out of scope — execute as provided; aggregate all violations. |
| Database? | Out of scope — in-memory rules. |
| Output? | List of Violations (message per rule). |

**Why this matters**: Separating single vs multi keeps Interface Segregation; engine stays open for new rules.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
Expense
├── amount
├── type (ExpenseType)
└── description

ExpenseType (enum)
└── RESTAURANT, JEWELLERY, AIRFARE, ENTERTAINMENT

Violations
└── message (e.g., "Airfare not allowed")

SingleExpenseRule (interface)
└── validate(Expense) → Optional<Violations>

MultiExpenseRule (interface)
└── validate(List<Expense>) → Optional<Violations>

ExpenseEvaluatorEngine
├── singleExpenseRules (List<SingleExpenseRule>)
├── multiExpenseRules (List<MultiExpenseRule>)
└── validate(List<Expense>) → List<Violations>

RuleEngineService
├── engine (ExpenseEvaluatorEngine)
└── execute(List<Expense>) → List<Violations>
```

**Relationships**:
- Engine holds two lists of rules; streams through both; aggregates Violations

---

## Phase 3: Choose Design Pattern: Extensible Rules

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|------------------------|
| **Strategy / Plugin** | SingleExpenseRule, MultiExpenseRule | Each rule is isolated; OCP. |
| **Interface Segregation** | Single vs Multi | Rules that act on one expense don't implement multi logic. |
| **Dependency Injection** | Engine receives List of rules | Engine never changes when rules added. |
| **Aggregation** | Engine collects all violations | No short-circuit; report everything. |

---

## Phase 4: Core Logic — Matches Code

### 4.1 Single-Expense Rules (from requirements)

| Rule | Logic |
|------|-------|
| RestaurantExceedsRule | type == RESTAURANT && amount > 75 |
| AirfareExpenseRule | type == AIRFARE (not allowed) |
| EntertainmentExpenseRule | type == ENTERTAINMENT (not allowed) |
| TwoFiftyMaxRule | amount > 250 (any type) |

### 4.2 Multi-Expense Rules

| Rule | Logic |
|------|-------|
| MaxRestaurantsAmountRule | sum(expenses where type==RESTAURANT) > 1000 |
| MaxTripAmountRule | sum(all expenses) > 2000 |

### 4.3 Engine Flow

```
validate(expenses):
  violations = []
  for each expense: for rule in singleExpenseRules
    if rule.validate(expense).isPresent()
      violations.add(rule.validate(expense).get())
  for rule in multiExpenseRules
    if rule.validate(expenses).isPresent()
      violations.add(rule.validate(expenses).get())
  return violations
```

---

## Phase 5: Package Structure (Matches Code)

```
ruleengine/
├── domain/
│   ├── model/
│   │   ├── Expense.java
│   │   ├── ExpenseType.java
│   │   └── Violations.java
│   └── rule/
│       ├── SingleExpenseRule.java
│       ├── MultiExpenseRule.java
│       ├── AirfareExpenseRule.java
│       ├── RestaurantExceedsRule.java
│       ├── EntertainmentExpenseRule.java
│       ├── TwoFiftyMaxRule.java
│       ├── MaxRestaurantsAmountRule.java
│       └── MaxTripAmountRule.java
├── application/
│   └── RuleEngineService.java
├── api/
│   └── RuleEngineHandler.java
└── README.md
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Empty expenses | Multi rules still run (trip total = 0) |
| Unknown expense type | Rules ignore or handle per logic |
| Multiple violations | All collected, not first-only |

---

## Phase 7: Implementation Order (Recommended)

1. **Models** — Expense, ExpenseType, Violations
2. **SingleExpenseRule** — interface
3. **Concrete single rules** — Airfare, RestaurantExceeds, Entertainment, TwoFiftyMax
4. **MultiExpenseRule** — interface
5. **Concrete multi rules** — MaxRestaurantsAmount, MaxTripAmount
6. **ExpenseEvaluatorEngine** — wire lists, validate
7. **RuleEngineService** — execute
8. **RuleEngineHandler** (API) — optional

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Open/Closed** | New rule = new class; Engine unchanged |
| **Single Responsibility** | Each rule does one check |
| **Interface Segregation** | Single vs Multi — no forcing multi logic on single rules |
| **Testability** | Unit test each rule in isolation |
| **Aggregation** | Return all violations, not first failure |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| Restaurant ≤ $75 | RestaurantExceedsRule |
| No airfare | AirfareExpenseRule |
| No entertainment | EntertainmentExpenseRule |
| Single expense ≤ $250 | TwoFiftyMaxRule |
| Trip total ≤ $2000 | MaxTripAmountRule |
| Restaurant total per trip ≤ $1000 | MaxRestaurantsAmountRule |
| Orchestration | ExpenseEvaluatorEngine |
| API | RuleEngineService, RuleEngineHandler |

---

## Run

```bash
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.ruleengine.*"
```
