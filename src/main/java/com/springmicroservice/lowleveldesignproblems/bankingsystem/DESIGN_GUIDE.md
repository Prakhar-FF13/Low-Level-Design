# Banking System LLD — Design Guide

Step-by-step notes for interviews and maintenance: how the `bankingsystem` package is structured, why key decisions were made, and how to extend it.

---

## Phase 1: Clarify & Scope (Do This First)

| Question | Assumption in this codebase |
|----------|-----------------------------|
| Storage? | In-memory via `AccountRepository` / `PaymentRepository` implementations. |
| Thread safety? | Single-threaded demo; repositories use `ConcurrentHashMap` for safe publication. |
| Timestamps? | Milliseconds (`int` / `Integer`); `null` often means “no upper bound” (`Integer.MAX_VALUE`). |
| Idempotency of operations? | Not required; each call is a new logical event. |
| REST / CLI? | No; wire `AccountsService` in tests or a future adapter. |

**Out of scope (current codebase)**

- Persistence to disk or SQL.
- Network APIs and authentication.
- Interest, fees, overdraft, multi-currency.

---

## Phase 2: Core Entities & Relationships

```
Account
├── accountId, balance (materialized for current state)
└── transactions: List<Transaction> — append-only ledger per account

Transaction
├── transactionId, type, amount, timestamp, peerAccountId?, outgoing (for transfers / pay)

Payment (aggregate for pay + cashback lifecycle)
├── paymentId, accountId, amount, payTimestamp
├── cashbackDueTimestamp (= pay + 86_400_000 ms), cashbackAmount (= floor(2%))
└── status: IN_PROGRESS | CASHBACK_RECEIVED

EventLog (cross-cutting audit stream)
├── eventId, timestamp, EventType
```

**Relationships**

- `AccountsService` orchestrates accounts and delegates payments to `PaymentService`.
- `PaymentService` owns cashback application order and payment CRUD.
- `EventPublisher` receives domain events (`ACCOUNT_CREATION`, `DEPOSIT`, `TRANSFER`, `PAYMENT`, `CASHBACK`).
- After **merge**, survivor account holds **merged** `Transaction` lists; `Payment.accountId` is **reassigned** from absorbed account to survivor.

---

## Phase 3: Ports, Adapters & SOLID

| Abstraction | Role |
|-------------|------|
| `AccountRepository` | Hide map (or future DB) behind CRUD + `findAll` + `delete`. |
| `PaymentRepository` | Payments by id; `findAll` for cashback sweep; `reassignAccountId` for merge. |
| `EventPublisher` | `@FunctionalInterface` — `EventManagerService` appends to an in-memory list. |

**Why**

- **Dependency inversion**: services depend on interfaces, not `ConcurrentHashMap`.
- **Single responsibility**: `Account` holds invariants for deposit / transfer / pay / cashback lines; `PaymentService` sequences cashback before other work at the same clock time.
- **Open/closed**: new storage = new repository implementation.

---

## Phase 4: Level-by-Level Behavior (Matches README)

### Level 1 — Accounts & ledger

- Deposits and transfers reject non-positive amounts.
- Transfers debit/credit atomically in one call path; both legs recorded with `TransactionType.TRANSFER` and `outgoing` true on source, false on destination.

### Level 2 — Top spenders

- Outgoing = sum of amounts for `TRANSFER` with `outgoing` **or** `PAY` (after Level 3), with `transaction.timestamp <= query cutoff`.
- Sort by total descending, then `accountId` lexicographically; take top N (zeros allowed if they rank in top N).

### Level 3 — Pay & cashback

- `pay` debits, creates `Payment` pending, publishes `PAYMENT` event.
- **Cashback ordering**: before any other operation whose logical time is `T`, apply all pending cashbacks with `cashbackDueTimestamp <= T`, sorted by `(cashbackDueTimestamp, paymentId)`.
- `getPaymentStatus` runs the same flush, then reads persisted `Payment.status` and maps to API strings (`IN PROGRESS` / `CASHBACK RECEIVED`).

### Level 4 — Merge & historical balance

- **mergeAccounts(account1, account2)**: survivor = `account1`; `absorbMergedAccount` adds balance and merges transaction lists sorted by `(timestamp, transactionId)`; payments reassigned to `account1`; `account2` **deleted**.
- **getBalance(timestamp, accountId, timeAt)**: flush cashbacks up to `timestamp`, then **replay** sorted transactions with effect rules (deposit/cashback +, pay −, transfer ± by `outgoing`) up to `timeAt` inclusive.

---

## Phase 5: Interview Talking Points

1. **Why `outgoing` on `Transaction`?** Both legs of a transfer use `TRANSFER`; the flag distinguishes spend (source) from receive (destination) for Level 2/3 without a second enum explosion.
2. **Why not recompute top spenders from a global log only?** Per-account ledger keeps queries local and matches “account-centric” banking APIs.
3. **Merge and payment ids:** Reassigning `Payment.accountId` preserves old `paymentId` values while routing queries through the survivor account — satisfies “queryable from account 1.”
4. **Historical balance vs live balance:** Live `Account.balance` is updated eagerly; `getBalance` recomputes from ledger for `timeAt` — catches merged history and ordering without a separate event store.

---

## Phase 6: Testing

- Unit tests: `AccountsServiceTest` (merge, payment reassignment, `getBalance` replay, full replay vs pay).
- Run: `./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.bankingsystem.**"`

---

## Phase 7: Extensions (Roadmap)

- Persist repositories (JPA) behind same interfaces.
- Replace linear cashback scan with a time-ordered index of due payments.
- Add `ACCOUNT_MERGED` event and audit trail for compliance.
- Concurrent transfers: per-account locking or serial execution per account id.
