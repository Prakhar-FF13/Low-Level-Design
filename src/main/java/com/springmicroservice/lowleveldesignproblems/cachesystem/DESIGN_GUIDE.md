# Cache System LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the Cache System in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| Eviction policies? | LRU, LFU, FIFO — pluggable, switchable at runtime. |
| Persistence? | Optional — WriteThrough (sync) or WriteBack (async queue). |
| Concurrency? | ReadWriteLock; read-dominant workloads. |
| TTL? | Optional per entry; lazy eviction on get + Reaper task for proactive cleanup. |
| Capacity? | Fixed; evict when full. |

**Why this matters**: Interviewers care about eviction policy design, separation of Storage vs Eviction, and concurrency.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
Cache<K,V> (interface)
├── put(K, V)
├── get(K) → Optional<V>
├── changeEvictionPolicy(EvictionPolicy<K>)
├── size(), clear()
└── (CacheImpl orchestrates Storage, EvictionPolicy, PersistenceStrategy)

CacheEntry<V>
├── value
├── expiryTime
└── isExpired()

CacheEntity (JPA / persistence)
├── id (key)
├── value (serialized)
└── expiryTime

EvictionPolicy<K> (interface)
├── keyAccessed(K)
├── evictKey() → K
├── removeKey(K)
└── clear(), getPolicyType()

Storage<K,V> (interface)
├── add(K, V)
├── get(K) → V
└── remove(K)

PersistenceStrategy (interface)
├── save(CacheEntity)
├── delete(String key)
└── shutdown()
```

**Key insight**: Storage holds heavy data; EvictionPolicy holds only keys and ordering. They are completely decoupled.

---

## Phase 3: Choose Design Patterns

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|------------------------|
| **Strategy** | EvictionPolicy (LRU, LFU, FIFO) | Pluggable; hot-swap at runtime. |
| **Strategy** | PersistenceStrategy (WriteThrough, WriteBack) | Sync vs async persistence. |
| **Repository** | CacheRepository (JPA) | Abstraction over H2/DB. |
| **Factory** | CacheFactory | Creates CacheImpl with correct wiring. |
| **Separation of concerns** | Storage vs EvictionPolicy | Storage = data holder; Policy = traffic director (keys only). |

---

## Phase 4: Core Algorithms — Matches Code

### 4.1 LRU Eviction (O(1))

- Doubly linked list + HashMap<K, Node<K>>
- keyAccessed: move node to head (MRU)
- evictKey: remove tail (LRU)

### 4.2 LFU Eviction (O(1))

- HashMap<K, Node> + HashMap<frequency, DoublyLinkedList>
- keyAccessed: bump frequency; move node to next freq list
- evictKey: remove from minFrequency list (head)

### 4.3 FIFO Eviction (O(1))

- LinkedHashSet<K> preserves insertion order
- evictKey: remove first inserted

### 4.4 put() Flow (Cache Full)

1. lock.writeLock()
2. if storage.isFull(): victimKey = evictionPolicy.evictKey(); storage.remove(victimKey); persistenceStrategy.delete(victimKey)
3. storage.add(key, CacheEntry); evictionPolicy.keyAccessed(key); persistenceStrategy.save(entity)
4. unlock

### 4.5 get() Flow

1. lock.writeLock()
2. entry = storage.get(key); if entry.isExpired() → remove and return empty
3. evictionPolicy.keyAccessed(key)
4. unlock; return value

### 4.6 TTL Reaper

- ScheduledExecutorService runs every 5s
- Iterate keys; if CacheEntry.isExpired() → remove from storage and policy

---

## Phase 5: Package Structure (Matches Code)

```
cachesystem/
├── api/
│   └── Cache.java (interface)
├── application/
│   └── CacheImpl.java
├── domain/
│   ├── model/
│   │   ├── CacheEntry.java
│   │   └── CacheEntity.java
│   ├── policy/
│   │   ├── EvictionPolicy.java
│   │   ├── LRUEvictionPolicy.java
│   │   ├── LFUEvictionPolicy.java
│   │   └── FIFOEvictionPolicy.java
│   └── exception/
│       ├── NotFoundException.java
│       └── CacheFullException.java
├── repository/
│   ├── storage/
│   │   ├── Storage.java
│   │   └── InMemoryStorage.java
│   └── persistence/
│       ├── PersistenceStrategy.java
│       ├── WriteThroughStrategy.java
│       └── WriteBackStrategy.java
├── CacheFactory.java
└── README.md
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| get() expired | Remove and return Optional.empty() |
| put() full | Evict before insert |
| changeEvictionPolicy | Feed all keys to new policy; clear old |
| WriteBack shutdown | Drain queue before shutdown |
| Concurrency | ReadWriteLock (write for put/get mutations) |

---

## Phase 7: Implementation Order (Recommended)

1. **CacheEntry** — value, expiryTime, isExpired()
2. **EvictionPolicy interface** — LRU (DLL+Map), LFU (freq map), FIFO (LinkedHashSet)
3. **Storage interface** — InMemoryStorage (ConcurrentHashMap)
4. **CacheImpl** — put, get, eviction, lock
5. **PersistenceStrategy** — WriteThrough, WriteBack
6. **CacheFactory** — wire components
7. **TTL Reaper** (optional)
8. **changeEvictionPolicy** (optional)

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Storage vs Eviction separation** | Storage holds data; Policy holds keys only — no logic inside Storage |
| **O(1) eviction** | LRU: DLL+Map; LFU: freq map + DLL; FIFO: LinkedHashSet |
| **Concurrency** | ReadWriteLock; fine-grained |
| **Extensibility** | New policy = implement EvictionPolicy; zero changes to CacheImpl |
| **Persistence** | WriteThrough vs WriteBack via strategy; CacheImpl doesn't care |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| LRU eviction | LRUEvictionPolicy (DLL + HashMap) |
| LFU eviction | LFUEvictionPolicy (freq map + DLL) |
| FIFO eviction | FIFOEvictionPolicy (LinkedHashSet) |
| In-memory storage | InMemoryStorage (ConcurrentHashMap) |
| Sync persistence | WriteThroughStrategy |
| Async persistence | WriteBackStrategy (BlockingQueue + worker thread) |
| TTL | CacheEntry.expiryTime, Reaper task |
| Concurrency | ReentrantReadWriteLock in CacheImpl |

---

## Run

```bash
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.cachesystem.*"
```
