# Stock Exchange LLD — Design Guide for Strong Interview Performance

> **Narrative & diagrams:** [README.md](./README.md) describes the implemented solution, REST API, and mermaid diagrams.

This guide walks you through designing the in-memory trading system (order placement, matching, order book) in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| User registration | Out of scope — dummy users pre-registered. |
| Match condition | Buy price = Sell price (or buy ≥ sell; clarify). |
| Partial fills allowed? | Yes — track remaining quantity; order can be partially filled. |
| Modify order semantics | Cancel + re-place (loses time priority) or in-place update (preserves priority)? |
| Order types | Limit orders only (price specified); market orders out of scope. |
| Trade expiry (optional) | Implement if time permits — auto-cancel after T seconds. |

**Why this matters**: Matching logic and concurrency requirements drive the entire design.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
User
├── userId
├── userName
├── phoneNumber
└── emailId

Order
├── orderId
├── userId
├── orderType (BUY / SELL)
├── symbol (RELIANCE, WIPRO, etc.)
├── quantity
├── price
├── orderAcceptedTimestamp
├── status (ACCEPTED / REJECTED / CANCELED)
└── filledQuantity (for partial fills; remaining = quantity - filledQuantity)

Trade
├── tradeId
├── tradeType (BUY / SELL)
├── buyerOrderId
├── sellerOrderId
├── symbol
├── quantity
├── price
└── tradeTimestamp

OrderBook (per symbol)
├── symbol
├── buySide (TreeMap<Price, Queue<Order>> — highest buy first)
├── sellSide (TreeMap<Price, Queue<Order>> — lowest sell first)
└── lock (ReentrantLock per symbol)

ExchangeService (facade)
├── IOrderBook
├── OrderMatchingExecutor
└── ExecutorService (injected)

OrderMatchingExecutor
├── IOrderBook
├── OrderMatchingStrategy
└── TradeService (port → TradeRepository)

```

**Relationships**:
- `Order` → `User` (many-to-one, via userId; user model optional for REST—`userId` string suffices)
- `Trade` → `Order` (buyerOrderId, sellerOrderId)
- `IOrderBook` holds resting orders; `ExchangeService` orchestrates place/cancel/modify; `OrderMatchingExecutor` runs match + trade persistence + cleanup

---

## Phase 3: Choose Design Patterns

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|----------------------|
| **Repository** | Trade storage (`TradeRepository`); order book via `IOrderBook` | Abstraction for in-memory vs DB; pluggable implementations per README. |
| **Order Book (domain model)** | Per-symbol resting orders | Encapsulates storage + concurrency; matching algorithm in strategy/executor. |
| **Fine-grained locking** | Per-symbol ReentrantLock | Concurrency without global bottleneck; no deadlocks if lock order is consistent. |
| **Strategy (optional)** | Matching rules | If multiple matching algorithms (e.g. pro-rata) — limit to price-time for scope. |

---

## Phase 4: Core Logic — Aligned with Code

### 4.1 Order Placement Flow

```
1. Validate: userId exists, quantity > 0, price > 0, symbol valid
2. Create Order (ACCEPTED status, timestamp)
3. Save to OrderRepository
4. Acquire lock for symbol
5. Call OrderBook.matchAndAdd(incomingOrder)
   - Match against opposite side (buy vs sell book)
   - Generate Trade(s), save to TradeRepository
   - Update order filledQuantity / status
   - Add remaining qty to same-side book if any
6. Release lock
7. Return order (with status, fills)
```

### 4.2 Matching Algorithm (Price-Time Priority)

```
matchAndAdd(incomingOrder):
  if incomingOrder.type == BUY:
    while incomingOrder.remainingQty > 0 and sellSide has orders:
      bestSell = sellSide.firstEntry()  // lowest price
      if bestSell.price > incomingOrder.price:
        break
      fillQty = min(remaining(incomingOrder), remaining(bestSell))
      executeTrade(buyer=incomingOrder, seller=bestSell, qty=fillQty)
      update filledQuantity; remove/reduce bestSell
    if incomingOrder.remainingQty > 0:
      addToBuySide(incomingOrder)
  else:  // SELL — symmetric, match against buySide (highest buy first)
```

### 4.3 Order Cancellation Flow

```
1. Lookup order by orderId; verify userId matches (ownership)
2. If order.status != ACCEPTED or already fully filled → reject
3. Acquire lock for symbol
4. Remove order from OrderBook (if resting)
5. Update order.status = CANCELED
6. Release lock
7. Return
```

### 4.4 Order Modification Flow

```
1. Lookup order; verify ownership
2. If not cancelable (filled, canceled, rejected) → reject
3. Cancel existing order (remove from book)
4. Create new order with same orderId or new orderId (clarify semantics)
5. Place new order (re-validate, match, add to book)
```

### 4.5 Order Status Query

```
1. Lookup order by orderId
2. Return: status, filledQuantity, quantity, price, timestamp
```

---

## Phase 5: Package Structure (Matches Code)

```
stockexchange/
├── api/
│   ├── controller/
│   │   ├── ExchangeOrderController.java
│   │   └── TradeController.java
│   └── exception/
│       └── StockExchangeExceptionHandler.java
├── config/
│   └── StockExchangeConfig.java
├── dto/                          # PlaceOrderRequest, OrderResponse, TradeResponse, …
├── exceptions/                   # TradingException and subclasses
├── models/
│   ├── User.java, Stock.java
│   ├── Order.java, OrderType.java, OrderStatus.java
│   └── Trade.java
├── orderbook/
│   ├── IOrderBook.java
│   └── impl/OrderBook.java
├── repository/
│   ├── TradeRepository.java
│   └── InMemoryTradeRepository.java
├── services/
│   ├── ExchangeService.java
│   ├── OrderMatchingExecutor.java
│   ├── TradeService.java         # interface
│   ├── DefaultTradeService.java
│   └── strategies/
│       ├── OrderMatchingStrategy.java
│       └── FifoOrderMatchingStrategy.java
├── DESIGN_GUIDELINE.md
└── README.md
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Place order | userId exists; quantity > 0; price > 0; symbol in allowed list |
| Cancel order | Order exists; userId matches; order not fully filled or already canceled |
| Modify order | Same as cancel; define if orderId preserved or new ID |
| Zero/negative quantity or price | Reject with REJECTED status |
| Unknown symbol | Reject or configurable symbol list |
| Concurrent cancel + match | Lock per symbol; check status before executing trade |
| Partial fill | Update filledQuantity; order stays in book until fully filled |

---

## Phase 7: Implementation Order (Recommended)

1. **Models** — Order, OrderType, OrderStatus, Trade (+ User if needed)
2. **IOrderBook + OrderBook** — per-symbol lists, locks, `findOrderById`
3. **TradeRepository + InMemoryTradeRepository**
4. **OrderMatchingStrategy + FifoOrderMatchingStrategy**
5. **TradeService (interface) + DefaultTradeService**
6. **OrderMatchingExecutor** — per-symbol match mutex, strategy, `recordTrades`, prune filled
7. **ExchangeService** — place/cancel/modify + inject `ExecutorService`
8. **StockExchangeConfig** — Spring `@Bean` wiring
9. **REST** — DTOs, `ExchangeOrderController`, `TradeController`, `StockExchangeExceptionHandler`
10. **Integration tests** — `@SpringBootTest` + `MockMvc` (see `StockExchangeIntegrationTest`)

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Correct matching** | Price-time priority; FIFO at same price; partial fills handled |
| **Concurrency** | Per-symbol locks; thread-safe collections; no deadlocks |
| **Clean separation** | OrderBook = resting orders; OrderMatchingExecutor = match + trades; ExchangeService = API + scheduling |
| **Abstraction** | Repository interfaces; in-memory swappable with persistent store |
| **Validation** | Reject invalid orders; REJECTED/CANCELED status; clear error handling |
| **Testability** | Unit test OrderBook matching in isolation; mock repositories |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| Place order | `ExchangeService.placeOrder` → `IOrderBook.addOrder` → async `OrderMatchingExecutor.executeMatch` |
| Execute trade on price match | `FifoOrderMatchingStrategy.matchOrders` (equal price, FIFO order) |
| Cancel order | `ExchangeService.cancelOrder` → `IOrderBook.removeOrder` |
| Modify order | `ExchangeService.modifyOrder` → `IOrderBook.updateOrder` → async match |
| Query order status | REST `GET /api/exchange/orders/{id}` → `IOrderBook.findOrderById` |
| Order book per symbol | `OrderBook`: `Map<stockId, List<Order>>` + per-symbol `ReentrantReadWriteLock` |
| Thread-safe matching | Per-`stockId` mutex in `OrderMatchingExecutor` + book locks |
| Store executed trades | `TradeService` → `TradeRepository` |

---

## 10. Data Structure Choice (Order Book)

| Side | Structure | Ordering |
|------|-----------|----------|
| **Buy** | `TreeMap<Price, Queue<Order>>` or `ConcurrentSkipListMap` | Descending (highest buy first) |
| **Sell** | `TreeMap<Price, Queue<Order>>` or `ConcurrentSkipListMap` | Ascending (lowest sell first) |

Within each price level: `LinkedList` or `ArrayDeque` for FIFO. If using a single PriorityQueue instead of price-level aggregation, use a comparator: price first (desc for buys, asc for sells), then timestamp for tie-break.

---

## 11. Anti-Patterns to Avoid

| Anti-Pattern | Better Approach |
|--------------|-----------------|
| Global lock for entire exchange | Per-symbol locks; minimize lock scope |
| Inline storage logic in domain | Repository interfaces; pluggable implementations |
| Modifying order in-place during match | Update filledQuantity atomically; avoid mutating shared refs incorrectly |
| Ignoring partial fills | Track `filledQuantity`; `remainingQuantity = quantity - filledQuantity` |
| Blocking under lock | No I/O or slow ops under lock; matching only |
| No validation | Validate quantity, price, user, symbol before processing |
| Returning mutable internal state | Return defensive copies or immutable DTOs |
| Mixed concerns | Keep executor vs facade vs book responsibilities separate |

---

## 12. Testing Scenarios

| Scenario | Expected Behavior |
|----------|-------------------|
| Place buy, place matching sell | Trade executes; both orders marked filled |
| Place buy, place sell at higher price | No match; both rest in book |
| Multiple sells at same price | Oldest sell matches first (FIFO) |
| Partial fill | Remaining qty stays in book; status shows partial fill |
| Cancel resting order | Order removed from book; status CANCELED |
| Cancel already-matched order | Reject or no-op with appropriate status |
| Concurrent place/cancel | No corruption; correct final state; no deadlock |
| Query order status | Returns status, filledQty, quantity, price, timestamp |

---

## 13. This codebase — REST & tests

- **Entry:** Spring Boot (`LowLevelDesignProblemsApplication`) loads `StockExchangeConfig` beans.
- **Orders:** `POST/GET/PUT/DELETE` under `/api/exchange/orders` (see [README.md](./README.md)).
- **Trades:** `GET /api/exchange/trades/...` for persisted fills.
- **Integration tests:** `src/test/java/.../stockexchange/api/StockExchangeIntegrationTest.java` — full context + `MockMvc`, polling helper for async matching.

---

## Run

```bash
# REST server (default port, e.g. 8080)
./gradlew bootRun

# Stock exchange tests only
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.stockexchange.*"
```
