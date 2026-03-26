# Splitwise LLD — Design Guide (Interview-Oriented)

> **Implemented solution & API table:** [README.md](./README.md)

This guide is the **interview narrative**: what to clarify first, which entities to draw, how balances and settlement work, and a sensible **implementation order** if you code live.

---

## Phase 1: Clarify & Scope (Do This First)

| Topic | Suggested assumption |
|--------|----------------------|
| Auth / sessions | Out of scope — identify users by `userId` from API. |
| Currency | Single currency; use `BigDecimal` with 2 decimal places. |
| Who can be on an expense | Only **members** of that group. |
| Multiple payers | Yes — list of (user, amount); sum must equal expense total. |
| Equal split | Split total **evenly** across listed participants; handle **remainder cents** (e.g. first participants get +1¢). |
| Percent split | Percentages sum to **100**; last participant absorbs **rounding** so row sums equal total. |
| Settlement | **Suggestions only** — compute from balances; optional “mark paid” can be a follow-up. |
| Settlement history DB | Optional bonus; not required to compute suggestions. |

---

## Phase 2: Core Entities (Whiteboard)

**Nouns:** User, Group, Expense.

**Relationships:**

- **Group** — many-to-many **Members** (User).
- **Expense** — belongs to one **Group**; has **total amount**, **split type** (equal / percent), **created time**.
- **ExpensePayer** — (expense, user, paidAmount). Several rows = several people paid.
- **ExpenseSplit** — (expense, user, owedAmount); optional **percent** for audit on % splits.

**Why two child tables?**  
“Who paid” and “who owes what share” are different concepts (e.g. one person pays for everyone).

---

## Phase 3: Balance Math (Explain Clearly)

For **one expense** and **one user U**:

- Add **paidAmount** if U is a payer.
- Subtract **owedAmount** from U’s split row.

**Net for U in the group** = sum of that quantity over **all expenses** in the group.

- **Net > 0** → others owe U on aggregate.
- **Net < 0** → U owes others on aggregate.

No separate “balance table” is required if you always derive from expenses.

---

## Phase 4: Settlement Suggestions (Minimal Transfers)

Input: map **userId → net balance** (from Phase 3).

Goal: a **small** list of directed payments `(from, to, amount)` so that if everyone pays those amounts, nets go to zero (within cent precision).

**Greedy approach** (implemented):

1. Split users into **creditors** (net > 0) and **debtors** (net < 0).
2. Repeat: take **largest creditor** and **largest debtor** (by absolute need).
3. Pay `min(creditor_balance, -debtor_balance)` from debtor to creditor; reduce both; re-insert if still non-zero.

**Interview note:** This does not claim to be globally optimal for all cost functions; it is standard, simple, and easy to code.

---

## Phase 5: API Shape (REST)

Group under one base path, e.g. `/api/splitwise`:

- CRUD-style **users** and **groups** (create + get; members via sub-resource).
- **POST expenses** under `groups/{id}/expenses` with body: total, split type, payers, and either equal participant ids or percent lines.
- **GET balances** and **GET settlement-suggestions** under the group.

Keep DTOs as **records**; validate with **Jakarta Validation** on POST bodies.

---

## Phase 6: Implementation Order (If Coding)

1. **Entities + enums** (`User`, `Group`, `Expense`, `ExpensePayer`, `ExpenseSplit`, `SplitType`).
2. **Repositories** — Spring Data; `findByIdWithMembers` for groups; expense queries by group and “user owes / user paid”.
3. **Service** — `addExpense` validation + `equalShares` / `percentShares` private methods; then `netBalances`; then `simplify`.
4. **Controller + DTOs + exception handler**.
5. **Manual curl** or a single `@SpringBootTest` smoke test.

---

## Phase 7: Edge Cases & Validations

| Case | Handling |
|------|----------|
| Payer amounts ≠ total | Reject. |
| Duplicate payer on same expense | Reject. |
| Non-member payer or participant | Reject. |
| PERCENT sum ≠ 100 (after scale) | Reject. |
| Remove member who appears on an expense | Reject (or soft-delete — out of scope here). |
| Empty group / no expenses | Balances all zero; suggestions empty. |

---

## Phase 8: What to Say About “Layers”

- **Persistence** — JPA entities map to tables; repositories stay thin.
- **Service** — all rules and math; no HTTP here.
- **Controller** — HTTP + mapping; thin.
- **Exception** — one type with **not found** vs **bad request** keeps the handler small.

This matches the **simplified** package layout in the repo: **one** `SplitwiseService`, **one** `SplitwiseController`, **one** DTO holder class.

---

## Anti-Patterns to Avoid in Interview

- Storing **only** “who paid” without **split lines** — cannot support unequal owed amounts.
- Using **double** for money — use `BigDecimal` (or long cents).
- Computing balances from a **cached balance** table without defining updates on every expense change — derived balances from expenses are easier to reason about initially.

---

## Optional Extensions (If Time)

- **Settlement records** table when users confirm a payment (audit trail).
- **Soft delete** for expenses.
- **Multi-currency** with FX rates (large scope).
