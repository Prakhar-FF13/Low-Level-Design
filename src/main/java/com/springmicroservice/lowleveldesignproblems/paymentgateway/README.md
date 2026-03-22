# Payment Gateway (Paytm-style) — Complete Tutorial

> **Start here**: See [DESIGN_GUIDE.md](./DESIGN_GUIDE.md) for a step-by-step design approach and interview tips for strong hire.

A Low-Level Design problem demonstrating **client onboarding**, **polymorphic payment methods** (UPI, Cards, NetBanking), **pluggable routing strategies** (mode-based and weighted distribution), and **traffic logging**. This README is a self-contained tutorial—no need to read the code.

---

## Table of Contents

1. [Functional Requirements](#functional-requirements)
2. [Architecture Overview](#architecture-overview)
3. [Component Diagrams (UML)](#component-diagrams-uml)
4. [Payment Capture Flow — Sequence Diagram](#payment-capture-flow--sequence-diagram)
5. [Why Polymorphic PaymentDetails — Deep Dive](#why-polymorphic-paymentdetails--deep-dive)
6. [Payment Routing Strategies — Deep Dive](#payment-routing-strategies--deep-dive)
7. [Bank Processing & Mock Failure](#bank-processing--mock-failure)
8. [How Components Work Together](#how-components-work-together)
9. [Design Patterns Used](#design-patterns-used)
10. [Running the Application](#running-the-application)
11. [Quick Reference](#quick-reference)

---

## Functional Requirements

| # | Requirement | Solution |
|---|-------------|----------|
| 1 | Client onboarding | `PaymentGatewayOrchestrator.onboardClient()` → `ClientService` |
| 2 | Client removal | `PaymentGatewayOrchestrator.removeClient()` |
| 3 | Client supports subset of payment methods | `Clients.paymentMethods` |
| 4 | Payment processed through bank | `Banks.processPayment()` |
| 5 | Random success/failure | `Banks.failureRate` + `Math.random()` |
| 6 | UPI, CREDIT_CARD, DEBIT_CARD, NET_BANKING | `PaymentMethods` enum + `PaymentDetails` implementations |
| 7 | Method-specific input (vpa, card, netbanking) | Polymorphic `PaymentDetails` (UPI, Card, NetBanking) |
| 8 | Routing by payment mode (e.g., all CC → HDFC) | `PaymentModeRoutingStrategy` |
| 9 | Routing by distribution (30% / 70%) | `WeightedRoutingStrategy` |
| 10 | Traffic logs | `TrafficLogger` |
| 11 | Error codes | `ErrorCode` enum in `PaymentResult` |
| 12 | Bank-level mock failure config | `Banks.failureRate` |

---

## Architecture Overview

The system follows a **facade + layered architecture**:

```
┌───────────────────────────────────────────────────────────────────┐
│                          Main (Entry Point)                        │
└───────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌───────────────────────────────────────────────────────────────────┐
│              PaymentGatewayOrchestrator (Facade)                   │
│  Coordinates: onboarding, capture, routing, traffic logging        │
└───────────────────────────────────────────────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        ▼                           ▼                           ▼
┌───────────────┐         ┌─────────────────┐         ┌─────────────────┐
│ ClientService │         │   BankService   │         │ TrafficLogger   │
│ (onboard,     │         │ (create bank,   │         │ (audit trail)   │
│  remove)     │         │  getAllBanks)   │         │                 │
└───────┬───────┘         └────────┬────────┘         └─────────────────┘
        │                          │
        ▼                          ▼
┌───────────────┐         ┌─────────────────┐
│ClientsRepository│       │ BankRepository  │
│ (In-Memory)   │         │ (In-Memory)    │
└───────────────┘         └─────────────────┘
                                    │
                                    ▼
                    ┌───────────────────────────────┐
                    │ PaymentRoutingStrategy        │
                    │ (injectable)                  │
                    │ • PaymentModeRoutingStrategy  │
                    │ • WeightedRoutingStrategy     │
                    └───────────────────────────────┘
                                    │
                                    ▼
                    ┌───────────────────────────────┐
                    │ Banks.processPayment()        │
                    │ (random success/failure)       │
                    └───────────────────────────────┘
```

**Key idea**: The orchestrator is the single entry point. It validates the client, validates payment details polymorphically, delegates bank selection to the routing strategy, processes through the bank, and logs traffic. Services and repositories are decoupled; the routing strategy is pluggable.

---

## Component Diagrams (UML)

### Package Structure

```
paymentgateway/
├── models/
│   ├── Clients.java
│   ├── Banks.java
│   ├── PaymentMethods.java (enum)
│   ├── PaymentRequest.java
│   ├── PaymentResult.java
│   ├── ErrorCode.java (enum)
│   └── details/
│       ├── PaymentDetails.java (interface)
│       ├── UPIPaymentDetails.java
│       ├── CardPaymentDetails.java
│       └── NetBankingPaymentDetails.java
├── strategies/
│   ├── PaymentRoutingStrategy.java (interface)
│   ├── PaymentModeRoutingStrategy.java
│   └── WeightedRoutingStrategy.java
├── repository/
│   ├── ClientsRepository.java
│   ├── BankRepository.java
│   └── impl/
│       ├── InMemoryClientsRepository.java
│       └── InMemoryBankRepository.java
├── services/
│   ├── ClientService.java
│   ├── BankService.java
│   └── TrafficLogger.java
├── exceptions/
│   ├── UnsupportedPaymentMethodException.java
│   └── InvalidPaymentDetailsException.java
├── PaymentGatewayOrchestrator.java
└── Main.java
```

### UML Class Diagram

```mermaid
classDiagram
    %% --- Domain Models ---
    class Clients {
        -String clientId
        -String clientName
        -List~PaymentMethods~ paymentMethods
    }

    class Banks {
        -String bankId
        -String bankName
        -double failureRate
        -List~PaymentMethods~ supportedPaymentMethods
        +processPayment(PaymentRequest) PaymentResult
    }

    class PaymentMethods {
        <<enumeration>>
        UPI
        CREDIT_CARD
        DEBIT_CARD
        NET_BANKING
    }

    class PaymentRequest {
        -String clientId
        -double amount
        -PaymentMethods paymentMethod
        -PaymentDetails paymentDetails
    }

    class PaymentResult {
        -boolean success
        -String transactionId
        -ErrorCode errorCode
        -String bankId
        +success(String, String)$ PaymentResult
        +failure(ErrorCode, String)$ PaymentResult
    }

    class ErrorCode {
        <<enumeration>>
        PAYMENT_FAILED
        BANK_UNAVAILABLE
        INVALID_PAYMENT_DETAILS
        INSUFFICIENT_FUNDS
        TRANSACTION_TIMED_OUT
    }

    class PaymentDetails {
        <<interface>>
        +getPaymentMethod() PaymentMethods
        +validate() void
    }

    class UPIPaymentDetails {
        -String vpaId
        +getPaymentMethod() PaymentMethods
        +validate() void
    }

    class CardPaymentDetails {
        -String cardNumber
        -LocalDateTime expiryDate
        -String cvv
        -PaymentMethods cardType
        +getPaymentMethod() PaymentMethods
        +validate() void
    }

    class NetBankingPaymentDetails {
        -String username
        -String password
        +getPaymentMethod() PaymentMethods
        +validate() void
    }

    %% --- Routing ---
    class PaymentRoutingStrategy {
        <<interface>>
        +selectBank(PaymentRequest, List~Banks~) Banks
    }

    class PaymentModeRoutingStrategy {
        -Map paymentMethodToBankId
        +selectBank(PaymentRequest, List~Banks~) Banks
    }

    class WeightedRoutingStrategy {
        -Map bankWeights
        +selectBank(PaymentRequest, List~Banks~) Banks
    }

    %% --- Repositories ---
    class ClientsRepository {
        <<interface>>
        +save(Clients) Clients
        +findById(String) Optional
        +remove(String) void
    }

    class BankRepository {
        <<interface>>
        +save(Banks) Banks
        +findById(String) Optional
        +findAll() List
    }

    %% --- Services ---
    class ClientService {
        -ClientsRepository repository
        +createClient(String, List) Clients
        +getClientById(String) Clients
        +removeClient(String) void
    }

    class BankService {
        -BankRepository repository
        +createBank(String, double, List) Banks
        +getAllBanks() List
        +getBankById(String) Banks
    }

    class TrafficLogger {
        -List logs
        +log(String, Banks, PaymentResult) void
        +getLogs() List
    }

    %% --- Orchestrator ---
    class PaymentGatewayOrchestrator {
        -ClientService clientService
        -BankService bankService
        -PaymentRoutingStrategy routingStrategy
        -TrafficLogger trafficLogger
        +onboardClient(String, List) String
        +removeClient(String) void
        +capturePayment(String, double, PaymentMethods, PaymentDetails) PaymentResult
        +getTrafficLogger() TrafficLogger
    }

    %% --- Relationships ---
    PaymentDetails <|.. UPIPaymentDetails
    PaymentDetails <|.. CardPaymentDetails
    PaymentDetails <|.. NetBankingPaymentDetails
    PaymentRequest --> PaymentDetails : contains
    PaymentRequest --> PaymentMethods : uses

    PaymentRoutingStrategy <|.. PaymentModeRoutingStrategy
    PaymentRoutingStrategy <|.. WeightedRoutingStrategy

    ClientService --> ClientsRepository : uses
    BankService --> BankRepository : uses

    PaymentGatewayOrchestrator --> ClientService : uses
    PaymentGatewayOrchestrator --> BankService : uses
    PaymentGatewayOrchestrator --> PaymentRoutingStrategy : uses
    PaymentGatewayOrchestrator --> TrafficLogger : uses
    PaymentGatewayOrchestrator --> Banks : processes via
    Banks --> PaymentRequest : receives
    Banks --> PaymentResult : returns
```

---

## Payment Capture Flow — Sequence Diagram

### Scenario 1: Successful UPI Payment (Cache Not Applicable — Payment Flow)

When a client captures a UPI payment, the flow validates client, validates UPI details, selects bank via routing, processes, and logs.

```mermaid
sequenceDiagram
    actor Client
    participant Main
    participant PaymentGatewayOrchestrator
    participant ClientService
    participant ClientsRepository
    participant BankService
    participant PaymentRoutingStrategy
    participant Banks
    participant TrafficLogger

    Client->>Main: run demo
    Main->>PaymentGatewayOrchestrator: capturePayment(clientId, 100, UPI, upiDetails)

    Note over PaymentGatewayOrchestrator: 1. Validate paymentDetails not null
    Note over PaymentGatewayOrchestrator: 2. Validate method matches details

    PaymentGatewayOrchestrator->>ClientService: getClientById(clientId)
    ClientService->>ClientsRepository: findById(clientId)
    ClientsRepository-->>ClientService: Clients
    ClientService-->>PaymentGatewayOrchestrator: Clients

    Note over PaymentGatewayOrchestrator: 3. Check client supports UPI

    PaymentGatewayOrchestrator->>PaymentGatewayOrchestrator: paymentDetails.validate()
    Note over PaymentGatewayOrchestrator: 4. Polymorphic validation (UPI format)

    PaymentGatewayOrchestrator->>PaymentGatewayOrchestrator: build PaymentRequest

    PaymentGatewayOrchestrator->>BankService: getAllBanks()
    BankService-->>PaymentGatewayOrchestrator: List~Banks~

    PaymentGatewayOrchestrator->>PaymentRoutingStrategy: selectBank(request, banks)
    PaymentRoutingStrategy-->>PaymentGatewayOrchestrator: Banks (e.g., ICICI)

    PaymentGatewayOrchestrator->>Banks: processPayment(request)
    Note over Banks: Math.random() < failureRate?
    Banks-->>PaymentGatewayOrchestrator: PaymentResult (success/failure)

    PaymentGatewayOrchestrator->>TrafficLogger: log(clientId, bank, result)
    PaymentGatewayOrchestrator-->>Main: PaymentResult
```

### Scenario 2: Client Onboarding

```mermaid
sequenceDiagram
    participant Main
    participant PaymentGatewayOrchestrator
    participant ClientService
    participant ClientsRepository

    Main->>PaymentGatewayOrchestrator: onboardClient("Merchant1", [UPI, CREDIT_CARD])
    PaymentGatewayOrchestrator->>PaymentGatewayOrchestrator: validate supportedMethods not empty
    PaymentGatewayOrchestrator->>ClientService: createClient(name, methods)
    ClientService->>ClientService: new Clients(UUID, name, methods)
    ClientService->>ClientsRepository: save(clients)
    ClientsRepository-->>ClientService: Clients
    ClientService-->>PaymentGatewayOrchestrator: Clients
    PaymentGatewayOrchestrator-->>Main: clientId
```

### Scenario 3: Bank Selection — PaymentModeRoutingStrategy

When routing by payment mode (e.g., all CREDIT_CARD → HDFC), the strategy looks up the configured bank for the payment method.

```mermaid
sequenceDiagram
    participant PaymentGatewayOrchestrator
    participant PaymentModeRoutingStrategy
    participant Banks

    PaymentGatewayOrchestrator->>PaymentModeRoutingStrategy: selectBank(request, banks)
    Note over PaymentModeRoutingStrategy: request.paymentMethod = CREDIT_CARD
    Note over PaymentModeRoutingStrategy: config: CREDIT_CARD -> HDFC bankId
    PaymentModeRoutingStrategy->>PaymentModeRoutingStrategy: paymentMethodToBankId.get(CREDIT_CARD)
    PaymentModeRoutingStrategy->>PaymentModeRoutingStrategy: filter banks by bankId + supportedMethods
    PaymentModeRoutingStrategy-->>PaymentGatewayOrchestrator: HDFC (Banks)
```

### Scenario 4: Bank Selection — WeightedRoutingStrategy

When routing by distribution (30% Bank1, 70% Bank2), the strategy uses weighted random selection.

```mermaid
sequenceDiagram
    participant PaymentGatewayOrchestrator
    participant WeightedRoutingStrategy

    PaymentGatewayOrchestrator->>WeightedRoutingStrategy: selectBank(request, banks)
    Note over WeightedRoutingStrategy: eligible banks support payment method
    Note over WeightedRoutingStrategy: weights: Bank1=0.3, Bank2=0.7
    WeightedRoutingStrategy->>WeightedRoutingStrategy: r = Math.random()
    WeightedRoutingStrategy->>WeightedRoutingStrategy: if r < 0.3 return Bank1 else Bank2
    WeightedRoutingStrategy-->>PaymentGatewayOrchestrator: Banks
```

### Scenario 5: Bank Processing with Mock Failure

Each bank has a configurable `failureRate`. When processing, the bank randomly fails with that probability.

```mermaid
sequenceDiagram
    participant PaymentGatewayOrchestrator
    participant Banks

    PaymentGatewayOrchestrator->>Banks: processPayment(request)
    Note over Banks: failureRate = 0.2 (20%)
    Banks->>Banks: Math.random() < 0.2 ?
    alt Failure (20% chance)
        Banks-->>PaymentGatewayOrchestrator: PaymentResult.failure(PAYMENT_FAILED, bankId)
    else Success (80% chance)
        Banks-->>PaymentGatewayOrchestrator: PaymentResult.success(transactionId, bankId)
    end
```

---

## Why Polymorphic PaymentDetails — Deep Dive

### The Problem

Each payment method has **different input fields**:

| Method | Fields |
|--------|--------|
| UPI | `vpaId` (format: `user@bank`) |
| Cards | `cardNumber`, `expiryDate`, `cvv`, `cardType` (CREDIT/DEBIT) |
| NetBanking | `username`, `password` |

A naive approach would be a single DTO with optional fields:

```java
// BAD: One giant DTO
class PaymentDetails {
    Optional<String> vpaId;
    Optional<String> cardNumber;
    Optional<String> expiry;
    Optional<String> cvv;
    Optional<String> username;
    Optional<String> password;
}
```

This leads to:

- **Giant switch/if-else** in validation: `if (method == UPI) validate vpa; else if (method == CREDIT_CARD) validate card; ...`
- **Violation of OCP**: Adding a new method (e.g., WALLET) requires touching validation, serialization, and routing.
- **Easy to misuse**: Caller might set both `vpaId` and `cardNumber`; which one applies?

### The Solution: Polymorphic PaymentDetails

Each payment method has its **own class** implementing a common interface:

```java
public interface PaymentDetails {
    PaymentMethods getPaymentMethod();
    void validate();
}
```

| Implementation | Own Fields | validate() Logic |
|----------------|------------|------------------|
| `UPIPaymentDetails` | `vpaId` | Non-null, contains `@` |
| `CardPaymentDetails` | `cardNumber`, `expiry`, `cvv`, `cardType` | Length, future expiry, cvv digits |
| `NetBankingPaymentDetails` | `username`, `password` | Non-null |

**Benefits:**

1. **No switch on payment method**: The orchestrator calls `paymentDetails.validate()` — each type validates itself.
2. **Open/Closed**: New method = new class + `validate()`. No changes to orchestrator.
3. **Type safety**: A `UPIPaymentDetails` cannot accidentally hold card fields.

### Adding a New Payment Method

1. Add enum value to `PaymentMethods` (e.g., `WALLET`).
2. Create `WalletPaymentDetails implements PaymentDetails` with `walletId`, `pin`, etc.
3. Implement `getPaymentMethod()` and `validate()`.
4. No changes to `PaymentGatewayOrchestrator`, `Banks`, or routing strategies.

---

## Payment Routing Strategies — Deep Dive

### PaymentModeRoutingStrategy

**Use case**: "All credit card transactions go to HDFC; all UPI goes to ICICI."

**Configuration**: `Map<PaymentMethods, String>` — payment method → bankId.

```java
Map.of(
    PaymentMethods.CREDIT_CARD, "hdfc-id",
    PaymentMethods.UPI, "icici-id"
);
```

**Logic**:

1. Look up `bankId` for `request.getPaymentMethod()`.
2. Find bank in list matching `bankId` and supporting the method.
3. Return that bank, or `null` if not found.

### WeightedRoutingStrategy

**Use case**: "30% of UPI transactions to Bank1, 70% to Bank2."

**Configuration**: `Map<String, Double>` — bankId → weight.

```java
Map.of("bank1-id", 0.3, "bank2-id", 0.7);
```

**Logic**:

1. Filter banks that support the payment method and have positive weight.
2. Normalize weights (sum to 1.0).
3. `r = Math.random()`; walk cumulative weights until `r < cumulative` → return that bank.

**Example**: Weights 0.3 and 0.7. If `r = 0.25` → Bank1. If `r = 0.5` → Bank2.

### Pluggable Strategy

The orchestrator receives `PaymentRoutingStrategy` via constructor. You can inject `PaymentModeRoutingStrategy` for production and `WeightedRoutingStrategy` for load testing — no code changes in the orchestrator.

---

## Bank Processing & Mock Failure

### Configurable failureRate

Each `Banks` instance has a `failureRate` (0.0 to 1.0). During `processPayment()`:

```java
if (Math.random() < failureRate) {
    return PaymentResult.failure(ErrorCode.PAYMENT_FAILED, bankId);
}
return PaymentResult.success(UUID.randomUUID().toString(), bankId);
```

**Use cases:**

- **Testing**: Set `failureRate = 0.5` to simulate 50% failures.
- **Production mock**: Set `failureRate = 0.1` for a test bank that occasionally fails.
- **Staging**: Set `failureRate = 0.0` for a stable test environment.

### ErrorCode Enum

`PaymentResult` includes an optional `ErrorCode`:

- `PAYMENT_FAILED` — Bank randomly rejected (mock).
- `BANK_UNAVAILABLE` — No bank could be selected by routing.
- `INVALID_PAYMENT_DETAILS` — Validation failed (null, wrong format, etc.).
- `INSUFFICIENT_FUNDS`, `TRANSACTION_TIMED_OUT` — Extensible for future use.

---

## How Components Work Together

### Onboarding Flow

```
Main → Orchestrator.onboardClient("Merchant1", [UPI, CREDIT_CARD])
     → validate supportedMethods not empty
     → ClientService.createClient()
     → ClientsRepository.save()
     → return clientId
```

### Capture Flow (Summary)

```
Main → Orchestrator.capturePayment(clientId, amount, method, details)
     → validate details not null, method matches details
     → ClientService.getClientById() — validate client exists
     → validate client supports method (null-safe)
     → paymentDetails.validate() — polymorphic
     → build PaymentRequest
     → BankService.getAllBanks()
     → PaymentRoutingStrategy.selectBank(request, banks)
     → if bank == null → return PaymentResult.failure(BANK_UNAVAILABLE)
     → bank.processPayment(request)
     → TrafficLogger.log(clientId, bank, result)
     → return PaymentResult
```

### Traffic Logging

Every capture attempt (success or failure) is logged:

- `clientId`, `bankId`, `PaymentResult`, `timestamp`
- Stored in `TrafficLogger` for audit and analytics
- Access via `orchestrator.getTrafficLogger().getLogs()`

### Remove Client

```
Main → Orchestrator.removeClient(clientId)
     → ClientService.removeClient()
     → ClientsRepository.remove(clientId)
```

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Facade** | `PaymentGatewayOrchestrator` | Single entry point; hides ClientService, BankService, routing, and logging. |
| **Strategy** | `PaymentRoutingStrategy` | Pluggable routing; swap PaymentMode vs Weighted without touching orchestrator. |
| **Polymorphism** | `PaymentDetails` (UPI, Card, NetBanking) | Each method has its own schema and validation; no switch/if-else. |
| **Repository** | `ClientsRepository`, `BankRepository` | Abstraction over storage; swap in-memory for DB. |
| **Dependency Injection** | Services and strategies via constructor | Testability; inject mocks for routing and repositories. |

---

## Running the Application

From the project root:

```bash
./gradlew runPaymentgateway
```

**What the demo does:**

1. Creates banks: HDFC (CC/Debit), ICICI (UPI/NetBanking), each with 20% failure rate.
2. Configures `PaymentModeRoutingStrategy`: CREDIT_CARD/DEBIT_CARD → HDFC, UPI/NET_BANKING → ICICI.
3. Onboards client "Merchant1" with UPI and CREDIT_CARD.
4. **Capture UPI payment** — routes to ICICI, processes (success or random failure), logs.
5. **Capture Credit Card payment** — routes to HDFC, processes, logs.
6. **Attempt NetBanking** (client doesn't support) — throws `UnsupportedPaymentMethodException`.
7. **Traffic logs** — prints all captured payments (client, bank, success, transactionId).
8. **Remove client** — removes Merchant1 from the gateway.

---

## Quick Reference

| Component | Responsibility |
|-----------|-----------------|
| **PaymentGatewayOrchestrator** | Facade; onboard, remove, capture; delegates to services and routing. |
| **ClientService** | Create client, get by ID, remove. |
| **BankService** | Create bank, get all banks, get by ID. |
| **PaymentRoutingStrategy** | Select bank for a payment request; implementations: PaymentMode, Weighted. |
| **PaymentDetails** | Polymorphic input for UPI, Card, NetBanking; owns `validate()`. |
| **Banks** | Process payment; random failure based on `failureRate`. |
| **TrafficLogger** | Log every capture attempt for audit. |
| **Repositories** | In-memory persistence for clients and banks. |

---

*This README serves as a complete tutorial. No code reading required.*
