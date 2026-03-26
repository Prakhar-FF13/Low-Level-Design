# 🚀 The Ultimate Low-Level Design Playground!

Welcome to my LLD repo! If you're tired of boring, textbook explanations of design patterns and want to see how to actually build cool stuff from scratch, you're in the right place. 

I built this repository to practice, revise, and completely master Object-Oriented Design (OOD). Each folder here is a deep dive into a classic system design interview problem. No fluffy theory just hard-hitting Java code, strict design patterns, and solid architecture!

---

## 📘 [LLD Master Guide](LLD_MASTER_GUIDE.md) — How to Think & Excel at LLD

**Stuck on clarification, inputs/outputs, or creating design guidelines?** The [LLD Master Guide](LLD_MASTER_GUIDE.md) is a comprehensive meta-guide that teaches you:
- **What questions to ask** in interviews (and to yourself) for any LLD problem
- **How to decide inputs/outputs** using a systematic framework
- **How to create detailed design guidelines** from vague requirements
- **Domain-specific question banks** for games, booking, payment, cache, queue, etc.
- **Pattern recognition** — when to use Strategy, State, Observer, etc.
- **Interview scripts**, time management, checklists, and practice strategies

Use it as your reference to build the insight needed to excel at LLD interviews.

## ▶️ Running the Applications

Several packages include a runnable main class. Use these Gradle tasks:

| Application | Command |
|-------------|---------|
| **Tic Tac Toe** | `./gradlew runTictactoe` |
| **Parking Lot** | `./gradlew runParkinglot` |
| **E-Commerce** | `./gradlew runEcommerce` |
| **Online Auction** | `./gradlew runOnlineauction` |
| **Movie CMS** | `./gradlew runMoviecms` |
| **Payment Gateway** | `./gradlew runPaymentgateway` |
| **Message Queue** | `./gradlew runMessagequeue` |
| **Battleship** | `./gradlew runBattleship` |
| **Chess** | `./gradlew runChess` |
| **Stock Broker** | `./gradlew runStockbroker` |
| **Stock Exchange** / **Splitwise** (Spring REST) | `./gradlew bootRun` |

### Gradle Task Parameters Explained

Each run task is a `JavaExec` task with these parameters:

| Parameter | Purpose |
|-----------|---------|
| **`group`** | Categorizes the task in `./gradlew tasks`. All LLD apps are grouped under `lld-applications` for easy discovery. |
| **`description`** | Human-readable description shown when listing tasks (e.g., `./gradlew tasks --group=lld-applications`). |
| **`classpath`** | Set to `sourceSets.main.runtimeClasspath` — includes your compiled classes plus all dependencies (Spring, H2, Lombok, etc.) so the JVM can load everything at runtime. |
| **`mainClass`** | The fully qualified class name containing `public static void main(String[] args)` — the entry point Gradle invokes. |
| **`standardInput`** | Set to `System.in` — connects the terminal to the process's stdin so interactive CLI apps (Scanner, BufferedReader) receive your keyboard input. Without this, apps that read from stdin would get an empty stream. |

To list all available run tasks:
```bash
./gradlew tasks --group=lld-applications
```

---

Here is what we are building and learning:

## 🎮 The Problems

### 1. [Rule Engine 🧮](src/main/java/com/springmicroservice/lowleveldesignproblems/ruleengine)
Ever wonder how corporate expense systems dynamically flag bad transactions without having a billion `if/else` statements? This teaches you how to build a dynamic, plug-and-play validation engine.
*   **What it teaches:** How to stop hardcoding business logic and make systems scalable.
*   **Design Patterns:** Extensible Rule Pattern (Strategy/Composite vibes).
*   **SOLID Principles:** **Open/Closed Principle** (add new rules without touching the core engine) and **Single Responsibility** (each rule does one thing!).

### 2. [ATM Machine 🏧](src/main/java/com/springmicroservice/lowleveldesignproblems/atmmachine)
ATMs are super tricky because giving out cash when there's no card inserted is a disaster. This problem teaches you how to lock down a system's behavior based on exactly what "state" it is currently in.
*   **What it teaches:** How to perfectly model state machines in code so your hardware/software doesn't glitch out.
*   **Design Patterns:** **State Design Pattern** (Absolute lifesaver for this!).
*   **SOLID Principles:** **Open/Closed Principle** (add new states like 'ReadPIN' easily) and strong **Encapsulation**.

### 3. [Route Handler / Web Middleware 🌐](src/main/java/com/springmicroservice/lowleveldesignproblems/routehandler)
How do web servers like Express or Spring Boot pass your HTTP request through a gauntlet of authenticators, loggers, and parsers before it hits the controller? We build that gauntlet from scratch here.
*   **What it teaches:** How to chain a bunch of independent operations together sequentially.
*   **Design Patterns:** **Chain of Responsibility Pattern**.
*   **SOLID Principles:** **Single Responsibility** (each middleware ONLY cares about its one specific job).

### 4. [Cache System ⚡](src/main/java/com/springmicroservice/lowleveldesignproblems/cachesystem)
Writing a dictionary to memory is easy. Writing an ultra-fast, thread-safe Cache that intuitively knows what data to delete when it gets full? That's engineering.
*   **What it teaches:** How to build pluggable architectures and manage memory eviction.
*   **Design Patterns:** **Strategy Pattern** (for swapping eviction policies like LRU vs LFU) and **Factory Pattern** (for generating the cache).
*   **SOLID Principles:** Heavy emphasis on **Dependency Inversion** (depending on abstract policies, not concrete algorithms).

### 5. [Cricbuzz / Live Score Broadcaster 🏏](src/main/java/com/springmicroservice/lowleveldesignproblems/cricbuzz)
Building a system that violently blasts live cricket scores to 50 million phones the millisecond a wicket falls.
*   **What it teaches:** How to build insanely decoupled systems where the backend has zero clue who it's sending data to—it just yells it into the void, and the right apps catch it.
*   **Design Patterns:** **Observer Pattern** (Publisher/Subscriber) equipped with custom **Predicate Filtering** so users only get the exact updates they ask for.
*   **SOLID Principles:** **Dependency Inversion Principle** (The producer only depends on a simple Consumer Interface, allowing infinite types of apps to connect!).

### 6. [Parking Lot System 🚗](src/main/java/com/springmicroservice/lowleveldesignproblems/parkinglot)
The classic LLD problem on steroids. Instead of generic `if (type == CAR)` matching, this builds a mathematical subset-matching engine using vehicle "capabilities", alongside real-time event publishing.
*   **What it teaches:** Domain modeling, Capability-based configuration, and decoupling cross-cutting concerns (like payments and analytics).
*   **Design Patterns:** **Strategy Pattern** (Best-Fit Slot Matching & Modular Payments), **Observer Pattern** (Live Display Boards), and **Facade Pattern**.
*   **SOLID Principles:** High emphasis on **Single Responsibility** (splitting ticket management, payments, and events into micro-services) and the **Open/Closed Principle**.

### 7. [BookMyShow 🎬](src/main/java/com/springmicroservice/lowleveldesignproblems/bookmyshow)
A movie ticket booking system with seat selection, concurrent booking handling, and REST API. Built with DDD and Hexagonal Architecture.
*   **What it teaches:** Domain-Driven Design, Ports & Adapters, pessimistic locking for concurrency, and layered testing (unit → controller → integration).
*   **Design Patterns:** **Hexagonal Architecture** (Ports & Adapters), **Repository Pattern**, **Domain Services**.
*   **SOLID Principles:** **Dependency Inversion** (domain depends on ports, not adapters), **Single Responsibility** (domain vs infrastructure vs API).

### 8. [E-Commerce 🛒](src/main/java/com/springmicroservice/lowleveldesignproblems/ecommerce)
Product search with filters, shopping cart, order placement, and cancellation. Full CLI demo with in-memory persistence.
*   **What it teaches:** Specification (Criteria) pattern for composable filters, Strategy pattern for price comparisons, Repository pattern for storage, and service-layer orchestration.
*   **Design Patterns:** **Specification Pattern** (Criteria, AndFilterCriteria, OrFilterCriteria), **Strategy Pattern** (price comparison operators), **Repository Pattern** (in-memory implementations), **Factory Pattern** (PriceComparisonStrategyFactory).
*   **SOLID Principles:** **Open/Closed** (add new filter criteria without modifying existing code), **Dependency Inversion** (services depend on repository interfaces).

### 9. [Tic Tac Toe ⭕](src/main/java/com/springmicroservice/lowleveldesignproblems/tictactoe)
Classic N x N Tic Tac Toe with extensible winning rules, undo support, and decoupled I/O for testing and future GUI.
*   **What it teaches:** State machine modeling for game flow, pluggable winning strategies, template method for game loop, and factory-based object creation.
*   **Design Patterns:** **State Pattern** (InProgress, Win, Draw), **Strategy Pattern** (Row, Column, Diagonal winning strategies), **Factory Pattern** (WinningStrategyFactory, GameFactory, PlayerFactory), **Template Method** (Game loop with I/O hooks).
*   **SOLID Principles:** **Single Responsibility** (states, strategies, I/O separated), **Open/Closed** (add strategies without touching core), **Dependency Inversion** (I/O abstractions, injected dependencies).

### 10. [Online Auction 🔨](src/main/java/com/springmicroservice/lowleveldesignproblems/onlineauction)
Sellers create auctions, buyers place bids, and winners are selected using a configurable strategy. Highest unique bid wins; preferred buyers break ties.
*   **What it teaches:** Auction lifecycle modeling, winner selection algorithms, participation cost tracking, and seller P&L calculation.
*   **Design Patterns:** **Strategy Pattern** (configurable winning strategy), **Repository Pattern** (in-memory implementations), **Service Layer** (AuctionService, BidsService, ProfitCalculationService).
*   **SOLID Principles:** **Dependency Inversion** (WinningStrategy interface), **Single Responsibility** (separate services for auctions, bids, profit).

### 11. [Movie CMS 🎞️](src/main/java/com/springmicroservice/lowleveldesignproblems/moviecms)
A content management system for movies with user/movie registration, pluggable filter-based search, and multi-level caching (L1 per-user, L2 global).
*   **What it teaches:** Multi-level cache hierarchy (L1 → L2 → primary), LRU eviction, cache key design for search, and composable filters with AND/OR logic.
*   **Design Patterns:** **Strategy/Filter Pattern** (pluggable search criteria), **Composite Pattern** (CompositeFilter for multi-filter search), **Chain of Responsibility** (cache lookup: L1 → L2 → loader), **Repository Pattern**, **Facade** (MovieCMSOrchestrator).
*   **SOLID Principles:** **Open/Closed** (new filters without touching search logic), **Single Responsibility** (services, cache, filters separated), **Dependency Inversion** (depend on Filter interface, repository interfaces).

### 12. [Payment Gateway 💳](src/main/java/com/springmicroservice/lowleveldesignproblems/paymentgateway)
A Paytm-style payment gateway that onboard clients, captures payments via UPI/Cards/NetBanking, and routes transactions to different banks with configurable strategies.
*   **What it teaches:** Polymorphic payment details, pluggable routing (mode-based vs weighted distribution), facade orchestration, and traffic logging for audit.
*   **Design Patterns:** **Strategy Pattern** (PaymentModeRoutingStrategy, WeightedRoutingStrategy), **Facade Pattern** (PaymentGatewayOrchestrator), **Polymorphism** (PaymentDetails: UPI, Card, NetBanking), **Repository Pattern**.
*   **SOLID Principles:** **Open/Closed** (new payment method = new Details class; new routing = new Strategy), **Single Responsibility** (orchestrator, services, routing separated), **Dependency Inversion** (depend on PaymentRoutingStrategy interface).

### 13. [In-Memory Message Queue 📬](src/main/java/com/springmicroservice/lowleveldesignproblems/messagequeue)
A lightweight Kafka/Redis Pub-Sub style messaging queue where publishers push JSON-like messages and subscribers receive them asynchronously via callbacks, with batch consumption and retry with exponential backoff.
*   **What it teaches:** Custom queue implementation (no java.util.Queue), pub-sub decoupling, fan-out delivery, retry with backoff, and thread safety.
*   **Design Patterns:** **Observer / Pub-Sub** (Subscriber callbacks), **Strategy Pattern** (BackoffStrategy for retry), **Facade** (Publisher over QueueManager), **Executor/Scheduler** (configurable poll delay).
*   **SOLID Principles:** **Single Responsibility** (Queue = storage; Dispatcher = delivery; RetryPolicy = retry), **Open/Closed** (new BackoffStrategy without changing RetryPolicy), **Dependency Inversion** (depend on MessageHandler, MessageQueue interfaces).

### 14. [Battleship 🚢](src/main/java/com/springmicroservice/lowleveldesignproblems/battleship)
Classic battleship game where two players take turns firing missiles at each other's fleet on a shared battlefield. Features square ships, spatial validation, turn management, fog-of-war views, and configurable strategies.
*   **What it teaches:** Domain modeling for games (Coordinate, Cell, Ship, Battlefield), spatial validation (bounds, overlap), turn management, view abstraction with fog of war, and strategy-based extensibility.
*   **Design Patterns:** **Strategy Pattern** (TargetingStrategy, GridDivisionStrategy, ShipPlacementStrategy), **Builder** (GameBuilder), **Value Object** (Coordinate, FireResult), **Repository Pattern** (GameRepository, PlayerRepository).
*   **SOLID Principles:** **Single Responsibility** (Game = orchestration; Battlefield = grid; TurnManager = turns), **Open/Closed** (new targeting/placement strategies without touching core), **Dependency Inversion** (depend on strategy interfaces, repository interfaces).

### 15. [Stock Broker 📈](src/main/java/com/springmicroservice/lowleveldesignproblems/stockbroker)
A stock broker system where multiple exchanges (BSE, NSE) push price updates, and the broker displays the latest prices and stores historical data.
*   **What it teaches:** Observer (Pub-Sub) pattern for decoupled producers and consumers, thread-safe data structures (CopyOnWriteArrayList, ConcurrentHashMap), exchange identity in updates, bounded historical storage.
*   **Design Patterns:** **Observer / Pub-Sub** (ExchangePublisher, Subscriber), **Strategy** (extensible exchanges by implementing the interface).
*   **SOLID Principles:** **Dependency Inversion** (exchanges and subscribers depend on interfaces), **Open/Closed** (add new exchanges without modifying subscribers).

### 16. [Text Editor 📝](src/main/java/com/springmicroservice/lowleveldesignproblems/texteditor)
In-memory editor with add, backspace-style delete, and undo — service layer only (no UI, no REST, no DB).
*   **What it teaches:** Modeling a document + cursor, **Command** with **execute** and **undo**, LIFO history, and a small **Facade** as the public API.
*   **Design Patterns:** **Command Pattern** (`InsertCommand`, `DeleteCommand`), **Facade** (`TextEditorService`).
*   **SOLID Principles:** **Single Responsibility** (buffer vs commands vs history vs service), clear boundaries for testing.

See [texteditor/README.md](src/main/java/com/springmicroservice/lowleveldesignproblems/texteditor/README.md) and [DESIGN_GUIDELINE.md](src/main/java/com/springmicroservice/lowleveldesignproblems/texteditor/DESIGN_GUIDELINE.md).

### 17. [Chess ♟️](src/main/java/com/springmicroservice/lowleveldesignproblems/chess)
Console chess on an 8×8 board with standard piece movement, alternating turns (White first), and moves as algebraic squares via a **`Move`** value object over **`Square`** coordinates.
*   **What it teaches:** Board and piece modeling, legal-move validation hooks, turn flow, and modular packages (`models`, `game`, `io`) ready for richer rules and alternate UIs.
*   **Design Patterns:** **Value Object** (`Move`, `Square`), domain-centric game orchestration.
*   **SOLID Principles:** **Single Responsibility** (board vs game vs I/O), **Open/Closed** (extend rules without rewriting the core loop).

See [chess/README.md](src/main/java/com/springmicroservice/lowleveldesignproblems/chess/README.md) and [DESIGN_GUIDE.md](src/main/java/com/springmicroservice/lowleveldesignproblems/chess/DESIGN_GUIDE.md).

### 18. [Stock Exchange 📊](src/main/java/com/springmicroservice/lowleveldesignproblems/stockexchange)
In-memory stock exchange with Spring REST: place, modify, and cancel orders; FIFO matching at equal price; per-symbol order books; trades recorded with concurrency controls.
*   **What it teaches:** Order-book modeling, **FIFO** matching strategy, async matching with injectable executors, per-symbol locking, and REST + validation as the adapter layer.
*   **Design Patterns:** **Strategy Pattern** (`OrderMatchingStrategy`, `FifoOrderMatchingStrategy`), **Repository Pattern** (trades, order book abstraction), **Dependency Injection** (Spring config).
*   **SOLID Principles:** **Dependency Inversion** (matching and trade services behind interfaces), **Single Responsibility** (exchange vs matching vs REST).

See [stockexchange/README.md](src/main/java/com/springmicroservice/lowleveldesignproblems/stockexchange/README.md) and [DESIGN_GUIDELINE.md](src/main/java/com/springmicroservice/lowleveldesignproblems/stockexchange/DESIGN_GUIDELINE.md). Run the whole Spring Boot app with `./gradlew bootRun`; APIs are documented in the package README.

### 19. [Banking System 🏦](src/main/java/com/springmicroservice/lowleveldesignproblems/bankingsystem)
In-memory banking with timestamped operations: accounts, transfers, top spenders, payments with **24h delayed cashback** (ordered before other events at the same millisecond), **account merge** with payment reassignment, and **historical balance** via ledger replay.
*   **What it teaches:** Per-account ledgers with explicit **outgoing** on transfers/pays, **ports & adapters** for accounts and payments, **event sink** for audit, and **deterministic replay** for `getBalance` after merges.
*   **Design Patterns:** **Repository Pattern**, **Dependency Inversion** (`EventPublisher`, repositories), **Facade-like** orchestration (`AccountsService` + `PaymentService`).
*   **SOLID Principles:** **Dependency Inversion** (services depend on repository/event interfaces), **Single Responsibility** (domain vs payment/cashback vs merge), **Open/Closed** (swap in-memory stores for persistence).

See [bankingsystem/README.md](src/main/java/com/springmicroservice/lowleveldesignproblems/bankingsystem/README.md) and [DESIGN_GUIDE.md](src/main/java/com/springmicroservice/lowleveldesignproblems/bankingsystem/DESIGN_GUIDE.md). Run tests with `./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.bankingsystem.**"` (no runnable `main` in this package yet).

### 20. [Splitwise 💸](src/main/java/com/springmicroservice/lowleveldesignproblems/splitwise)
Expense sharing in **groups**: multiple payers per bill, **equal** or **percentage** splits, **net balances** per member, and **greedy settlement suggestions** (minimal cash transfers). **JPA + H2** persistence; intentionally **compact** (one `SplitwiseService`, one `SplitwiseController`, DTOs in one file) for interview-sized scope.
*   **What it teaches:** Modeling payers vs owed shares, **BigDecimal** money rules, deriving balances from facts, greedy settlement, REST + validation.
*   **Design Patterns:** **Repository Pattern** (Spring Data JPA), **Facade** (`SplitwiseService`), **DTO records** + centralized exception handling.
*   **SOLID Principles:** **Single Responsibility** (service = rules, controller = HTTP), thin persistence layer.

See [splitwise/README.md](src/main/java/com/springmicroservice/lowleveldesignproblems/splitwise/README.md) and [DESIGN_GUIDELINE.md](src/main/java/com/springmicroservice/lowleveldesignproblems/splitwise/DESIGN_GUIDELINE.md). Run the app with `./gradlew bootRun`; APIs are under `/api/splitwise/...`.