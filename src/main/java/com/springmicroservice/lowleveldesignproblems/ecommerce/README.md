# E-Commerce Low-Level Design (LLD)

> **Start here**: See [DESIGN_GUIDE.md](./DESIGN_GUIDE.md) for a step-by-step design approach and interview tips.

This project demonstrates the Low-Level Design (LLD) for an **E-Commerce System** with product search, cart, and order management. It uses the Specification and Strategy patterns for filtering, plus a clean Repository pattern for in-memory persistence.

## Design Requirements

1. System should allow users to search products from a catalog of products based on different filtration criteria.
2. Users should be able to add products to their carts.
3. Order placing and cancelling of orders at a later stage.
4. System should tell the current status of the order.

### Other Requirements

- In-memory DB needed.
- CLI interface.

---

## The Solution

The system combines multiple patterns:

1. **Specification (Criteria) Pattern** — Product filters implement `Criteria`. `AndFilterCriteria` and `OrFilterCriteria` enable composable boolean expressions.
2. **Strategy Pattern** — Price comparisons (>, <, >=, <=, =, !=) use `PriceComparisonStrategy` implementations via `PriceComparisonStrategyFactory`.
3. **Repository Pattern** — `ProductRepository`, `CartRepository`, and `OrderRepository` abstract storage. In-memory implementations satisfy the in-memory DB requirement.
4. **Service Layer** — `CartService` and `OrderService` orchestrate cart and order flows, delegating persistence to repositories.

### UML Class Diagram

```mermaid
classDiagram
    class Criteria {
        <<interface>>
        +satisfy(List~Product~ products) List~Product~
    }

    class PriceFilterCriteria {
        -double price
        -PriceComparisonStrategy strategy
        +satisfy(List~Product~ products) List~Product~
    }

    class AndFilterCriteria {
        -List~Criteria~ criteriaList
        +satisfy(List~Product~ products) List~Product~
    }

    class OrFilterCriteria {
        -List~Criteria~ criteriaList
        +satisfy(List~Product~ products) List~Product~
    }

    class PriceComparisonStrategy {
        <<interface>>
        +compare(double p1, double p2) boolean
    }

    class PriceComparisonStrategyFactory {
        <<utility>>
        +getPriceComparisonStrategy(Operator) PriceComparisonStrategy
    }

    class Operator {
        <<enumeration>>
        Equals
        NotEquals
        GreaterThan
        GreaterThanOrEquals
        LessThan
        LessThanOrEquals
    }

    class Product {
        -String productId
        -String name
        -String description
        -double price
        -Category category
    }

    class Cart {
        -String cartId
        -String userId
        -List~CartItem~ items
    }

    class CartItem {
        -String productId
        -int quantity
    }

    class Order {
        -String orderId
        -String userId
        -OrderStatus status
        -List~OrderItem~ items
    }

    class OrderItem {
        -String productId
        -String productName
        -double price
        -int quantity
    }

    class OrderStatus {
        <<enumeration>>
        PLACED
        SHIPPED
        DELIVERED
        CANCELLED
    }

    class Category {
        -String categoryId
        -String categoryName
    }

    Criteria <|.. PriceFilterCriteria : implements
    Criteria <|.. AndFilterCriteria : implements
    Criteria <|.. OrFilterCriteria : implements
    AndFilterCriteria --> Criteria : composes
    OrFilterCriteria --> Criteria : composes
    PriceFilterCriteria --> PriceComparisonStrategy : delegates
    PriceComparisonStrategyFactory ..> Operator : uses
    PriceComparisonStrategyFactory ..> PriceComparisonStrategy : creates
    PriceComparisonStrategy <|.. GreaterThanStrategy : implements
    PriceComparisonStrategy <|.. LessThanStrategy : implements
    PriceComparisonStrategy <|.. EqualsStrategy : implements
    PriceComparisonStrategy <|.. NotEqualsStrategy : implements
    PriceComparisonStrategy <|.. GreaterThanOrEqualsStrategy : implements
    PriceComparisonStrategy <|.. LessThanOrEqualsStrategy : implements

    class GreaterThanStrategy {
        +compare(double, double) boolean
    }

    class LessThanStrategy {
        +compare(double, double) boolean
    }

    class EqualsStrategy {
        +compare(double, double) boolean
    }

    class NotEqualsStrategy {
        +compare(double, double) boolean
    }

    class GreaterThanOrEqualsStrategy {
        +compare(double, double) boolean
    }

    class LessThanOrEqualsStrategy {
        +compare(double, double) boolean
    }

    class Main {
        <<CLI>>
        +main(String[]) void
    }

    Main ..> Criteria : uses
    Main ..> Product : displays
    Main --> CartService : uses
    Main --> OrderService : uses
    CartService --> CartRepository : uses
    CartService --> ProductRepository : uses
    OrderService --> OrderRepository : uses
    OrderService --> CartService : uses
    Product --> Category : has
    Cart *-- CartItem : contains
    Order *-- OrderItem : contains
    Order --> OrderStatus : has
```

### Component Structure

```
ecommerce/
├── models/
│   ├── Product.java              # Product entity (with productId)
│   ├── Category.java             # Category entity
│   ├── Cart.java                 # User cart
│   ├── CartItem.java             # Product + quantity in cart
│   ├── Order.java                # Placed order
│   ├── OrderItem.java            # Product snapshot at order time
│   └── OrderStatus.java          # PLACED, SHIPPED, DELIVERED, CANCELLED
├── repository/                   # In-memory persistence
│   ├── ProductRepository.java
│   ├── CartRepository.java
│   ├── OrderRepository.java
│   ├── InMemoryProductRepository.java
│   ├── InMemoryCartRepository.java
│   └── InMemoryOrderRepository.java
├── catalog/
│   └── ProductCatalogFactory.java   # Seeds product catalog
├── services/
│   ├── CartService.java          # Add to cart, view cart, clear
│   ├── OrderService.java         # Place order, cancel, status
│   └── filter/
│   ├── Criteria.java             # Specification interface
│   ├── PriceFilterCriteria.java  # Price-based filter
│   ├── AndFilterCriteria.java    # AND combiner
│   ├── OrFilterCriteria.java     # OR combiner
│   ├── strategies/               # Price comparison strategies
│   │   ├── PriceComparisonStrategy.java
│   │   ├── GreaterThanStrategy.java
│   │   ├── LessThanStrategy.java
│   │   ├── EqualsStrategy.java
│   │   ├── NotEqualsStrategy.java
│   │   ├── GreaterThanOrEqualsStrategy.java
│   │   └── LessThanOrEqualsStrategy.java
│       └── factories/
│           └── PriceComparisonStrategyFactory.java
├── utils/
│   └── Operator.java             # Comparison operators enum
├── Main.java                     # CLI entry point (search, cart, order, cancel)
└── README.md
```

### Run

Run `Main.java` from your IDE (right-click → Run). The CLI supports:
- **Search**: List products, filter by price
- **Cart**: Add products (by product ID, e.g. `prod-1`), view cart
- **Order**: Place order (clears cart), view orders, cancel order (PLACED only)

### Tests

```bash
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.ecommerce.*"
```
