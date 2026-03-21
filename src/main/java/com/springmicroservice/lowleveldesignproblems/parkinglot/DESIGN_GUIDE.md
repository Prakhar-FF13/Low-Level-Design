# Parking Lot LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the Parking Lot system in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| Match by vehicle type or by capabilities? | Capability-based (ParkingFeature sets); vehicles require features, slots provide features. |
| Slot-finding algorithm? | Pluggable (FirstFreeSlotStrategy, Best-Fit to minimize wasted features). |
| Payment calculation? | Pluggable (HourlyPaymentStrategy, FlatRate, etc.). |
| Real-time display boards? | Observer pattern — EventPublisher notifies ParkingEventListener. |
| Thread safety? | Stateless strategies; slot state in domain models. |

**Why this matters**: Interviewers like seeing you avoid enum explosion and use capability-based matching.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
ParkingLot
├── id, name, address
└── parkingFloors (List<ParkingFloor>)

ParkingFloor
├── parkingFloorId
└── slots (List<Slot>)

Slot (interface)
├── getId(), getWidth(), getHeight(), getType()
├── isFree(), assignVehicle(Vehicle), freeSlot()
├── getProvidedFeatures() → Set<ParkingFeature>
└── canFit(Vehicle) → boolean

CarSlot (implements Slot)
├── id
├── parkedVehicle
└── providedFeatures (Set<ParkingFeature>)

Vehicle
├── id, vehicleNum
└── vehicleType (VehicleType)

VehicleType (enum)
├── CAR, BIKE, ELECTRIC_CAR
└── getRequiredFeatures() → Set<ParkingFeature>

ParkingFeature (enum)
├── CAR_SIZE, BIKE_SIZE, EV_CHARGER, HYDROGEN_PUMP, SOLAR_PANEL
└── (Used for subset matching: slot provides ⊇ vehicle requires)

Ticket
├── carNumber
├── slot
└── inTime, outTime

Receipt
├── receiptNumber
├── ticket
└── fee
```

**Relationships**:
- `ParkingLot` 1 — * `ParkingFloor` — * `Slot`
- `Slot` o— `Vehicle` (when assigned)
- `Vehicle` → `VehicleType` → `getRequiredFeatures()`
- `Slot` → `getProvidedFeatures()`

---

## Phase 3: Choose Design Patterns

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|------------------------|
| **Strategy** | SlotDeterminingStrategy, PaymentStrategy | Pluggable slot-finding and pricing; OCP. |
| **Facade** | ParkingLotManager | Single entry point; delegates to TicketService, PaymentService, EventPublisher. |
| **Observer** | EventPublisher, ParkingEventListener, DisplayBoardUpdater | Real-time display boards; loose coupling. |
| **State in slots** | Slot.isFree(), assignVehicle(), freeSlot() | State lives in domain, not in a HashMap; stateless strategies. |

---

## Phase 4: Core Algorithms — Matches Code

### 4.1 Compatibility (Capability Subset)

```
CompatibilityEngine.canFit(Vehicle, Slot):
  return slot.getProvidedFeatures().containsAll(vehicle.getVehicleType().getRequiredFeatures())
```

### 4.2 Best-Fit Slot Selection (FirstFreeSlotStrategy)

- Iterate floors → slots
- For each slot: if canFit, compute extraFeatures = slot.provided.size() - vehicle.required.size()
- Pick slot with **minimum** extraFeatures (exact match = 0)
- Prevents standard car from taking EV slot when regular slot exists

### 4.3 Payment (HourlyPaymentStrategy)

- Uses ticket.inTime, ticket.outTime to compute duration
- fee = f(duration) — implement per business rule

### 4.4 Observer Flow

```
ParkingLotManager.getReceipt(vehicle)
  → freeSlot()
  → eventPublisher.publishVehicleUnparked(slot.getType())
  → DisplayBoardUpdater.onVehicleUnparked() updates DisplayBoard
```

---

## Phase 5: Package Structure (Matches Code)

```
parkinglot/
├── models/
│   ├── ParkingLot.java
│   ├── ParkingFloor.java
│   ├── Slot.java (interface)
│   ├── CarSlot.java
│   ├── Vehicle.java
│   ├── VehicleType.java
│   ├── ParkingFeature.java
│   ├── Ticket.java
│   ├── Receipt.java
│   └── DisplayBoard.java
├── strategies/
│   ├── SlotDeterminingStrategy.java
│   ├── FirstFreeSlotStrategy.java
│   ├── CompatibilityEngine.java
│   ├── PaymentStrategy.java
│   └── HourlyPaymentStrategy.java
├── services/
│   ├── ParkingLotManager.java (Facade)
│   ├── TicketService.java
│   └── PaymentService.java
├── observer/
│   ├── ParkingEventListener.java
│   ├── DisplayBoardUpdater.java
│   └── EventPublisher.java
├── exceptions/
│   ├── ParkingLotFullException.java
│   └── InvalidTicketException.java
└── ParkingLotApplication.java
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Park vehicle | Slot exists and canFit; throw ParkingLotFullException if none |
| Unpark | Ticket exists; throw InvalidTicketException if not |
| Best-fit | Prefer slot with exact match (0 extra features) over EV slot for regular car |

---

## Phase 7: Implementation Order (Recommended)

1. **Models** — ParkingFeature, VehicleType, Vehicle, Slot, CarSlot, ParkingFloor, ParkingLot, Ticket, Receipt
2. **CompatibilityEngine** — canFit(Vehicle, Slot)
3. **SlotDeterminingStrategy** — FirstFreeSlotStrategy with best-fit
4. **PaymentStrategy** — HourlyPaymentStrategy
5. **TicketService** — save, get, remove ticket
6. **PaymentService** — processPayment
7. **EventPublisher + DisplayBoardUpdater** — Observer
8. **ParkingLotManager** — Facade wiring
9. **ParkingLotApplication** — CLI

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Avoid enum explosion** | Capability-based matching (ParkingFeature sets) instead of vehicle-type enums |
| **Stateless strategies** | Slot state in domain; strategies hold no mutable state |
| **SOLID** | Facade delegates; Strategy for slot-finding and payment; Observer for display |
| **Extensibility** | New vehicle type = add getRequiredFeatures(); new slot type = add providedFeatures |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| Match vehicle to slot | CompatibilityEngine, ParkingFeature, VehicleType |
| Best-fit slot selection | FirstFreeSlotStrategy |
| Ticket storage | TicketService |
| Fee calculation | PaymentService, PaymentStrategy |
| Real-time displays | EventPublisher, ParkingEventListener, DisplayBoardUpdater |
| Single entry point | ParkingLotManager (Facade) |

---

## Run

```bash
./gradlew compileJava
java -cp build/classes/java/main com.springmicroservice.lowleveldesignproblems.parkinglot.ParkingLotApplication

# Commands: PARK CAR MH-04-1234, PARK ELECTRIC_CAR EV-9876, UNPARK MH-04-1234, EXIT
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.parkinglot.*"
```
