# BookMyShow LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the movie ticket booking system using DDD and Hexagonal Architecture in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| Scope? | Booking flow only — no auth, payments, search. |
| Concurrency? | Two users booking same seat — must not double-book. |
| Persistence? | Database (H2/JPA); domain stays pure. |
| REST API? | GET available seats; POST book seats. |
| Lock strategy? | Pessimistic locking (SELECT ... FOR UPDATE). |

**Why this matters**: Concurrency + seat locking is the core interview focus.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
Movie
├── id, title
└── shows (List<Show>)

Theater
├── id, address
└── screens (List<Screen>)

Screen
├── id, rows, columns
├── theater
└── seats (List<Seat>)

Seat
├── id, seatNumber, row, column
└── screen

Show
├── showId
├── movie, screen
└── (screening at a time)

ShowSeats (availability for a seat in a show)
├── id
├── seat, show
├── ticket (nullable)
├── status (AVAILABLE, LOCKED, BOOKED)
└── (Seat A1 for Show #1)

Ticket
├── ticketId
├── show
└── showSeats (List<ShowSeats>)

SeatStatus (enum)
└── AVAILABLE, LOCKED, BOOKED
```

**Relationships**:
- Theater → Screen → Seat
- Movie + Screen → Show
- Show + Seat → ShowSeats (one per seat per show)
- Ticket → ShowSeats (booked seats)

---

## Phase 3: Choose Architecture: DDD + Hexagonal

| Layer | Purpose | Contains |
|-------|---------|----------|
| **Domain** | Pure business logic | Models, enums, BookingDomainService, ports (interfaces) |
| **Application** | Use-case orchestration | BookingService |
| **API** | REST | Controller, DTOs |
| **Infrastructure** | External systems | JPA entities, adapters, repositories |

| Port (interface) | Adapter (implementation) |
|-------------------|----------------------------|
| ShowRepositoryPort | ShowRepositoryAdapter |
| ShowSeatRepositoryPort | ShowSeatRepositoryAdapter |
| TicketRepositoryPort | TicketRepositoryAdapter |

---

## Phase 4: Core Logic — Matches Code

### 4.1 Get Available Seats

```
GET /bookings/shows/{showId}/available-seats
→ BookingService.getAvailableSeats(showId)
→ ShowSeatRepositoryPort.findByShowIdAndStatus(showId, AVAILABLE)
→ Map to AvailableSeatResponse
```

### 4.2 Book Seats (Critical Path)

```
POST /bookings { showId, seatIds }
→ BookingService.bookSeats(showId, seatIds) [@Transactional]
  1. Load show
  2. findByIdInForUpdate(seatIds)  [SELECT ... FOR UPDATE — locks rows]
  3. BookingDomainService.createTicket(show, showSeats)
     - validateBooking: seats belong to show, status == AVAILABLE
     - Create Ticket
     - Set each ShowSeats.status = BOOKED, ticket = ticket
  4. TicketRepositoryPort.save(ticket)
  5. Commit [releases locks]
```

### 4.3 Concurrency (Pessimistic Locking)

- User A: findByIdInForUpdate([1,2,3]) → acquires locks
- User B: findByIdInForUpdate([1]) → blocks
- User A: validate, create ticket, save, commit → releases
- User B: gets lock, loads seats (now BOOKED) → domain validation fails → 400

### 4.4 Domain vs JPA

- Domain: Movie, Show, ShowSeats, Ticket (no @Entity)
- Infrastructure: MovieEntity, ShowEntity, etc. (JPA)
- EntityMapper: toDomain(), toEntity()

---

## Phase 5: Package Structure (Matches Code)

```
bookmyshow/
├── domain/
│   ├── models/
│   │   ├── Movie.java
│   │   ├── Theater.java
│   │   ├── Screen.java
│   │   ├── Seat.java
│   │   ├── Show.java
│   │   ├── ShowSeats.java
│   │   └── Ticket.java
│   ├── enums/
│   │   └── SeatStatus.java
│   ├── ports/
│   │   ├── ShowRepositoryPort.java
│   │   ├── ShowSeatRepositoryPort.java
│   │   └── TicketRepositoryPort.java
│   ├── services/
│   │   └── BookingDomainService.java
│   └── exceptions/
│       └── BookingException.java
├── application/
│   └── booking/
│       └── BookingService.java
├── api/
│   ├── controller/
│   │   └── BookingController.java
│   └── dto/
│       ├── BookingRequest.java
│       ├── BookingResponse.java
│       └── AvailableSeatResponse.java
└── infrastructure/
    ├── adapter/
    │   ├── ShowRepositoryAdapter.java
    │   ├── ShowSeatRepositoryAdapter.java
    │   └── TicketRepositoryAdapter.java
    ├── persistence/
    │   ├── entity/
    │   ├── repository/
    │   └── mapper/
    │       └── EntityMapper.java
    └── DataSeeder.java
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Show not found | 400 |
| SeatIds empty | 400 |
| Seat not found | 400 (size mismatch) |
| Seat not for show | BookingDomainService throws |
| Seat not AVAILABLE | BookingDomainService throws (concurrent booking) |
| Lock ordering | Always lock seats in consistent order to avoid deadlock |

---

## Phase 7: Implementation Order (Recommended)

1. **Domain models** — Movie, Theater, Screen, Seat, Show, ShowSeats, Ticket, SeatStatus
2. **Ports** — interfaces
3. **BookingDomainService** — createTicket, validateBooking
4. **JPA entities + repositories**
5. **EntityMapper**
6. **Adapters** — implement ports
7. **BookingService** — orchestration + findByIdInForUpdate
8. **Controller + DTOs**
9. **DataSeeder**

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Domain purity** | No JPA/Spring in domain |
| **Ports & Adapters** | Core depends on interfaces; adapters in infrastructure |
| **Concurrency** | Pessimistic locking; lock-then-validate |
| **Transactional boundary** | @Transactional on bookSeats |
| **Validation** | Domain service validates; controller uses @Valid |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| View available seats | BookingService, ShowSeatRepositoryPort.findByShowIdAndStatus |
| Book seats | BookingService, BookingDomainService |
| Concurrent safety | findByIdInForUpdate (pessimistic lock) |
| Domain logic | BookingDomainService.createTicket |
| Persistence | Adapters, JPA entities, EntityMapper |

---

## Run

```bash
./gradlew bootRun
curl http://localhost:8080/bookings/shows/1/available-seats
curl -X POST http://localhost:8080/bookings -H "Content-Type: application/json" -d '{"showId":1,"seatIds":[1,2,3]}'
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.bookmyshow.*"
```
