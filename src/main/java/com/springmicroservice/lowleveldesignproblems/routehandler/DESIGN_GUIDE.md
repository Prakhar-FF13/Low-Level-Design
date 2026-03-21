# Route Handler / Middleware LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the Route Handler with middleware support in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| Route matching? | Map URL → handler chain; first match wins. |
| Middleware order? | Different per route; chain is configurable. |
| Before vs after? | Handlers can run logic before and/or after calling next. |
| Most specific match? | Out of scope — first match from list. |
| Request/Response shape? | Request (url, method, body, headers, pathParams, queryParams); Response DTO. |

**Why this matters**: Chain of Responsibility is the natural fit for middleware pipelines.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
RouteHandler
├── handlers (Map<String, IHandler>)  // URL → start of chain
└── serveRequest(Request) → Response

IHandler (interface)
└── handle(Request) → Response

AuthHandler (implements IHandler)
├── next (IHandler)
└── handle: auth check → if OK, next.handle(request); else error response

LogBeforeHandler (implements IHandler)
├── next (IHandler)
└── handle: log before → next.handle(request)

LogAfterHandler (implements IHandler)
├── next (IHandler)
└── handle: response = next.handle(request); log after; return response

DoNothingController (implements IHandler)
├── next (IHandler, often null)
└── handle: actual controller logic; return Response

Request
├── url, method, body
├── headers, pathParameters, queryParameters
└── (DTO)

Response
└── (DTO)
```

**Relationships**:
- Each handler holds `next` (Chain of Responsibility)
- RouteHandler maps URL to the first handler in the chain
- Handler calls `next.handle(request)` to pass along

---

## Phase 3: Choose Design Pattern: Chain of Responsibility

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|------------------------|
| **Chain of Responsibility** | IHandler chain | Each handler is decoupled; order configurable per route. |
| **Factory** | MostCommonHandlersList | Builds common chains (Auth → LogBefore → LogAfter → Controller). |
| **Strategy** | Different handler implementations | Auth, LogBefore, LogAfter, Controller — each does one job. |

---

## Phase 4: Core Logic — Matches Code

### 4.1 Request Flow

```
RouteHandler.serveRequest(request)
  → handler = handlers.get(request.url)
  → return handler.handle(request)

AuthHandler.handle(request)
  → if (!authenticated) return errorResponse
  → return next.handle(request)

LogBeforeHandler.handle(request)
  → log("Before: " + request)
  → return next.handle(request)

LogAfterHandler.handle(request)
  → response = next.handle(request)
  → log("After: " + response)
  → return response

DoNothingController.handle(request)
  → process request
  → return Response
```

### 4.2 Building Chains

- `MostCommonHandlersList.getHandlers()` creates: Auth → LogBefore → LogAfter → DoNothingController
- Each handler receives `next` in constructor
- RouteHandler stores Map<url, IHandler> where value is the chain head

---

## Phase 5: Package Structure (Matches Code)

```
routehandler/
├── domain/
│   └── dto/
│       ├── Request.java
│       └── Response.java
├── handlers/
│   ├── IHandler.java
│   ├── AuthHandler.java
│   ├── LogBeforeHandler.java
│   └── LogAfterHandler.java
├── controller/
│   └── DoNothingController.java
├── RouteHandler.java
├── factories/
│   └── MostCommonHandlersList.java
└── README.md
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Route not found | Return 404 or default handler |
| Auth failure | AuthHandler returns error response; next not called |
| Null next | Controller typically has null next (end of chain) |

---

## Phase 7: Implementation Order (Recommended)

1. **DTOs** — Request, Response
2. **IHandler** — interface with handle(Request) → Response
3. **DoNothingController** — simplest handler (no next)
4. **LogBeforeHandler**, **LogAfterHandler** — wrap next
5. **AuthHandler** — conditional pass-through
6. **MostCommonHandlersList** — factory to build chain
7. **RouteHandler** — Map<url, IHandler>, serveRequest

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Decoupling** | Each handler knows only its `next`; no global state |
| **Composability** | Order of middleware configurable per route |
| **Single Responsibility** | Auth does auth; Log does log; Controller does business |
| **Extensibility** | New middleware = new IHandler impl; add to chain |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| Match request to controller | RouteHandler + Map<url, IHandler> |
| Middleware before/after | LogBeforeHandler, LogAfterHandler (call next before/after) |
| Different order per route | Build different chains per URL |
| Auth | AuthHandler (guards next) |
| Chain building | MostCommonHandlersList (factory) |

---

## Run

```bash
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.routehandler.*"
```
