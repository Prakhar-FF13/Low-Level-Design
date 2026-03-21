# ATM Machine LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the ATM system using the State pattern in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| One user at a time? | Yes — sequential per session. |
| Card validation? | Via backend adapter (IATMBackendApi); mock in tests. |
| Invalid actions? | Throw IllegalStateException (e.g., withdraw without card). |
| PIN entry? | Out of scope for 1-hour interview; card insert implies validation. |
| Balance check / PIN change? | Out of scope. |

**Why this matters**: ATM behavior changes drastically by state; avoid if/else sprawl.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
ATM (Context)
├── state (ATMState)
├── atmMachineBalance
└── backend (IATMBackendApi)

ATMState (interface)
├── insertCard(ATM atm, Card card)
├── ejectCard(ATM atm)
└── requestCash(ATM atm, int amount)

IdleState (implements ATMState)
└── Waiting for card; reject requestCash, ejectCard

HasCardState (implements ATMState)
├── currentCard
└── Valid card in; reject insertCard; ejectCard → Idle; requestCash → Dispense (if balance OK)

DispenseState (implements ATMState)
├── dispenseAmount
└── Transient: dispense cash, update balance, → Idle

Card
├── cardNumber, pin, bank, name
└── (passed to backend for validation)

IATMBackendApi (interface)
└── validateCard(Card) → boolean
```

**Relationships**:
- ATM holds current `ATMState`; delegates all actions to `state.method(atm, ...)`
- States transition ATM: `atm.setState(new HasCardState(card))`

---

## Phase 3: Choose Design Pattern: State

| Principle | Application |
|-----------|-------------|
| **State Pattern** | ATMContext delegates to polymorphic State; no if/else on state. |
| **Single Responsibility** | Each state class owns its transitions and rejections. |
| **Open/Closed** | New state (e.g., ReadPINState) = new class; no changes to existing states. |
| **Dependency Injection** | IATMBackendApi injected; mock in tests. |

---

## Phase 4: State Transitions — Matches Code

### 4.1 IdleState

| Action | Behavior |
|--------|----------|
| insertCard | Call backend.validateCard(card); if OK → atm.setState(new HasCardState(card)); else throw |
| ejectCard | Throw IllegalStateException |
| requestCash | Throw IllegalStateException |

### 4.2 HasCardState

| Action | Behavior |
|--------|----------|
| insertCard | Throw IllegalStateException |
| ejectCard | atm.setState(new IdleState()) |
| requestCash | If atm.atmMachineBalance >= amount → atm.setState(new DispenseState(amount)); else throw |

### 4.3 DispenseState

| Action | Behavior |
|--------|----------|
| insertCard | Throw IllegalStateException |
| ejectCard | Throw IllegalStateException |
| requestCash | Update atm.atmMachineBalance; dispense (log); atm.setState(new IdleState()) |

---

## Phase 5: Package Structure (Matches Code)

```
atmmachine/
├── domain/
│   ├── ATM.java
│   ├── models/
│   │   ├── ATMState.java (interface)
│   │   └── Card.java
│   └── states/
│       ├── IdleState.java
│       ├── HasCardState.java
│       └── DispenseState.java
├── domain/adapters/
│   └── IATMBackendApi.java
├── (tests use AlwaysTrueATMBackendAPI or mock)
└── README.md
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| requestCash without card | IdleState → IllegalStateException |
| ejectCard without card | IdleState → IllegalStateException |
| insertCard when HasCard | HasCardState → IllegalStateException |
| requestCash exceeds balance | HasCardState → throw (or return error) |
| Card validation fails | IdleState → IllegalStateException |

---

## Phase 7: Implementation Order (Recommended)

1. **Models** — Card
2. **ATMState interface** — insertCard, ejectCard, requestCash
3. **IdleState** — transitions to HasCardState on valid insert
4. **HasCardState** — ejectCard → Idle; requestCash → Dispense or throw
5. **DispenseState** — dispense, → Idle
6. **IATMBackendApi** — interface; AlwaysTrueATMBackendAPI for tests
7. **ATM** — Context with state, delegate calls to state

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Avoid procedural if/else** | State pattern; behavior in state classes |
| **SOLID** | SRP per state; OCP for new states; DI for backend |
| **Testability** | Mock IATMBackendApi; unit test each state |
| **Clear transitions** | Each state explicitly sets next state on ATM |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| Insert card | IdleState.insertCard → HasCardState |
| Validate card | IATMBackendApi.validateCard |
| Eject card | HasCardState.ejectCard → IdleState |
| Withdraw cash | HasCardState.requestCash → DispenseState → IdleState |
| Reject invalid actions | Each state throws IllegalStateException |

---

## Run

```bash
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.atmmachine.*"
```
