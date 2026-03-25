# The Complete LLD Meta-Guide: How to Think, Ask, and Design Like a Senior Engineer

> **Purpose**: This document teaches you how to systematically create detailed design guidelines for any Low-Level Design problem — in interviews or practice. Use it as your reference to decide inputs/outputs, know what questions to ask, and build the insight needed to excel at LLD.

---

## Table of Contents

1. [Part I: The LLD Mindset](#part-i-the-lld-mindset)
2. [Part II: The Clarification Framework](#part-ii-the-clarification-framework)
3. [Part III: Input/Output Decision Framework](#part-iii-inputoutput-decision-framework)
4. [Part IV: Universal Question Bank](#part-iv-universal-question-bank)
5. [Part V: Domain-Specific Question Banks](#part-v-domain-specific-question-banks)
6. [Part VI: Requirement Parsing & Entity Extraction](#part-vi-requirement-parsing--entity-extraction)
7. [Part VII: Design Guideline Creation Process](#part-vii-design-guideline-creation-process)
8. [Part VIII: Templates & Checklists](#part-viii-templates--checklists)
9. [Part IX: Interview Scripts & What to Say](#part-ix-interview-scripts--what-to-say)
10. [Part X: Practice Strategies & Self-Assessment](#part-x-practice-strategies--self-assessment)
11. [Part XI: Pattern Recognition](#part-xi-pattern-recognition--when-to-use-which-design-pattern)
12. [Part XII: Detailed Worked Examples](#part-xii-detailed-worked-examples--from-requirements-to-design)
13. [Part XIII: Requirement Ambiguity Catalog](#part-xiii-requirement-ambiguity-catalog)
14. [Part XIV: Input/Output Anti-Patterns](#part-xiv-inputoutput-anti-patterns-and-fixes)
15. [Part XV: Domain-Specific I/O Examples](#part-xv-domain-specific-inputoutput-examples)
16. [Part XVI: Interview Time Management](#part-xvi-interview-time-management-script)
17. [Part XVII: Self-Questioning Prompts](#part-xvii-self-questioning-prompts-ask-yourself-while-designing)
18. [Part XVIII: Repo Problem Mapping](#part-xviii-mapping-your-repos-problems-to-this-guide)
19. [Part XIX: Interview Readiness Checklist](#part-xix-checklist--am-i-ready-for-an-lld-interview)
20. [Part XX: Final Thoughts](#part-xx-final-thoughts--building-the-insight)

---

# Part I: The LLD Mindset

## 1.1 Why You Get Stuck: The Root Cause

When you're stuck on inputs/outputs, clarification, or design guidelines, it's usually because:

1. **Requirements are underspecified** — They leave room for multiple valid interpretations.
2. **You don't have a framework** — You don't know *which* questions to ask or *how* to organize your thinking.
3. **You jump to code** — You start designing classes before you've locked down behavior.
4. **You assume instead of clarifying** — You pick one interpretation silently and build on it, only to be surprised when the interviewer had something else in mind.

**The fix**: Treat clarification as a **first-class phase**. Design your *process* for design.

---

## 1.2 The Golden Rule

> **Never design before clarifying. Never clarify without a checklist. Never code before writing a design guideline.**

Your mental model:

```
Requirements (vague) → Clarification (questions + assumptions) → Design Guideline (structured doc) → Code
```

If you skip the middle steps, you'll thrash.

---

## 1.3 What a "Design Guideline" Really Is

A design guideline is not magic. It's the **crystallization of your clarified requirements** into:

1. **Assumptions table** — What you decided (or the interviewer confirmed).
2. **Entity model** — Nouns and relationships.
3. **Core logic** — How flows work, step by step.
4. **Validations & edge cases** — What can go wrong.
5. **Implementation order** — What to build first.

**The insight**: If you ask the right questions and record the answers, the design guideline almost writes itself.

---

## 1.4 The Three Pillars of LLD Excellence

| Pillar | What It Means |
|--------|---------------|
| **Clarification** | Ask questions; state assumptions; get agreement before designing. |
| **Abstraction** | Model the domain with clear entities; avoid god objects and procedural code. |
| **Extensibility** | Use Strategy, State, Observer, etc., so new behavior = new class, not new if/else. |

This guide focuses heavily on **Clarification** because that's where most people get stuck.

---

# Part II: The Clarification Framework

## 2.1 The 7 Dimensions of Clarification

For any LLD problem, you need to clarify across these dimensions:

| Dimension | What You're Clarifying | Example Questions |
|-----------|------------------------|-------------------|
| **Scope** | What's in/out of the problem | "Should I include authentication, or focus only on the booking flow?" |
| **State & Lifecycle** | How entities evolve over time | "Does the auction have states like OPEN, CLOSED, ENDED?" |
| **Inputs & Outputs** | What goes in, what comes out | "What does the user provide when placing a bid? Just amount, or more?" |
| **Rules & Invariants** | What must always be true | "Can a buyer bid multiple times in the same auction?" |
| **Edge Cases** | Boundary conditions, error handling | "What if two users book the same seat simultaneously?" |
| **Non-Functional** | Concurrency, persistence, performance | "Should we assume single-threaded or handle concurrent access?" |
| **Extensibility** | What might change later | "Could we add new payment methods or routing strategies?" |

---

## 2.2 The Clarification Process (Step-by-Step)

1. **Listen** — Hear the full problem statement. Don't interrupt.
2. **Restate** — "So if I understand correctly, we need to build X that does Y and Z."
3. **List open questions** — Mentally or on paper, list what's ambiguous.
4. **Prioritize** — Ask the highest-impact questions first (scope, core flows).
5. **State assumptions** — For each question: "If you don't have a preference, I'll assume X."
6. **Record** — Put your assumptions in a table. Reference it while designing.

---

## 2.3 How to Phrase Questions in Interviews

**Good**:
- "Can a buyer place multiple bids in the same auction, or only one?"
- "When the cache is full, should we evict using LRU, LFU, or something else?"
- "If you don't mind, I'll assume we're building this in-memory for the interview. Is that okay?"

**Bad**:
- "How do bids work?" (too vague)
- "Should I use a HashMap?" (implementation detail; too early)
- "I think we need a database." (assuming without asking)

**Pattern**: Ask about **behavior and rules**, not implementation.

---

# Part III: Input/Output Decision Framework

## 3.1 Why Inputs/Outputs Feel Hard

Inputs and outputs are hard because:
- Requirements rarely spell them out.
- There are often multiple valid designs.
- You're not sure what level of granularity to use.

**Solution**: Use a systematic framework.

---

## 3.2 The Input Framework

For any operation (e.g., "place a bid", "park a vehicle", "fire at coordinate"):

### Step 1: Identify the Actor
Who is performing the operation?
- User, System, External Service, Another component

### Step 2: What Does the Actor Know?
What information would they realistically have at call time?
- IDs (user, auction, vehicle)
- Values (amount, coordinates, payment details)
- Context (session, game state)

### Step 3: What Must Be Provided vs. Derived?
- **Provided**: Caller must supply (e.g., `auctionId`, `amount`)
- **Derived**: System can infer (e.g., `buyerId` from session, `timestamp` from system)

### Step 4: Minimize, But Don't Omit
- Prefer **minimal necessary input** — don't ask for things the system can derive.
- But **don't omit required identifiers** — e.g., if booking seats, you need `showId` and `seatIds`.

### Input Decision Checklist
- [ ] Actor identified?
- [ ] All required identifiers included?
- [ ] No redundant fields (that can be derived)?
- [ ] Polymorphic input? (e.g., `PaymentDetails` — UPI vs Card — different shapes)

---

## 3.3 The Output Framework

### Step 1: What Does the Caller Need?
- Success/failure?
- Created resource (ID, full object)?
- Side effects (e.g., receipt printed)?
- Partial results (e.g., list of violations)?

### Step 2: Failure Modes
- **Return value**: `Optional<T>`, `Result<T, E>`, `Either<L, R>`
- **Exception**: Domain exceptions (e.g., `SeatAlreadyBookedException`)
- **Error object**: `PaymentResult(success=false, errorCode=INSUFFICIENT_FUNDS)`

### Step 3: Consistency
- Same operation should return same shape (e.g., `FireResult` always has `hit`, `shipDestroyed`, `coordinate`).
- For list operations: empty list vs. null? → Prefer empty list.

### Output Decision Checklist
- [ ] Success case: what is returned?
- [ ] Failure case: exception, error object, or null/empty?
- [ ] Is the return type immutable or mutable? (Prefer immutable)
- [ ] Does the caller need to distinguish multiple failure types?

---

## 3.4 API Design Principles for LLD

1. **Explicit over implicit** — `fire(attacker, target, coordinate)` is clearer than `fire(coordinate)` if turn is implicit.
2. **Single responsibility per method** — `placeShip()` doesn't also display the board.
3. **Avoid primitive obsession** — Use `Coordinate` not `(int x, int y)`; use `PaymentDetails` not `Map<String, Object>`.
4. **Consistent naming** — `createX`, `getX`, `updateX`, `deleteX` or `removeX`.
5. **Idempotency when applicable** — "Register subscriber" — if already registered, no-op or error?

---

## 3.5 Worked Example: Battleship `fire()`

**Requirement**: "Players take turns firing at the opponent's grid."

**Input analysis**:
- Actor: Game (orchestrator) or Player (via Game)
- What's needed: Who is firing (current turn), where to fire (coordinate)
- Derived: Target = opponent of current turn
- **Input**: `fire(Coordinate coordinate)` — Game knows current turn, so coordinate alone suffices.

**Output analysis**:
- Caller needs: Did we hit? Was a ship destroyed? Game over?
- **Output**: `FireResult(hit: boolean, shipDestroyed: boolean, coordinate: Coordinate, message: String)` — or include `gameOver: boolean`

**Edge outputs**:
- Wrong turn → `OutOfTurnException`
- Already fired there → `CoordinateAlreadyFiredException`
- Invalid coordinate → `InvalidCoordinateException`

---

## 3.6 Worked Example: BookMyShow `bookSeats()`

**Requirement**: "User books selected seats for a show."

**Input analysis**:
- Actor: User (via API)
- Required: `showId`, `seatIds` (list)
- Derived: `userId` from auth/session (out of scope for LLD, but assume passed)
- **Input**: `bookSeats(showId: Long, seatIds: List<Long>)` or `BookSeatRequest(showId, seatIds)`

**Output analysis**:
- Success: Created ticket with ID, seat details
- **Output**: `Ticket` or `BookingResponse(ticketId, seats, amount)`
- Failure: Seat taken, show not found, invalid seat IDs
- **Exception**: `SeatAlreadyBookedException`, `ShowNotFoundException`, `InvalidSeatException`

---

## 3.7 Worked Example: Payment Gateway `capturePayment()`

**Requirement**: "Client initiates payment; gateway routes to bank."

**Input analysis**:
- Actor: Client (merchant)
- Required: `clientId`, `amount`, `paymentMethod`, payment-specific details (UPI: VPA, Card: number/expiry/cvv)
- **Polymorphic**: Payment details vary by method
- **Input**: `capturePayment(clientId, amount, paymentMethod, PaymentDetails details)` — where `PaymentDetails` is interface with UPI/Card/NetBanking implementations

**Output analysis**:
- Success: `PaymentResult(success=true, transactionId, bankId)`
- Failure: `PaymentResult(success=false, errorCode)` — business failure, not exception
- **Design choice**: Don't throw on bank rejection; return structured result (real-world APIs do this)

---

# Part IV: Universal Question Bank

Use these questions for **any** LLD problem. Customize as needed.

## 4.1 Scope Questions

| # | Question | When to Ask |
|---|----------|-------------|
| 1 | What exactly is in scope for this problem? | Always, first |
| 2 | What should I explicitly exclude (e.g., auth, payments, search)? | When problem is broad |
| 3 | Is this a CLI, API, or library? Who is the consumer? | When interface type is unclear |
| 4 | Should I focus on one flow (e.g., booking only) or the full system? | When time-boxed (e.g., 1 hour) |
| 5 | Are there any "bonus" or "nice-to-have" features I should know about? | To prioritize |

## 4.2 State & Lifecycle Questions

| # | Question | When to Ask |
|---|----------|-------------|
| 1 | Does [entity] have a lifecycle or states (e.g., OPEN, CLOSED)? | When entity can change over time |
| 2 | What triggers a state transition? | When lifecycle exists |
| 3 | Can [entity] be modified after creation? If so, what can change? | When mutation is possible |
| 4 | Is there a concept of "session" or "context" that spans operations? | For multi-step flows |
| 5 | What happens when [terminal state] is reached? (e.g., game over, auction closed) | For completion conditions |

## 4.3 Input/Output Questions

| # | Question | When to Ask |
|---|----------|-------------|
| 1 | What does the caller provide when invoking [operation]? | For each main operation |
| 2 | What should [operation] return on success? | For each main operation |
| 3 | How should failures be communicated — exceptions, error codes, or return values? | When failure modes exist |
| 4 | Are there different types of input for [polymorphic concept]? (e.g., UPI vs Card) | When multiple variants |
| 5 | Should the output be immutable? | When passing results around |

## 4.4 Rules & Invariants Questions

| # | Question | When to Ask |
|---|----------|-------------|
| 1 | Can [entity A] be associated with multiple [entity B]? (e.g., one user, many orders) | For relationships |
| 2 | Is there a uniqueness constraint? (e.g., one bid per buyer per auction) | When duplicates might occur |
| 3 | What are the valid ranges or formats for [field]? | For validation |
| 4 | Are there any ordering guarantees? (e.g., FIFO, priority) | For queues, processing |
| 5 | Can [action] be performed more than once? Idempotent? | For retries, duplicate requests |

## 4.5 Edge Case Questions

| # | Question | When to Ask |
|---|----------|-------------|
| 1 | What if two users try to [competing action] at the same time? | For shared resources |
| 2 | What if [resource] is full/empty/not found? | For capacity, existence |
| 3 | What if input is invalid (null, negative, out of range)? | Always |
| 4 | What if [operation] is called in wrong order or wrong state? | For stateful systems |
| 5 | Are there any timeout or expiry scenarios? | For time-sensitive data |

## 4.6 Non-Functional Questions

| # | Question | When to Ask |
|---|----------|-------------|
| 1 | Should we assume single-threaded or handle concurrency? | For shared mutable state |
| 2 | Where does data live — in-memory, or do we need persistence? | When data must survive restarts |
| 3 | Are there any performance constraints (e.g., O(1) eviction)? | When algorithms matter |
| 4 | Do we need to support multiple instances (distributed) or single process? | For scaling |
| 5 | Is thread safety a requirement? | For concurrent access |

## 4.7 Extensibility Questions

| # | Question | When to Ask |
|---|----------|-------------|
| 1 | Could we add new [variant] in the future? (e.g., new payment method, new vehicle type) | When variants exist |
| 2 | Should [algorithm/strategy] be pluggable or fixed? | When multiple algorithms possible |
| 3 | Will there be different types of [entity] with different behavior? | When polymorphism might help |
| 4 | Is this a library that others will extend? | When designing for reuse |

---

# Part V: Domain-Specific Question Banks

## 5.1 Game Problems (Tic-Tac-Toe, Battleship, etc.)

| Category | Question |
|----------|----------|
| Grid/Board | What is the grid size? Fixed or configurable? |
| Grid/Board | How is the grid divided (e.g., per player territory)? |
| Turns | Who goes first? How is turn order determined? |
| Turns | Does a miss/hit change turn? (e.g., hit = extra turn?) |
| Pieces/Units | What do pieces look like? Size, shape, orientation? |
| Pieces/Units | Can pieces overlap? Touch? |
| Winning | What defines a win? All cells, pattern, elimination? |
| Winning | Is there a draw condition? |
| View | What does each player see? Own pieces, opponent's? Fog of war? |
| Input | Manual input (coordinates) or automated (e.g., random targeting)? |
| Input | Is placement manual or random? |

## 5.2 Booking/Reservation Problems (BookMyShow, Hotel, etc.)

| Category | Question |
|----------|----------|
| Concurrency | What if two users book the same seat/resource? |
| Concurrency | Locking strategy — optimistic or pessimistic? |
| Lifecycle | Is there a "hold" or "lock" period before confirm? |
| Lifecycle | Can a booking be cancelled? Refund? |
| Scope | Just booking, or also search, payments, notifications? |
| Scope | Single show/session or multiple? |
| Validation | What makes a seat "available"? |
| Validation | Can users book non-contiguous seats? |

## 5.3 Payment/Routing Problems (Payment Gateway, etc.)

| Category | Question |
|----------|----------|
| Payment methods | What methods? UPI, Card, NetBanking, etc.? |
| Payment methods | Does each method have different input schema? |
| Routing | How is the bank/processor selected? |
| Routing | Fixed routing, weighted distribution, or dynamic (success rate)? |
| Failure | How to handle bank rejection? Retry? |
| Failure | Do we return error codes or throw? |
| Onboarding | How does a client get onboarded? |
| Onboarding | Can one client use multiple banks? |

## 5.4 Parking/Slot Allocation Problems

| Category | Question |
|----------|----------|
| Matching | Match by vehicle type or by capabilities/features? |
| Matching | Slot selection — first available, best fit, closest? |
| Slot types | Are slots typed (Car, Bike, EV) or feature-based? |
| Payment | How is fee calculated? Hourly, flat, tiered? |
| Display | Real-time display boards? Event-driven? |
| Capacity | What if lot is full? |
| Vehicle | What vehicle types? Do we need to add more later? |

## 5.5 Cache/Storage Problems

| Category | Question |
|----------|----------|
| Eviction | What policy when full? LRU, LFU, FIFO, other? |
| Eviction | Can policy be changed at runtime? |
| Persistence | In-memory only, or persist to disk/DB? |
| Persistence | Write-through or write-back? |
| TTL | Do entries expire? Lazy or proactive cleanup? |
| Concurrency | Single-threaded or concurrent access? |
| Capacity | Fixed or dynamic? |

## 5.6 Queue/Messaging Problems

| Category | Question |
|----------|----------|
| Queue type | Bounded or unbounded? |
| Queue type | Custom implementation or use library? (Often "custom" in LLD) |
| Delivery | Push (callback) or pull (consumer polls)? |
| Delivery | Single consumer or fan-out (multiple subscribers)? |
| Failure | Retry on consumer failure? Backoff? DLQ? |
| Ordering | FIFO guaranteed? |
| Message format | What does a message look like? JSON, typed object? |

## 5.7 Rule Engine / Validation Problems

| Category | Question |
|----------|----------|
| Rule types | Single-entity rules vs. aggregate rules? |
| Rule types | Can rules depend on each other? Priority? |
| Output | All violations or first failure? |
| Output | What does a violation look like? |
| Extensibility | Add new rules without changing engine? |
| Input | What is being validated? Single item or batch? |

## 5.8 ATM / State Machine Problems

| Category | Question |
|----------|----------|
| States | What states exist? Idle, CardInserted, Dispensing, etc.? |
| Transitions | What triggers each transition? |
| Validation | How is card validated? PIN? (Scope?) |
| Invalid actions | What happens if user does wrong action in state? |
| Concurrency | One user at a time? |

---

# Part VI: Requirement Parsing & Entity Extraction

## 6.1 The Noun-Verb Technique

**Step 1**: Underline all **nouns** in the requirement. These are candidate entities.
**Step 2**: Underline all **verbs** (actions). These are candidate operations/methods.
**Step 3**: Map nouns to entities, verbs to methods. Eliminate duplicates and group.

**Example** (Battleship):
- Nouns: grid, player, ship, cell, coordinate, turn, hit, miss, game
- Verbs: place, fire, destroy, view, alternate
- Entities: Grid, Player, Ship, Cell, Coordinate, Game, Turn
- Methods: placeShip(), fire(), isDestroyed(), getView(), nextTurn()

---

## 6.2 Entity Relationship Patterns

| Pattern | Description | Example |
|---------|-------------|---------|
| 1-to-many | One parent, many children | Game → Players, ParkingLot → Floors |
| Many-to-many | Both sides can have multiple | User ↔ Auction (via Bid) |
| Composition | Child can't exist without parent | Cell belongs to Battlefield |
| Aggregation | Child can exist independently | Car can exist without ParkingLot |
| Inheritance | Is-a relationship | Buyer, Seller extend User |
| Association | Uses or references | Game uses TurnManager |

---

## 6.3 Flow Extraction

For each user story or requirement, write:

1. **Trigger**: What initiates the flow?
2. **Preconditions**: What must be true before?
3. **Steps**: Numbered sequence.
4. **Postconditions**: What is true after?
5. **Alternate paths**: What if X fails?
6. **Output**: What is returned?

**Template**:
```
Flow: [Name]
Trigger: [Event/Action]
Preconditions: [List]
Steps:
  1. ...
  2. ...
Postconditions: [List]
Alternate: If [condition], then [action]
Output: [Type]
```

---

## 6.4 Invariant Extraction

Invariants are things that must *always* be true. Ask:
- "What must never happen?"
- "What relationship must always hold?"

Examples:
- Battleship: A coordinate can be fired at most once per player.
- BookMyShow: A seat can only be booked once per show.
- Parking: A slot can hold at most one vehicle at a time.
- Auction: Bid amount must be within [lowestBid, highestBid].

List invariants in your design guide. They become validations.

---

# Part VII: Design Guideline Creation Process

## 7.1 The 10-Phase Template

Use this structure for any LLD problem. Fill in each phase after clarification.

### Phase 1: Clarify & Scope
- Table: Question | Your Assumption
- "Why this matters" — 1-2 sentences on interviewer focus

### Phase 2: Core Entities & Relationships
- List entities with attributes and key methods
- Relationship diagram (text or mermaid)

### Phase 3: Design Patterns
- Table: Pattern | Where | Why

### Phase 4: Core Logic
- Step-by-step flow for each main operation
- Pseudocode or structured bullets

### Phase 5: Package Structure
- Directory tree

### Phase 6: Validations & Edge Cases
- Table: Scenario | Validation

### Phase 7: Implementation Order
- Numbered list of components to build first

### Phase 8: What Makes a Strong Hire
- Table: Attribute | How to Show It

### Phase 9: Quick Reference
- Table: Requirement | Primary Component

### Phase 10: (Optional) Sequence Diagrams, UML
- For complex flows

---

## 7.2 How to Populate Each Phase from Your Clarification

| Phase | Source |
|-------|--------|
| Phase 1 | Your clarification questions + answers = Assumptions table |
| Phase 2 | Nouns from requirements + relationships = Entities |
| Phase 3 | "What might change?" + "How to extend?" = Patterns |
| Phase 4 | Flows you extracted (Section 6.3) = Core Logic |
| Phase 5 | Entities + patterns = Package structure (group by domain) |
| Phase 6 | Edge case questions + invariants = Validations |
| Phase 7 | Dependencies: build value objects first, then entities, then services |
| Phase 8 | What interviewers evaluate = Strong hire attributes |
| Phase 9 | Trace each requirement to a component = Quick reference |

---

## 7.3 Time-Boxing for Interviews

| Phase | Suggested Time (45–60 min interview) |
|-------|--------------------------------------|
| Clarify & Scope | 5–8 min |
| Entities & Relationships | 5–8 min |
| Design Patterns | 3–5 min |
| Core Logic (1–2 main flows) | 5–10 min |
| Start coding | 25–35 min |
| Validations (as you code) | Ongoing |
| Discussion | 5 min |

**Rule**: Don't over-document. A one-page design outline is enough. The guideline doc you're creating here is for *practice* and *preparation*, not for writing in real time.

---

# Part VIII: Templates & Checklists

## 8.1 Clarification Assumptions Template

```markdown
## Clarification & Scope

| # | Question | Assumption |
|---|----------|------------|
| 1 | [Scope] | |
| 2 | [State/Lifecycle] | |
| 3 | [Input/Output] | |
| 4 | [Rules] | |
| 5 | [Edge cases] | |
| 6 | [Concurrency] | |
| 7 | [Extensibility] | |

Why this matters: [1 sentence]
```

## 8.2 Entity Template

```markdown
## Entity: [Name]

- **Attributes**: [list]
- **Methods**: [list]
- **Relationships**: [to other entities]
- **Invariants**: [what must hold]
```

## 8.3 Flow Template

```markdown
## Flow: [Name]

**Trigger**: [what starts it]
**Input**: [params]
**Output**: [return type]

**Steps**:
1. Validate [preconditions]
2. [Action 1]
3. [Action 2]
...
N. Return [output]

**Error cases**:
- [Condition] → [Exception/Handling]
```

## 8.4 Pre-Coding Checklist

- [ ] Clarification done; assumptions recorded
- [ ] Core entities identified
- [ ] Main flows written (at least 1–2)
- [ ] Key invariants listed
- [ ] Design patterns chosen
- [ ] Implementation order decided
- [ ] Edge cases noted

## 8.5 Pre-Submit Checklist (If Building Fully)

- [ ] All requirements traced to code
- [ ] Exceptions for invalid operations
- [ ] No hardcoded values (use config)
- [ ] SOLID principles applied
- [ ] Tests for main flows
- [ ] README or design notes updated

---

# Part IX: Interview Scripts & What to Say

## 9.1 Opening (After Problem Statement)

> "Thanks, that's clear. Before I start designing, let me clarify a few things to make sure I'm on the right track. [Ask 2–3 highest-impact questions]. If you don't have a preference, I'll assume [X]. Does that work?"

## 9.2 When Stating Assumptions

> "I'll assume [X] for now. We can change it if you'd like."

## 9.3 When Moving to Design

> "Given that, here's how I'm thinking about the design. I'll start with the core entities..."

## 9.4 When Choosing a Pattern

> "I'll use the Strategy pattern here because [reason]. This lets us add new [variants] without changing the core logic."

## 9.5 When Handling Edge Cases

> "For [edge case], I'll validate [condition] and throw [Exception]. Does that align with your expectations?"

## 9.6 When You're Stuck

> "I'm considering two approaches: [A] and [B]. [A] is simpler but [trade-off]. [B] is more extensible but [trade-off]. Which direction would you prefer?"

## 9.7 When Wrapping Up

> "To summarize: I've built [components]. The main flows are [list]. I've handled [key edge cases]. What would you like me to extend or change next?"

---

# Part X: Practice Strategies & Self-Assessment

## 10.1 The 20-Problem Drill

Pick 20 LLD problems (including those in this repo). For each:

1. **Read the requirement** (don't look at the design guide yet).
2. **Create your own clarification table** using the question banks.
3. **Write your own design guideline** using the 10-phase template.
4. **Compare** with the existing DESIGN_GUIDE.md.
5. **Reflect**: What did you miss? What did you get right?

**Goal**: After 20 problems, the question banks and template become automatic.

---

## 10.2 The "Blind Design" Exercise

1. Pick a problem you've never seen.
2. Set a timer: 10 min clarification, 15 min design (no code).
3. Produce: Assumptions table + Entity list + 1 main flow.
4. Self-grade: Did you ask good questions? Did you catch the key entities?

---

## 10.3 The Input/Output Drill

For each problem you practice:
- List every public method/API you'd expose.
- For each: Write input params, output type, and 2–3 error cases.
- Compare with the implemented code or design guide.

---

## 10.4 Self-Assessment Rubric

After each problem, rate yourself (1–5):

| Criterion | 1 (Weak) | 5 (Strong) |
|-----------|----------|------------|
| **Clarification** | Didn't ask questions; made silent assumptions | Asked 5+ targeted questions; recorded assumptions |
| **Entity design** | Unclear entities; god objects | Clear, single-responsibility entities |
| **Input/Output** | Vague or inconsistent APIs | Explicit, minimal, consistent |
| **Patterns** | Procedural; lots of if/else | Strategy/State/Observer where appropriate |
| **Edge cases** | Forgot validations | Explicit validation table |
| **Extensibility** | Hard to add features | New feature = new class |

---

## 10.5 Common Mistakes to Avoid

1. **Starting to code before clarifying** — Always clarify first.
2. **Asking about implementation too early** — "Should I use HashMap?" before "What are the entities?"
3. **One big class** — Split by responsibility.
4. **Implicit state** — Make turn, status, etc., explicit.
5. **Ignoring concurrency** — Ask: "Single-threaded or multi?" when shared state exists.
6. **Polymorphism missed** — When you have "if (type == A) ... else if (type == B)", consider polymorphism.
7. **No error handling** — Every operation can fail; define how.
8. **Over-engineering** — Don't add 10 patterns for a 1-hour problem. 2–3 well-chosen patterns suffice.

---

## 10.6 Resources in This Repo

Use these DESIGN_GUIDE.md files as references after you've attempted the problem yourself:

- **Battleship**: Spatial modeling, turn management, view abstraction, strategy patterns
- **Parking Lot**: Capability-based matching, observer for display
- **BookMyShow**: Concurrency, pessimistic locking, DDD/Hexagonal
- **Payment Gateway**: Polymorphic input, routing strategies
- **Rule Engine**: Interface segregation, single vs multi rules
- **Cache**: Eviction policies, storage vs policy separation
- **Message Queue**: Custom queue, pub-sub, retry with backoff
- **ATM**: State pattern, state transitions
- **Online Auction**: Winner selection algorithm, P&L calculation

---

# Appendix A: Quick Reference Card (Print This)

## Clarification Dimensions
1. Scope 2. State 3. Inputs/Outputs 4. Rules 5. Edge cases 6. Non-functional 7. Extensibility

## Input Framework
Actor → What they know → Provided vs derived → Minimal but complete

## Output Framework
Success shape → Failure mode (exception/result) → Consistent return type

## Design Guideline Phases
1. Clarify 2. Entities 3. Patterns 4. Core Logic 5. Package 6. Validations 7. Impl Order 8. Strong Hire 9. Quick Ref

## Golden Rule
Never design before clarifying. Never clarify without a checklist.

---

# Appendix B: Problem-Type → Key Questions Map

| Problem Type | Top 5 Questions to Ask |
|--------------|------------------------|
| Game | Grid division? Turn order? Win condition? View (fog of war)? Placement manual/random? |
| Booking | Concurrency? Locking? Hold period? Scope (search, payment)? |
| Payment | Payment methods? Routing strategy? Failure handling? Polymorphic input? |
| Parking | Match by type or capability? Slot selection algo? Payment model? |
| Cache | Eviction policy? Persistence? TTL? Concurrency? |
| Queue | Bounded/unbounded? Push/pull? Retry? Custom queue? |
| Rule Engine | Single vs multi rules? Output (all violations)? Extensible? |
| ATM/State | States? Transitions? Invalid action handling? |
| Auction | Lifecycle? Multiple bids? Winner selection? P&L? |

---

# Appendix C: Example — Full Clarification to Design Guide (Abbreviated)

## Problem: "Design a parking lot"

### Clarification (5 min)

| Question | Assumption |
|----------|------------|
| Scope? | Park, unpark, fee calculation only. No search, no display boards. |
| Vehicle types? | Car, Bike, Electric Car. Match by capabilities (CAR_SIZE, EV_CHARGER), not enum. |
| Slot selection? | Best-fit (minimize wasted features). Pluggable. |
| Payment? | Hourly. Pluggable strategy. |
| Concurrency? | Single-threaded for interview. |

### Entities (from nouns)
ParkingLot, Floor, Slot, Vehicle, Ticket, Receipt, CompatibilityEngine, SlotStrategy, PaymentStrategy

### One Flow: Park

1. Get vehicle type → required features
2. SlotStrategy.findSlot(vehicle) → first slot where slot.features ⊇ vehicle.required, minimize |slot.features - vehicle.required|
3. Assign vehicle to slot
4. Create Ticket(vehicle, slot, inTime)
5. Return ticket

### Validations
- Lot full → exception
- Invalid ticket on unpark → exception

**That's the core.** The rest (Package structure, Implementation order) follows from this.

---

# Part XI: Pattern Recognition — When to Use Which Design Pattern

## 11.1 Decision Tree: "Which Pattern Fits?"

```
Does behavior change based on STATE (e.g., different actions allowed in different modes)?
  → YES: State Pattern (ATM, Game status)

Do you have MULTIPLE ALGORITHMS for the same operation (slot selection, payment calculation, routing)?
  → YES: Strategy Pattern (Parking, Payment Gateway, Battleship targeting)

Do MULTIPLE OBSERVERS need to react to events (display boards, logging, notifications)?
  → YES: Observer Pattern (Cricbuzz, Parking display, Message Queue)

Does a request pass through a CHAIN of handlers (auth → log → parse → controller)?
  → YES: Chain of Responsibility (Route Handler, Middleware)

Do you need to build COMPLEX OBJECTS with many optional parts?
  → YES: Builder Pattern (Game setup, Configuration)

Do you have POLYMORPHIC INPUT (different shapes for same concept — UPI vs Card)?
  → YES: Interface + implementations (PaymentDetails)

Are you VALIDATING against multiple independent rules?
  → YES: Strategy/Plugin (Rule Engine)

Is there a REPEATABLE SKELETON with steps (setup → loop → cleanup)?
  → YES: Template Method (Game loop)

Do you need to ABSTRACT STORAGE (in-memory vs DB)?
  → YES: Repository Pattern (BookMyShow, E-Commerce)

Do you need a SINGLE ENTRY POINT that hides complexity?
  → YES: Facade (PaymentGateway, ParkingLotManager)
```

---

## 11.2 Requirement → Pattern Mapping

| Requirement Phrase | Likely Pattern | Example |
|--------------------|----------------|---------|
| "Different ways to..." | Strategy | "Different ways to find a slot" → SlotDeterminingStrategy |
| "Based on the current state..." | State | "Based on whether card is inserted" → ATMState |
| "Notify when..." | Observer | "Notify display when car leaves" → ParkingEventListener |
| "Validate against rules..." | Strategy/Plugin | "Validate expense rules" → SingleExpenseRule |
| "Route to different..." | Strategy | "Route to different banks" → PaymentRoutingRule |
| "Build with many options..." | Builder | "Build game with config" → GameBuilder |
| "Store/retrieve..." | Repository | "Store tickets" → TicketRepository |
| "Single point to..." | Facade | "Single point for parking operations" → ParkingLotManager |
| "Chain of..." | Chain of Responsibility | "Chain of middleware" → RouteHandler |
| "Different types of..." | Polymorphism | "Different payment methods" → PaymentDetails implementations |

---

## 11.3 "Smell" → Pattern Transformation

| Code Smell | Fix with Pattern |
|------------|------------------|
| Large if/else on type/enum | Strategy or Polymorphism |
| Large if/else on state | State Pattern |
| Method does too much | Extract to separate classes; Facade to orchestrate |
| Hard to add new X without changing existing code | Strategy, Plugin, or Polymorphism |
| Tight coupling between producer and consumer | Observer, Event-driven |
| Complex construction logic | Builder |
| Direct dependency on concrete storage | Repository interface |
| Repeated validation logic | Chain of Responsibility or Rule objects |

---

# Part XII: Detailed Worked Examples — From Requirements to Design

## 12.1 Example 1: Elevator System (Abridged)

**Requirement**: "Design an elevator system for a building with N floors."

### Clarification Table

| Question | Assumption |
|----------|------------|
| One elevator or multiple? | Multiple elevators. |
| How does elevator choose direction? | Same direction priority; then nearest request. |
| Capacity? | Limited. Overload = reject. |
| Button types? | Floor buttons inside; Up/Down on each floor. |
| Real-time display? | Display current floor. Observer for display. |

### Key Entities
Elevator, Floor, Request, ElevatorController, Direction (enum), RequestQueue

### Key Operations
- requestElevator(floor, direction) → assigns elevator
- pressFloor(elevatorId, floor) → adds destination
- move() → one floor per tick (simplified)

### Input/Output
- requestElevator(floor: int, direction: Direction) → elevatorId or null if all busy
- pressFloor(elevatorId: String, floor: int) → void (or exception if invalid)

---

## 12.2 Example 2: Library Management System

**Requirement**: "Design a library system for borrowing and returning books."

### Clarification Table

| Question | Assumption |
|----------|------------|
| Single copy or multiple copies per book? | Multiple copies (BookItem). Book = title; BookItem = physical copy. |
| Member types? | Single type for now. |
| Late fees? | Out of scope. |
| Reservation? | Out of scope. |
| Fine calculation? | Out of scope. |

### Key Entities
Library, Book (title, author), BookItem (barcode, book reference), Member, Loan (member, bookItem, dueDate)

### Key Operations
- borrow(memberId, barcode) → Loan or exception
- return(loanId or barcode) → void
- search(criteria) → List<Book> (optional)

### Input/Output
- borrow(memberId: String, bookItemBarcode: String) → Loan
  - Fail: Member has max loans, book not available, member not found
- return(bookItemBarcode: String) → void
  - Fail: Loan not found, already returned

---

## 12.3 Example 3: Vending Machine

**Requirement**: "Design a vending machine."

### Clarification Table

| Question | Assumption |
|----------|------------|
| Coin/note types? | Accept predefined denominations. |
| Change? | Return exact change if possible; else refund. |
| Item selection? | By code (A1, B2, etc.). |
| State? | Idle, Selecting, Payment, Dispensing. |
| Out of stock? | Show "sold out"; don't allow selection. |

### Key Entities
VendingMachine, Item (code, price, quantity), State (Idle, Selecting, Payment, Dispensing), Inventory

### State Transitions
- Idle + select(item) → Selecting (if item available)
- Selecting + insertCoin(amount) → accumulate; when >= price → Payment
- Payment + confirm → Dispensing → Idle
- Any state + cancel → refund, Idle

### Input/Output
- selectItem(code: String) → void (throws if sold out)
- insertCoin(amount: int) → void
- confirmPurchase() → Item + List<Coin> (change)
- cancel() → List<Coin> (refund)

---

# Part XIII: Requirement Ambiguity Catalog

When you see these phrases, they usually need clarification:

| Phrase | Ambiguous Because | Questions to Ask |
|--------|-------------------|------------------|
| "Design a X" | Scope unclear | What exactly should X do? What's out of scope? |
| "Users can..." | Who are users? How many? | Single/multi? Roles? |
| "Handle multiple..." | Concurrency? | Single-threaded or concurrent? Locking? |
| "Support different..." | How many? Extensible? | Fixed set or pluggable? |
| "Efficient" | What metric? | Time? Space? Scalability? |
| "Real-time" | Latency? Push vs pull? | What's acceptable delay? |
| "Large scale" | How large? | Orders of magnitude? |
| "Similar to X" | Which features of X? | Which parts of X should we replicate? |
| "Basic/crud" | How basic? | What operations exactly? |
| "With authentication" | Scope? | In scope or mock? |

---

# Part XIV: Input/Output Anti-Patterns and Fixes

## 14.1 Anti-Pattern: God Object Input

**Bad**: `process(data: Map<String, Object>)` — Caller passes everything in a map.

**Fix**: Typed input object or explicit parameters. `process(request: PaymentRequest)`.

## 14.2 Anti-Pattern: Implicit Output

**Bad**: Method has side effects (prints, updates global state) but returns void. Caller can't get result.

**Fix**: Return the result. Use Observer for side effects (logging, display).

## 14.3 Anti-Pattern: Exception for Control Flow

**Bad**: Using exceptions for "not found" when it's expected (e.g., cache miss).

**Fix**: Return `Optional<T>` or `Result<T, E>`. Reserve exceptions for unexpected failures.

## 14.4 Anti-Pattern: Inconsistent Return Types

**Bad**: Sometimes return object, sometimes null, sometimes empty list for "no result."

**Fix**: Pick one convention. Prefer `Optional<T>` or empty collection over null.

## 14.5 Anti-Pattern: Leaky Abstraction in Output

**Bad**: Returning JPA entity (with lazy loading, proxies) to caller.

**Fix**: Return DTO or domain object. Keep persistence details in infrastructure.

---

# Part XV: Domain-Specific Input/Output Examples

## 15.1 Game Systems

| Operation | Input | Output |
|-----------|-------|--------|
| placeShip | playerId, topLeft: Coordinate, size: int | void (exception on invalid) |
| fire | coordinate: Coordinate | FireResult(hit, shipDestroyed, gameOver) |
| getBoardView | playerId | BattlefieldView (string or matrix) |
| makeMove | playerId, position: int | GameResult(winner, draw, nextPlayer) |

## 15.2 Booking Systems

| Operation | Input | Output |
|-----------|-------|--------|
| getAvailableSeats | showId | List<SeatDto> |
| bookSeats | showId, seatIds, userId | Ticket |
| cancelBooking | ticketId | void (or RefundInfo) |

## 15.3 Payment Systems

| Operation | Input | Output |
|-----------|-------|--------|
| onboardClient | name, supportedMethods | clientId |
| capturePayment | clientId, amount, method, PaymentDetails | PaymentResult |
| refund | transactionId, amount | RefundResult |

## 15.4 Cache Systems

| Operation | Input | Output |
|-----------|-------|--------|
| get | key | Optional<Value> |
| put | key, value | void |
| put | key, value, ttlSeconds | void |
| evict | key | void |
| changePolicy | EvictionPolicy | void |

## 15.5 Queue/Messaging Systems

| Operation | Input | Output |
|-----------|-------|--------|
| createQueue | name, capacity? | void |
| publish | queueName, payload | void (or messageId) |
| subscribe | queueName, Subscriber | void |
| unsubscribe | queueName, subscriberId | void |

---

# Part XVI: Interview Time Management Script

## 60-Minute LLD Interview Breakdown

| Minute | Phase | Actions |
|--------|-------|---------|
| 0–2 | Listen | Take notes. Don't interrupt. |
| 2–5 | Restate + Clarify | "So we need X. I have a few questions..." Ask 3–5. |
| 5–8 | Assumptions | State assumptions. Get nod. |
| 8–15 | High-Level Design | Entities, relationships. Draw or list. |
| 15–20 | Core Flows | 1–2 main flows. Pseudocode or steps. |
| 20–25 | Patterns | "I'll use Strategy for X because..." |
| 25–50 | Code | Implement core. Prioritize: entities first, then main flow. |
| 50–55 | Edge Cases | Add validations, exceptions. |
| 55–60 | Summary | "Here's what I built. I'd extend by..." |

## 45-Minute LLD Interview (Shorter)

| Minute | Phase |
|--------|-------|
| 0–5 | Listen + Clarify (fewer questions) |
| 5–12 | Entities + 1 main flow |
| 12–40 | Code |
| 40–45 | Validations + summary |

---

# Part XVII: Self-Questioning Prompts (Ask Yourself While Designing)

1. **Entities**: "Have I captured all nouns? Is any entity doing too much?"
2. **Relationships**: "Is this 1-to-many or many-to-many? Does the child need the parent's ID?"
3. **State**: "What are the states? What triggers transitions? Is state explicit or implicit?"
4. **Input**: "What does the caller have? What can I derive? Am I asking for too much?"
5. **Output**: "What does the caller need? How do I communicate failure?"
6. **Invariants**: "What must never happen? Have I added validation?"
7. **Extensibility**: "If we add X tomorrow, do I need to change existing code?"
8. **Concurrency**: "What if two threads call this? Is it a problem?"
9. **Testing**: "Can I unit test this? What do I need to mock?"
10. **Naming**: "Will another developer understand this in 6 months?"

---

# Part XVIII: Mapping Your Repo's Problems to This Guide

| Problem | Primary Clarification Focus | Key Patterns | Input/Output Complexity |
|---------|----------------------------|--------------|--------------------------|
| Battleship | Grid division, turn, view, ship model | Strategy, Value Object, Builder | Medium (Coordinate, FireResult) |
| Parking Lot | Capability vs type, slot selection | Strategy, Observer, Facade | Low |
| BookMyShow | Concurrency, locking | Hexagonal, Repository | Medium (BookingRequest) |
| Payment Gateway | Payment methods, routing | Strategy, Polymorphism, Facade | High (PaymentDetails variants) |
| Rule Engine | Single vs multi rules | Strategy, Interface Segregation | Low |
| Cache | Eviction, persistence | Strategy, Repository | Low |
| Message Queue | Push vs pull, retry | Observer, Strategy, Custom DS | Medium |
| ATM | State transitions | State | Low |
| Online Auction | Winner selection, P&L | Strategy, Repository | Medium |
| Tic-Tac-Toe | Winning rules, undo | Strategy, State, Factory | Low |
| E-Commerce | Filters, cart | Specification, Strategy | Medium |
| Route Handler | Middleware order | Chain of Responsibility | Low |
| Cricbuzz | Fan-out, filtering | Observer | Low |
| Movie CMS | Cache hierarchy, filters | Composite, Chain, Strategy | Medium |
| Banking System | Timestamp ordering, cashback vs other ops, merge + replay | Repository, Dependency Inversion, domain ledger | Medium (Optional-heavy API, `timeAt` replay) |

Use this table to know where to focus your clarification and design for each problem type.

---

# Part XIX: Checklist — "Am I Ready for an LLD Interview?"

- [ ] I have read and internalized the 7 dimensions of clarification (Part II)
- [ ] I can list 5+ questions from the universal bank for any problem (Part IV)
- [ ] I know the input framework: Actor → Provided vs Derived → Minimal (Part III)
- [ ] I know the output framework: Success shape → Failure mode → Consistency (Part III)
- [ ] I can extract entities from nouns and flows from verbs (Part VI)
- [ ] I know the 10-phase design guideline template (Part VII)
- [ ] I have practiced creating my own design guide for at least 5 problems
- [ ] I can explain when to use Strategy, State, Observer from requirement phrases (Part XI)
- [ ] I have practiced the "blind design" exercise (Part X)
- [ ] I have interview scripts ready (Part IX)
- [ ] I know the time management for 45- and 60-minute interviews (Part XVI)

---

# Part XX: Final Thoughts — Building the Insight

**Insight is pattern recognition.** The more problems you solve *with a process*, the more you recognize:
- "This sounds like a state machine" → State pattern
- "Multiple algorithms for same thing" → Strategy
- "Different input shapes" → Polymorphism
- "Concurrent access to shared resource" → Ask about locking
- "Rules that can be added" → Plugin/Strategy

**Your goal**: Reach a point where, upon hearing a problem, you *instantly* think of 3–5 clarification questions and 2–3 likely patterns. That comes from deliberate practice using this guide.

**How to use this document**:
1. **Before practice**: Skim Parts II, III, IV, V.
2. **During practice**: Use Part VII template; fill using Parts IV, V, VI.
3. **After practice**: Self-assess using Part X; compare with DESIGN_GUIDE.md.
4. **Before interview**: Review Parts IX, XVI, Appendix A.
5. **Ongoing**: Add your own questions to the banks when you encounter new ambiguities.

---

*End of LLD Master Guide. Use this document as your reference. Revisit Sections II, III, IV, and V before every practice session or interview.*
