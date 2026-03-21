# Cricbuzz Score Update LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the real-time cricket score update system using the Observer pattern in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| Push vs Pull? | Push — Producer pushes Match/ScoreUpdateEvent to Consumers. |
| Single match or multiple? | Support multiple matches; topic-based subscription (matchId). |
| Conditional subscriptions? | EventFilter — consumer can filter by matchId, EventType (BOUNDARY, WICKET, etc.). |
| Data source? | ScoreService (e.g., ICCAPI) — external; Producer polls and broadcasts. |
| Over-fetching? | Use lightweight ScoreUpdateEvent DTO instead of full Match when possible. |

**Why this matters**: Extreme decoupling — core engine doesn't care who consumes; Observer/Publisher-Subscriber fits.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
Match
├── matchId
├── innings1, innings2 (Innings)
├── teamA, teamB (Team)
└── (root aggregate)

Innings
├── id
├── runs, wickets, oversRemaining
├── active
└── (match state)

Team
├── id, name
└── players (List<Player>)

ScoreUpdateEvent (DTO)
├── matchId
├── runsAdded, wicketsTaken, oversRemaining
├── eventType (NORMAL_DELIVERY, BOUNDARY, WICKET, INNINGS_BREAK, MATCH_END)
└── (lightweight for broadcast)

ScoreProducers (interface)
├── subscribe(Subscription)
├── unsubscribe(Subscription)
└── updateScore() or broadcastUpdate(ScoreUpdateEvent)

ICCScoreProducer (implements ScoreProducers)
├── match
├── subscriptions (List<Subscription>)
├── scoreService (ScoreService)
└── Polls ScoreService → mutates Match → broadcasts to subscribers

ScoreConsumers (interface)
└── updateScore(Match) or onScoreUpdate(ScoreUpdateEvent)

Subscription
├── consumer (ScoreConsumers)
├── filterCondition (EventFilter)
└── isInterestedIn(ScoreUpdateEvent) → boolean

EventFilter (interface)
└── test(ScoreUpdateEvent) → boolean

ScoreService (interface)
├── getRuns(), getWickets(), getOversRemaining()
└── (ICCAPI implements — external data source)
```

**Relationships**:
- Producer holds List<Subscription>; each Subscription wraps Consumer + EventFilter
- On update: for each Subscription, if isInterestedIn(event) → consumer.onScoreUpdate(event)

---

## Phase 3: Choose Design Pattern: Observer

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|------------------------|
| **Observer (Pub/Sub)** | ScoreProducers, ScoreConsumers | Decoupling; Producer doesn't know consumers. |
| **Topic-based subscription** | Subscription + EventFilter | Consumers subscribe to specific match/event type. |
| **Event DTO** | ScoreUpdateEvent | Lightweight broadcast; avoids over-fetching Match. |
| **Dependency Inversion** | ScoreService interface | Producer depends on abstraction; ICCAPI is adapter. |

---

## Phase 4: Core Logic — Matches Code

### 4.1 Update Flow (ICCScoreProducer)

```
updateScore() or updateScoreForMatch(matchId):
  1. Fetch from scoreService: runs, wickets, overs
  2. Mutate match state (innings1/innings2)
  3. Generate ScoreUpdateEvent (or pass Match)
  4. for (Subscription sub : subscriptions)
       if (sub.isInterestedIn(event))
         sub.getConsumer().onScoreUpdate(event)
```

### 4.2 Subscription Filtering

- EventFilter.test(event): e.g. matchId == 5 && eventType == WICKET
- Subscription.isInterestedIn(event): filterCondition == null || filterCondition.test(event)
- Enables: "Only Match 5", "Only wickets", "Match 5 AND wickets"

### 4.3 Topic-Based (Alternative)

- Map<Long, List<ScoreConsumers>> topicSubscribers by matchId
- subscribe(matchId, consumer): topicSubscribers.computeIfAbsent(matchId, ...).add(consumer)
- updateScoreForMatch(matchId, match): notify only topicSubscribers.get(matchId)

---

## Phase 5: Package Structure (Matches Code)

```
cricbuzz/
├── api/
│   └── ICCAPI.java (ScoreService impl)
├── domain/
│   ├── models/
│   │   ├── Match.java
│   │   ├── Innings.java
│   │   ├── Team.java
│   │   ├── Players.java
│   │   └── ScoreUpdateEvent.java
│   ├── producers/
│   │   ├── ScoreProducers.java
│   │   └── ICCScoreProducer.java
│   └── consumers/
│       ├── ScoreConsumers.java
│       ├── ICCUIConsumer.java
│       ├── Subscription.java
│       └── EventFilter.java
└── README.md
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Null subscriptions | Initialize to empty list |
| Active innings | Update innings1 vs innings2 based on match state |
| Filter null | Subscription with null filter → receives all events |

---

## Phase 7: Implementation Order (Recommended)

1. **Models** — Match, Innings, Team, Player
2. **ScoreUpdateEvent** — DTO with matchId, eventType, deltas
3. **ScoreService** — interface; ICCAPI impl
4. **ScoreConsumers** — interface
5. **EventFilter** — interface
6. **Subscription** — consumer + filter; isInterestedIn
7. **ScoreProducers** — subscribe, unsubscribe
8. **ICCScoreProducer** — poll, mutate, broadcast
9. **ICCUIConsumer** — concrete consumer

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Decoupling** | Producer has zero knowledge of consumer types |
| **Conditional subscriptions** | EventFilter + Subscription; consumer defines own rules |
| **Lightweight events** | ScoreUpdateEvent vs full Match |
| **Extensibility** | New consumer = implement ScoreConsumers; new filter = EventFilter |
| **Inversion of Control** | Producer pushes; consumers are passive |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| Real-time distribution | ICCScoreProducer, broadcast loop |
| Decoupling | ScoreProducers/ScoreConsumers interfaces |
| Topic filtering | Subscription + EventFilter |
| External data | ScoreService, ICCAPI |
| Lightweight payload | ScoreUpdateEvent |

---

## Run

```bash
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.cricbuzz.*"
```
