# E-Commerce LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the E-Commerce system (product search, cart, orders) in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| In-memory or database? | In-memory (per requirements). |
| Search filters — single or composable? | Composable (AND/OR between criteria). |
| Price operators supported? | `=`, `!=`, `>`, `<`, `>=`, `<=`. |
| Order cancel — any status or only PLACED? | Only PLACED orders can be cancelled. |
| One cart per user or per session? | One cart per user (userId). |

**Why this matters**: Interviewers like seeing you nail requirements before diving into code.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
Product
├── productId, name, description, price
└── category (Category)

Category
├── categoryId
└── categoryName

Cart
├── cartId
├── userId
└── items (List<CartItem>)

CartItem
├── productId
└── quantity

Order
├── orderId
├── userId
├── status (PLACED, SHIPPED, DELIVERED, CANCELLED)
└── items (List<OrderItem>)

OrderItem (snapshot at order time)
├── productId
├── productName
├── price
└── quantity
```

**Relationships**:
- `Product` → `Category` (many-to-one)
- `Cart` 1 — * `CartItem`
- `Order` 1 — * `OrderItem`
- `Order` → `OrderStatus` enum

---

## Phase 3: Choose Design Patterns

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|------------------------|
| **Specification (Criteria)** | Product filters | Composable boolean expressions (`AndFilterCriteria`, `OrFilterCriteria`). |
| **Strategy** | Price comparisons | `PriceComparisonStrategy` per operator (>, <, >=, <=, =, !=). `PriceComparisonStrategyFactory` maps `Operator` → strategy. |
| **Repository** | Data access | `ProductRepository`, `CartRepository`, `OrderRepository` + in-memory impl. |
| **Service Layer** | Business logic | `CartService`, `OrderService` orchestrate flows. |

---

## Phase 4: Core Logic — Aligned with Code

### 4.1 Filter Flow (Criteria Pattern)

```
1. User passes Criteria (e.g. PriceFilterCriteria with Operator.GREATER_THAN, price=100)
2. Criteria.satisfy(List<Product> products) → filters products
3. AndFilterCriteria: apply all criteria (AND)
4. OrFilterCriteria: apply any criterion (OR)
5. PriceFilterCriteria delegates to PriceComparisonStrategy.compare(productPrice, filterPrice)
```

### 4.2 Price Filter

- `Operator` enum: `Equals`, `NotEquals`, `GreaterThan`, `GreaterThanOrEquals`, `LessThan`, `LessThanOrEquals`
- `PriceComparisonStrategyFactory.getPriceComparisonStrategy(Operator)` returns the right strategy
- Each strategy: `compare(double p1, double p2) → boolean`

### 4.3 Cart Flow

- Add product by productId; increase quantity if already in cart
- View cart returns items
- Place order: create Order from cart items (snapshot), clear cart, set status PLACED

### 4.4 Order Flow

- Cancel only when `status == PLACED`
- Order status: PLACED → SHIPPED → DELIVERED (or CANCELLED)

---

## Phase 5: Package Structure (Matches Code)

```
ecommerce/
├── models/
│   ├── Product.java
│   ├── Category.java
│   ├── Cart.java
│   ├── CartItem.java
│   ├── Order.java
│   ├── OrderItem.java
│   └── OrderStatus.java
├── repository/
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   ├── OrderRepository.java
│   ├── InMemoryProductRepository.java
│   ├── InMemoryCartRepository.java
│   └── InMemoryOrderRepository.java
├── catalog/
│   └── ProductCatalogFactory.java
├── services/
│   ├── CartService.java
│   ├── OrderService.java
│   └── filter/
│       ├── Criteria.java
│       ├── PriceFilterCriteria.java
│       ├── AndFilterCriteria.java
│       ├── OrFilterCriteria.java
│       ├── strategies/
│       │   ├── PriceComparisonStrategy.java
│       │   ├── GreaterThanStrategy.java, LessThanStrategy.java
│       │   ├── EqualsStrategy.java, NotEqualsStrategy.java
│       │   ├── GreaterThanOrEqualsStrategy.java
│       │   └── LessThanOrEqualsStrategy.java
│       └── factories/
│           └── PriceComparisonStrategyFactory.java
├── utils/
│   └── Operator.java
├── Main.java
└── README.md
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Add to cart | Product must exist |
| Place order | Cart must not be empty |
| Cancel order | Only PLACED status |
| Price filter | Operator must be valid; PriceFilterCriteria uses strategy |
| Search with no criteria | Return all products (or empty) |

---

## Phase 7: Implementation Order (Recommended)

1. **Models** — Product, Category, Cart, CartItem, Order, OrderItem, OrderStatus
2. **Repositories** — Interfaces + InMemory* implementations
3. **ProductCatalogFactory** — Seed sample products
4. **Criteria + Strategies** — PriceComparisonStrategy, PriceFilterCriteria, And/Or
5. **CartService** — add, view, clear
6. **OrderService** — place, cancel, status
7. **Main (CLI)** — Wire everything

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Clarity** | Criteria interface; clear method names |
| **SOLID** | Single responsibility (each strategy, each criteria); depend on abstractions |
| **Extensibility** | New operator = new Strategy class; new filter = new Criteria |
| **Composability** | AndFilterCriteria, OrFilterCriteria combine any Criteria |
| **Testability** | Services take repos via constructor; strategies are pure functions |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| Search products by filter | Criteria, PriceFilterCriteria, AndFilterCriteria, OrFilterCriteria |
| Price comparisons | PriceComparisonStrategy, PriceComparisonStrategyFactory, Operator |
| Add to cart | CartService, CartRepository |
| Place order | OrderService, OrderRepository, CartService |
| Cancel order | OrderService (status check) |
| In-memory storage | InMemoryProductRepository, InMemoryCartRepository, InMemoryOrderRepository |

---

## Run

```bash
# Run Main.java from IDE
# CLI: Search (list/filter), Cart (add/view), Order (place/cancel/status)
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.ecommerce.*"
```
