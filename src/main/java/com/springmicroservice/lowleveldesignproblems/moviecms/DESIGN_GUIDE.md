# Movie Content Management System LLD — Design Guide

This guide walks you through designing the Movie CMS in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| What fields define a Movie? | id, title, genre, year, language, director, etc. |
| Is cache user-specific for L1? | Yes — L1 is 5 items per user; L2 is 20 items global. |
| Cache eviction policy? | LRU (Least Recently Used) — most common for interview LLD. |
| Search filters — which attributes? | At minimum: genre, year, language; easily extensible via `Filter` interface. |
| Single vs multi-filter: AND or OR? | Default: AND (all filters must match). |
| Cache hit/miss: per request or aggregate? | Both — per-request tracking + aggregate analytics. |
| Who can clear cache? | Admin/system; optionally per-user for L1. |

**Why this matters**: Interviewers like seeing you nail requirements before diving into code.

---

## Phase 2: Identify Core Entities & Relationships

### Models (Domain Entities)

```
Movie
├── id
├── title
├── genre
├── year
├── language
├── director
└── (extensible metadata)

User
├── id
├── name
└── (optional: preferences, history)

CacheEntry (for L1/L2 storage)
├── key (e.g., userId + queryHash or global queryHash)
├── value (List<Movie>)
├── timestamp (for LRU eviction)
└── size / hitCount (for analytics)
```

### Enums (Types)

```
Genre        — ACTION, COMEDY, DRAMA, HORROR, SCI_FI, etc.
CacheLevel   — L1, L2, PRIMARY
CacheEvent   — HIT, MISS (for analytics)
```

### Exceptions

```
MovieNotFoundException
UserNotFoundException
CacheException
InvalidFilterException
```

### Filter Interface (Key for Extensibility)

```
Filter (interface)
├── matches(Movie movie) : boolean
└── (optional) getField() : String — for debugging/analytics

Concrete Filters:
├── GenreFilter implements Filter
├── YearFilter implements Filter
├── LanguageFilter implements Filter
├── DirectorFilter implements Filter
└── CompositeFilter implements Filter — AND/OR of multiple filters
```

**Relationships**:
- `User` 1 — 1 L1 cache (5 items)
- L2 cache: 1 global (20 items)
- Primary store: 1 global (unlimited)
- Search: applies `Filter`(s) over `Movie` set

---

## Phase 3: Choose Design Patterns

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|------------------------|
| **Strategy / Filter interface** | Search criteria | Single-filter vs multi-filter; easy to add new filter types without touching search logic. |
| **Composite** | Multi-filter search | Combine filters (AND/OR) in a tree structure. |
| **Repository** | Data access (movies, users) | In-memory for LLD; easily swapped with DB later. |
| **Service Layer** | Business logic | Clear separation: controller calls services, services orchestrate. |
| **Chain of Responsibility** | Cache hierarchy | L1 → L2 → Primary; each level tries, delegates on miss. |
| **Decorator / Wrapper** (optional) | Cache with analytics | Wrap cache to track hits/misses. |
| **Singleton** (or DI) | L2 / Primary store | Single global instance. |

---

## Phase 4: Core Algorithms

### 4.1 Multi-Level Cache Lookup

```
1. Build cache key from userId + searchParams (for L1) or searchParams only (for L2)
2. Check L1 (if user-specific) → HIT ? return; MISS → continue
3. Check L2 → HIT ? return (and optionally backfill L1); MISS → continue
4. Query Primary Store → get result
5. On miss: update L2, then L1 (respect capacity + eviction)
6. Record HIT or MISS in analytics
```

### 4.2 Cache Eviction (LRU)

```
When adding and capacity exceeded:
1. Find least recently used entry (by timestamp or access order)
2. Remove it
3. Insert new entry
```

### 4.3 Search with Filters

**Single Filter:**
```
results = movies.stream().filter(f -> f.matches(movie)).toList()
```

**Multi Filter (AND):**
```
results = movies.stream()
  .filter(movie -> filters.stream().allMatch(f -> f.matches(movie)))
  .toList()
```

**Multi Filter with CompositeFilter:**
```
CompositeFilter composite = new CompositeFilter(AND, genreFilter, yearFilter);
results = movies.stream().filter(composite::matches).toList()
```

### 4.4 Cache Analytics

```
- Per request: record HIT or MISS for the cache level used
- Aggregate: Map<CacheLevel, { hitCount, missCount }>
- Hit rate = hitCount / (hitCount + missCount)
```

---

## Phase 5: Package Structure (Clean Architecture)

```
moviecms/
├── models/
│   ├── Movie.java
│   ├── User.java
│   ├── CacheEntry.java
│   └── enums/
│       ├── Genre.java
│       ├── CacheLevel.java
│       └── CacheEvent.java
├── filters/
│   ├── Filter.java              # interface
│   ├── GenreFilter.java
│   ├── YearFilter.java
│   ├── LanguageFilter.java
│   ├── DirectorFilter.java
│   └── CompositeFilter.java     # AND/OR of filters
├── exceptions/
│   ├── MovieNotFoundException.java
│   ├── UserNotFoundException.java
│   ├── CacheException.java
│   └── InvalidFilterException.java
├── repositories/
│   ├── MovieRepository.java
│   ├── UserRepository.java
│   └── impl/
│       ├── InMemoryMovieRepository.java
│       └── InMemoryUserRepository.java
├── cache/
│   ├── Cache.java               # interface (get, put, clear)
│   ├── CacheLevel.java          # or use enum
│   ├── L1Cache.java             # 5 items per user
│   ├── L2Cache.java             # 20 items global
│   ├── PrimaryStore.java        # unlimited
│   ├── MultiLevelCache.java     # Chain: L1 → L2 → Primary
│   ├── CacheAnalytics.java      # Hit vs Miss tracking
│   └── CacheKeyBuilder.java     # userId + queryHash
├── services/
│   ├── MovieService.java        # register movie, search
│   ├── UserService.java         # register user
│   └── CacheService.java        # cache ops, analytics, maintenance
├── orchestrator/
│   └── MovieCMSOrchestrator.java   # or MovieCMSController
├── Main.java                    # CLI demo
└── README.md
```

**Alternative naming**: You can use `MovieCMSController` instead of `MovieCMSOrchestrator` if it better fits your mental model. The orchestrator coordinates `MovieService`, `UserService`, and `CacheService` for high-level operations like "search movie" (which touches cache and movie store).

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Duplicate movie | Same title + year? Reject or allow — clarify. |
| Duplicate user | Same name/email? Reject. |
| Empty search | No filters → return all movies (or paginate). |
| No results | Return empty list; do not throw. |
| Invalid filter value | e.g., year < 1900 or > 2100 → `InvalidFilterException`. |
| Cache clear | L1: per-user; L2: global; Primary: typically not cleared. |
| User not found on search | L1 is user-specific; invalid userId → handle gracefully. |

---

## Phase 7: Implementation Order (Recommended)

1. **Models** — `Movie`, `User`, `CacheEntry`, enums (`Genre`, `CacheLevel`, `CacheEvent`)
2. **Exceptions** — Custom exceptions
3. **Filter interface + implementations** — `Filter`, `GenreFilter`, `YearFilter`, `CompositeFilter`
4. **Repositories** — `MovieRepository`, `UserRepository` + in-memory impl
5. **Cache layer** — `Cache` interface, `L1Cache`, `L2Cache`, `PrimaryStore`, `MultiLevelCache`
6. **Cache analytics** — Hit/miss tracking
7. **Services** — `MovieService`, `UserService`, `CacheService`
8. **Orchestrator** — `MovieCMSOrchestrator` / `MovieCMSController`
9. **Main / CLI** — Wire everything, demo flows

---

## Phase 8: Requirement → Component Mapping

| Requirement | Primary Component |
|-------------|-------------------|
| Movie Registration | `MovieService`, `MovieRepository` |
| User Registration | `UserService`, `UserRepository` |
| Single-filter search | `MovieService` + `Filter` implementations |
| Multi-filter search | `MovieService` + `CompositeFilter` |
| L1 Cache (5 per user) | `L1Cache` |
| L2 Cache (20 global) | `L2Cache` |
| Primary Store | `PrimaryStore` / `MovieRepository` |
| Cache analytics (Hit vs Miss) | `CacheAnalytics` (in `CacheService` or wrapper) |
| Cache maintenance (clear) | `CacheService` |
| Error handling | `exceptions/` package |

---

## Phase 9: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Clarity** | Name classes/methods clearly; `Filter`, `Cache`, `MovieCMSOrchestrator` |
| **SOLID** | Single responsibility; `Filter` interface for Open/Closed; depend on abstractions |
| **Testability** | Services take repos and cache via constructor; easy to mock |
| **Extensibility** | New `Filter` types without touching search logic; new cache levels possible |
| **Requirements traceability** | Each requirement maps to a class/method |
| **Cache design** | Clear L1→L2→Primary chain; eviction policy (LRU) stated and implemented |
| **Communication** | Explain: "I used Filter interface because we have single and multi-filter; CompositeFilter handles AND logic" |

---

## Phase 10: Quick Reference — Your Proposed Structure (Validated)

| Layer | Components | Notes |
|-------|------------|------|
| **Models** | Movie, User, CacheEntry, Filter interface | Filter in `filters/` package for clarity |
| **Enums** | Genre, CacheLevel, CacheEvent | Types for extensibility |
| **Exceptions** | MovieNotFound, UserNotFound, CacheException, InvalidFilter | Centralized error handling |
| **Services** | MovieService, UserService, CacheService | Core business logic |
| **Controller / Orchestrator** | MovieCMSOrchestrator | Coordinates all services |

**Additions to your list:**
- **Repositories** — Data access abstraction (aligns with Online Auction)
- **Cache package** — L1, L2, Primary, MultiLevelCache, Analytics
- **Filters package** — `Filter` interface + implementations + `CompositeFilter`
- **Orchestrator** — Yes; this is your entry point for high-level flows like "search"

---

## Next Steps

1. Implement models + enums + exceptions (25 min)
2. Filter interface + implementations (25 min)
3. Repositories (15 min)
4. Cache layer + analytics (45 min)
5. Services (30 min)
6. Orchestrator + CLI (25 min)

Total: ~2.5 hours for a solid, interview-ready LLD.
