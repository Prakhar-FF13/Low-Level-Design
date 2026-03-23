# Stock Broker LLD - Design Guideline

## 1. Requirements Summary

| # | Requirement | Priority |
|---|-------------|----------|
| 1 | Multiple stock exchanges might be sending data over to us | Must have |
| 2 | Stock broker should show the latest stock price | Must have |
| 3 | Should be able to store historical price of the stock | Good to have |

---

## 2. Design Principles & Patterns

### 2.1 Observer / Pub-Sub Pattern
- **Use when:** Multiple data producers (exchanges) need to push updates to consumers (broker displays, storage, etc.)
- **Components:**
  - **Subject/Publisher:** Exchanges that emit stock price updates
  - **Observer/Subscriber:** Components that react to updates (UI, storage, analytics)
- **Benefits:** Loose coupling, extensibility for new exchanges and subscribers

### 2.2 Key Design Considerations

| Concern | Guideline |
|---------|-----------|
| **Multiple Exchanges** | Each exchange is a separate publisher; broker aggregates/identifies data by exchange source |
| **Latest Price** | Maintain a `Map<StockSymbol, Price>` (or `Map<Exchange+Symbol, Price>`) for O(1) lookup |
| **Historical Data** | Use time-series storage: `Map<StockSymbol, List<PriceSnapshot>>` or similar; consider bounded history |
| **Thread Safety** | Exchanges may push concurrently; use `CopyOnWriteArrayList` for subscribers, concurrent maps for data |
| **Data Model** | Price should include: value, currency, timestamp, optionally exchange ID |

---

## 3. Class Design Checklist

### 3.1 Publisher Interface
- [ ] `subscribe(Subscriber s)` — register a subscriber
- [ ] `unsubscribe(Subscriber s)` — remove a subscriber
- [ ] `notify(StockUpdate update)` — broadcast update to all subscribers
- [ ] Publisher should uniquely identify itself (e.g., exchange name/ID)

### 3.2 Subscriber Interface
- [ ] `update(StockUpdate update)` — receive and process update
- [ ] Subscribers should not block; consider async if needed

### 3.3 Data Models
- [ ] **StockUpdate/PriceSnapshot:** symbol, value, currency, timestamp, exchangeId
- [ ] **StockBroker:** Aggregates updates, exposes `getLatestPrice(symbol)` and optionally `getHistory(symbol)`

### 3.4 Extensibility
- [ ] Easy to add new exchanges (NSE, NYSE, etc.)
- [ ] Easy to add new subscribers (dashboard, alerts, logging)

---

## 4. Anti-Patterns to Avoid

1. **Tight coupling:** Subscriber depending on concrete publisher types
2. **Uninitialized collections:** Lists/maps that cause NPE on first use
3. **Missing identity:** Updates without exchange/source identification when multiple exchanges exist
4. **Mutable shared state:** Returning internal maps without defensive copies
5. **Ignoring concurrency:** No thread safety when multiple exchanges push data

---

## 5. Recommended Architecture

```
[Exchange A] ──┐
[Exchange B] ──┼──► [Publisher Interface] ──► [Subscriber Interface]
[Exchange C] ──┘         │                            │
                         │                     ┌──────┴──────┐
                         │                     ▼             ▼
                         │              [LatestPriceView] [HistoryStore]
                         │
                         └──► [StockBroker] (optional aggregator/facade)
```

---

## 6. Testing Scenarios

| Scenario | Expected Behavior |
|----------|-------------------|
| Single exchange sends update | Subscriber receives and displays latest price |
| Multiple exchanges send same symbol | Broker shows latest per exchange, or best/aggregate |
| Subscribe then unsubscribe | Unsubscribed component stops receiving updates |
| Concurrent updates | No race conditions, consistent state |
| Historical storage | Past prices retrievable for a symbol |
