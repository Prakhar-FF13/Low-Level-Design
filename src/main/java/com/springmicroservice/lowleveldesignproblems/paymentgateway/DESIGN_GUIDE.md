# Payment Gateway LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the Payment Gateway (Paytm-style) system in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| How does a client get onboarded? | Client registers with PG; provides list of supported payment methods; PG returns clientId + credentials. |
| Can one client use multiple banks? | Yes — routing is at PG level, not per-client. Client just initiates payment. |
| What does "routing by distribution" mean? | 30% to Bank1, 70% to Bank2 — use weighted random selection. |
| How does "dynamic routing by success rate" work (bonus)? | Track per-bank success/failure; route more to higher-success-rate banks. |
| Bank mocking — configurable failure %? | Bank has config like `failureRate: 0.2` (20% fail randomly). |

**Why this matters**: Interviewers like seeing you handle **multiple routing strategies** and **polymorphic payment methods** cleanly.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

### Domain Models

```
Client
├── id, name, credentials
└── supportedPaymentMethods (Set<PaymentMethod>)

Bank
├── id, name
├── failureRate (double, for mocking) — optional
└── processPayment(PaymentRequest) → PaymentResult

PaymentMethod (enum)
├── UPI
├── CREDIT_CARD
├── DEBIT_CARD
└── NET_BANKING

PaymentDetails (sealed/interface + implementations)
├── UPIPaymentDetails → vpaId
├── CardPaymentDetails → number, expiry, cvv
└── NetBankingPaymentDetails → username, password

PaymentRequest
├── clientId
├── amount
├── paymentMethod
├── paymentDetails (polymorphic)
└── metadata

PaymentResult
├── success (boolean)
├── transactionId
├── errorCode (optional)
└── bankId (which bank processed it)

PaymentRoutingRule (interface)
└── selectBank(PaymentRequest, List<Bank>) → Bank
```

### Key Relationships

- `Client` 1 — * `PaymentMethod` (subset)
- `PaymentGateway` — * `Client`, * `Bank`
- `PaymentRequest` → `PaymentMethod` + `PaymentDetails` (polymorphic)
- `Bank` processes `PaymentRequest` → `PaymentResult`
- **Routing**: `PaymentRoutingRule` selects which `Bank` for a given `PaymentRequest`

---

## Phase 3: Choose Design Patterns

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|------------------------|
| **Strategy** | `PaymentRoutingRule` (PaymentModeRouting, DistributionRouting, DynamicSuccessRateRouting) | Pluggable routing; add new strategies without touching core. OCP. |
| **Factory** | `PaymentDetailsFactory` or `PaymentValidatorFactory` | Create correct validator/processor per payment method. |
| **Facade** | `PaymentGateway` | Single entry: onboard client, capture payment, delegate to routing + bank. |
| **Polymorphism** | `PaymentDetails` (UPI, Card, NetBanking) | Each method has its own input schema — avoid giant switch/if-else. |
| **Repository** | `ClientRepository`, `BankRepository` | Abstract storage; in-memory for interview. |
| **Composite (optional)** | Routing rules — e.g., "CC → HDFC; UPI → 30/70 split" | Chain multiple routing rules for complex logic. |

---

## Phase 4: Core Logic — Matches Code

### 4.1 Client Onboarding

```
PaymentGateway.onboardClient(name, supportedMethods)
  → validate supportedMethods not empty
  → client = new Client(id, name, supportedMethods)
  → clientRepository.save(client)
  → return clientId
```

### 4.2 Payment Capture Flow

```
PaymentGateway.capturePayment(clientId, amount, method, details)
  → client = clientRepository.findById(clientId)
  → if (!client.supports(method)) throw UnsupportedPaymentMethodException
  → validatePaymentDetails(method, details)  // polymorphic validation
  → request = new PaymentRequest(clientId, amount, method, details)
  → bank = routingRule.selectBank(request, banks)
  → result = bank.processPayment(request)
  → logTraffic(clientId, bank, result)  // bonus: traffic logs
  → return result
```

### 4.3 Bank Processing (with Mock Failure)

```
Bank.processPayment(request)
  → if (random.nextDouble() < failureRate) return PaymentResult.failure(ERROR_CODE)
  → // Simulate success
  → return PaymentResult.success(transactionId, bankId)
```

### 4.4 Routing Strategies

**PaymentModeRouting** (e.g., all CC → HDFC):

```
selectBank(request, banks):
  return banks.stream()
    .filter(b -> routingConfig.get(request.paymentMethod) == b.id)
    .findFirst()
    .orElse(defaultBank)
```

**DistributionRouting** (30% Bank1, 70% Bank2):

```
selectBank(request, banks):
  double r = random.nextDouble()
  if (r < 0.3) return bank1
  return bank2
```

**DynamicSuccessRateRouting** (bonus):

```
selectBank(request, banks):
  sort banks by successRate descending
  pick top bank (or weighted random among top N)
```

### 4.5 Payment Details Validation (Polymorphic)

```
UPIPaymentDetails: validate vpaId non-null, format
CardPaymentDetails: validate number (length), expiry (future), cvv (3-4 digits)
NetBankingPaymentDetails: validate username, password non-null
```

Use **Strategy** or **polymorphic validate()** — avoid:

```java
if (method == UPI) { /* validate vpa */ }
else if (method == CREDIT_CARD) { /* validate card */ }
```

---

## Phase 5: Package Structure (Matches Code)

```
paymentgateway/
├── models/
│   ├── Client.java
│   ├── Bank.java
│   ├── PaymentMethod.java
│   ├── PaymentRequest.java
│   ├── PaymentResult.java
│   ├── details/
│   │   ├── PaymentDetails.java (interface)
│   │   ├── UPIPaymentDetails.java
│   │   ├── CardPaymentDetails.java
│   │   └── NetBankingPaymentDetails.java
│   └── ErrorCode.java (enum for failures)
├── routing/
│   ├── PaymentRoutingRule.java (interface)
│   ├── PaymentModeRoutingRule.java
│   ├── DistributionRoutingRule.java
│   └── DynamicSuccessRateRoutingRule.java  // bonus
├── repositories/
│   ├── ClientRepository.java
│   └── BankRepository.java
├── services/
│   ├── PaymentGateway.java (Facade)
│   ├── PaymentValidator.java (or per-method validators)
│   └── TrafficLogger.java  // bonus
├── exceptions/
│   ├── UnsupportedPaymentMethodException.java
│   ├── InvalidPaymentDetailsException.java
│   └── PaymentFailedException.java
├── config/
│   └── RoutingConfig.java  // paymentMethod → bankId, distribution weights
└── Main.java
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Onboard client | Supported methods not empty; clientId unique |
| Capture payment | Client exists; method in client's supported set; details valid for method |
| Bank selection | At least one bank available; routing config present for mode-based routing |
| Distribution routing | Weights sum to 1.0; fallback if no bank matches |
| Bank failure | Return structured PaymentResult with errorCode; don't throw (business failure ≠ exception) |

---

## Phase 7: Implementation Order (Recommended)

1. **Enums & Value Objects** — PaymentMethod, ErrorCode, PaymentResult
2. **PaymentDetails** — Interface + UPI, Card, NetBanking implementations
3. **Bank** — processPayment with random failure; configurable failureRate
4. **Client** — id, supportedMethods, supports(method)
5. **Repositories** — In-memory ClientRepository, BankRepository
6. **PaymentRoutingRule** — Interface; PaymentModeRoutingRule first
7. **DistributionRoutingRule** — Weighted random selection
8. **PaymentGateway (Facade)** — onboardClient, capturePayment, wire routing
9. **Validators** — Per-method validation for PaymentDetails
10. **Bonus** — DynamicSuccessRateRouting, TrafficLogger, ErrorCode enum

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Avoid switch/if-else on payment method** | Polymorphic PaymentDetails + Strategy for routing |
| **Pluggable routing** | PaymentRoutingRule interface; inject different strategies |
| **Single entry point** | PaymentGateway as Facade; clients/banks/routing encapsulated |
| **SOLID** | OCP: new payment method = new Details class + validator; new routing = new Rule. SRP: Bank processes, Routing selects, Gateway orchestrates. DIP: depend on PaymentRoutingRule interface. |
| **Real-world touch** | Error codes, configurable failure rate, traffic logging |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| Client onboarding | PaymentGateway.onboardClient, ClientRepository |
| Support subset of methods | Client.supportedPaymentMethods, supports(method) |
| Add/remove clients | ClientRepository.add, remove |
| Payment through bank | Bank.processPayment |
| Random success/failure | Bank.failureRate + Random in processPayment |
| UPI / Card / NetBanking input | PaymentDetails implementations (UPI, Card, NetBanking) |
| Routing by payment mode | PaymentModeRoutingRule |
| Routing by distribution | DistributionRoutingRule |
| Dynamic routing (bonus) | DynamicSuccessRateRoutingRule + success rate tracker |
| Traffic logs (bonus) | TrafficLogger in capturePayment flow |
| Error codes (bonus) | ErrorCode enum in PaymentResult |
| Bank mock config (bonus) | Bank.failureRate, RoutingConfig |

---

## Interview Tips: How to Present This

1. **Start with entities** — "I see Clients, Banks, Payment Methods, and Routing Rules. Let me map these out."
2. **Call out routing complexity** — "Routing can be mode-based or distribution-based; I'll use Strategy so we can add more without changing the core."
3. **Handle polymorphism early** — "Each payment method has different inputs; I'll use separate Detail classes rather than one big DTO with optional fields."
4. **Mention extensibility** — "New payment method = new Details + validator. New bank = add to list. New routing rule = new Rule implementation."
5. **Bonus features show depth** — If time permits, mention DynamicSuccessRateRouting and TrafficLogger to show you think beyond MVP.

---

## Run (After Implementation)

```bash
./gradlew runPaymentgateway
# Or add runPaymentgateway task to build.gradle
```
