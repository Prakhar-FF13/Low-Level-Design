# Online Auction LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the Online Auction system in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| Is there auction lifecycle (e.g., OPEN, CLOSED, ENDED)? | Yes — auctions can be OPEN or CLOSED. |
| Can a buyer bid multiple times in the same auction? | Yes — but only one active bid per buyer (update replaces). |
| What happens when auction closes? | Winner is selected; seller P&L is calculated. |
| Is participation cost paid when joining or when bidding? | When placing first bid in an auction. |
| Preferred buyer: is it global or per-auction? | Global — based on participating in >1 auction ever. |

**Why this matters**: Interviewers like seeing you nail requirements before diving into code.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
User (abstract)
├── Buyer  — participates in auctions, places bids
└── Seller — creates auctions, receives P&L

Auction
├── id
├── lowestBidLimit, highestBidLimit
├── seller (UserId)
├── participationCost
├── status (OPEN / CLOSED)
└── product (optional — what’s being sold)

Bid
├── id
├── auctionId
├── buyerId
├── amount
└── status (ACTIVE / WITHDRAWN)

Participation (tracks who paid to join)
├── auctionId
├── buyerId
├── paidAt
```

**Relationships**:
- `User` 1 — * `Auction` (seller)
- `User` * — * `Auction` (buyer, via bids)
- `Auction` 1 — * `Bid`
- `Buyer` has at most 1 active `Bid` per `Auction`

---

## Phase 3: Choose Design Patterns

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|------------------------|
| **Strategy** | Winner selection logic | Different algorithms (unique-highest, preferred-buyer tiebreaker). Easy to extend. |
| **Repository** | Data access | In-memory for LLD; easily swapped with DB later. |
| **Service Layer** | Business logic | Clear separation: controllers call services, services orchestrate. |
| **State** (optional) | Auction lifecycle | OPEN vs CLOSED behavior (bid allowed vs not). |
| **Factory** (optional) | Auction / Bid creation | Centralized validation. |

---

## Phase 4: Core Algorithms — Write Them Clearly

### 4.1 Winner Selection

```
1. Get all ACTIVE bids for the auction
2. Group by amount → Map<Double, List<Bid>>
3. Find amounts that appear exactly once (unique bids)
4. If none → NO_WINNER
5. Get max(unique amounts)
6. If tie on max amount → apply preferred-buyer rule
   - Among bidders at max amount, prefer "preferred buyers"
   - If multiple preferred → next highest unique bid, etc.
7. Return winning bid or null
```

**Preferred buyer tiebreaker** (when multiple bidders at same max unique bid):
- Preferred buyer (participated in >1 auction) wins
- If multiple preferred at same max → fallback to next lower unique bid, repeat

### 4.2 Seller P&L

```
avg = (lowestBidLimit + highestBidLimit) / 2
numBidders = count of distinct buyers who placed any bid (active or withdrawn)

If winner exists:
  profit = winningBidAmount + (numBidders * 0.2 * participationCost) - avg
Else:
  profit = numBidders * 0.2 * participationCost
```

### 4.3 Participation Cost Split

- 20% → Seller
- 80% → Platform (you)

Track participation when first bid is placed (or when joining — align with your scope).

---

## Phase 5: Package Structure (Clean Architecture)

```
onlineauction/
├── models/
│   ├── User.java           # base or interface
│   ├── Buyer.java
│   ├── Seller.java
│   ├── Auction.java
│   ├── Bid.java
│   ├── AuctionStatus.java  # OPEN, CLOSED
│   └── BidStatus.java      # ACTIVE, WITHDRAWN
├── repository/
│   ├── AuctionRepository.java
│   ├── BidRepository.java
│   ├── UserRepository.java
│   └── impl/               # InMemoryXxxRepository
├── services/
│   ├── AuctionService.java      # create, close auction
│   ├── BidService.java          # create, update, withdraw bid
│   ├── WinnerSelectionService.java  # or Strategy
│   └── ProfitCalculationService.java
├── strategies/             # Optional: WinnerSelectionStrategy
│   └── UniqueHighestBidStrategy.java
├── Main.java               # CLI demo
└── README.md
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Bid amount | `lowestBidLimit <= amount <= highestBidLimit` |
| Bid timing | Only when auction is OPEN |
| Update bid | Same buyer, same auction, auction OPEN |
| Withdraw bid | Same buyer, auction OPEN |
| Close auction | Only seller (or system) can close |
| Unique bid | Count frequency; only single occurrences qualify |
| Preferred buyer | Global count of distinct auctions participated in > 1 |

---

## Phase 7: Implementation Order (Recommended)

1. **Models** — `User`, `Buyer`, `Seller`, `Auction`, `Bid`, enums
2. **Repositories** — Interfaces + in-memory impl
3. **BidService** — create, update, withdraw with validations
4. **AuctionService** — create, close
5. **WinnerSelectionService** — unique highest + preferred-buyer
6. **ProfitCalculationService** — P&L formula
7. **Main / CLI** — wire everything, demo flows

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Clarity** | Name classes/methods clearly; avoid clever one-liners |
| **SOLID** | Single responsibility per class; depend on abstractions (Repository interfaces) |
| **Testability** | Services take repos via constructor; easy to mock |
| **Extensibility** | Strategy for winner selection; new rules without touching existing code |
| **Requirements traceability** | Map each requirement to a class/method in design |
| **Edge-case handling** | Explicit checks for no winner, invalid bids, closed auction |
| **Communication** | Explain trade-offs: "I used X because Y; alternatives are Z" |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| Buyers participate in multiple auctions | `BidService`, `BidRepository` |
| Sellers create multiple auctions, track P&L | `AuctionService`, `ProfitCalculationService` |
| Auction: id, limits, seller, participation cost | `Auction` model |
| Create/update/withdraw bids until closed | `BidService` + `AuctionStatus` check |
| Bid within limits | Validation in `BidService` |
| Highest unique bid wins | `WinnerSelectionService` / `UniqueHighestBidStrategy` |
| No unique bid → no winner | Handled in winner selection logic |
| P&L formula | `ProfitCalculationService` |
| Preferred buyers (tiebreaker) | `PreferredBuyerResolver` or inside winner strategy |
| 20% participation to seller, 80% to platform | Constant / config; used in P&L |

---

## Next Steps

1. Implement models first (30 min)
2. Repositories (20 min)
3. BidService + AuctionService (45 min)
4. Winner selection + P&L (30 min)
5. CLI + tests (30 min)

Total: ~2.5 hours for a solid, interview-ready LLD.
