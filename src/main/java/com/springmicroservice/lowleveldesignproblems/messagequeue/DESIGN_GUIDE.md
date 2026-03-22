# In-Memory Message Queue LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the In-Memory Messaging Queue (Kafka/Redis Pub-Sub style) in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| Custom Queue — what exactly? | Build your own bounded/unbounded queue (array/LL + pointer) — no `java.util.Queue`. |
| Queue independence? | Each named queue (e.g., EmailQueue) has its own message buffer; no cross-queue visibility. |
| Message format? | `Map<String, Object>` for JSON-like flexibility; serialize/deserialize as needed. |
| Subscriber model? | Push via callback — publisher notifies subscriber; no blocking poll. |
| Batch consumption? | Subscriber receives List of messages (configurable batch size) in callback. |
| Failure handling? | On callback exception: retry with backoff; optionally move to DLQ (dead-letter queue). |
| Retry backoff? | Exponential: 1s → 2s → 4s → cap at maxRetries. |
| Thread safety? | Publisher + multiple subscribers; use `synchronized` or `ReentrantLock` on queue; concurrent subscriber callbacks. |
| Configurable delay? | Scheduler/executor with configurable poll interval before delivering next batch. |

**Why this matters**: Interviewers care about **decoupled pub-sub**, **custom queue implementation**, **retry with backoff**, and **thread safety**.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

### Domain Models

```
Message
├── id (UUID)
├── payload (Map<String, Object>)
├── timestamp
└── metadata (optional)

Queue (interface / custom implementation)
├── name (String) — e.g., "EmailQueue"
├── capacity (optional, bounded)
├── enqueue(Message) → void
├── dequeue() → Message (or batch)
├── peek() → Message
├── size() → int
└── isEmpty() → boolean

Publisher
├── publish(queueName, Message)
└── (delegates to QueueManager to get queue and enqueue)

Subscriber
├── id (String)
├── callback (MessageHandler / Consumer<List<Message>>)
├── batchSize (int)
├── onMessage(List<Message> messages) — invoked by queue/dispatcher
└── unsubscribe() — remove from queue's subscriber list

MessageHandler (functional interface)
└── void handle(List<Message> messages) throws Exception

QueueManager (or MessageBroker)
├── createQueue(name)
├── getQueue(name)
├── registerSubscriber(queueName, Subscriber)
├── removeSubscriber(queueName, subscriberId)
└── publish(queueName, Message)

Dispatcher / Worker
├── polls queue at interval (or on publish)
├── delivers batch to each subscriber (callback)
├── handles retry on failure
└── runs in background thread(s)

RetryPolicy
├── maxRetries
├── backoffStrategy (constant, linear, exponential)
└── executeWithRetry(Callable)
```

### Key Relationships

- `QueueManager` 1 — * `Queue` (named queues)
- `Queue` 1 — * `Subscriber` (multiple subscribers per queue)
- `Publisher` → `QueueManager.publish()` → `Queue.enqueue()`
- `Dispatcher` → `Queue.dequeue(batchSize)` → `Subscriber.onMessage(batch)`
- **Retry**: `Dispatcher` wraps callback in `RetryPolicy.executeWithRetry()`

---

## Phase 3: Choose Design Patterns

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|------------------------|
| **Observer / Pub-Sub** | Subscriber registers callback; Publisher pushes; subscribers notified | Core LLD — decoupled publisher and subscriber. |
| **Strategy** | `BackoffStrategy` (constant, linear, exponential) | Pluggable retry behavior; bonus feature. |
| **Executor / Scheduler** | `ScheduledExecutorService` for periodic poll | Configurable delay between pulls; async delivery. |
| **Facade** | `QueueManager` / `MessageBroker` | Single entry: create queue, publish, register subscriber. |
| **Custom Data Structure** | Queue (array-based circular buffer or linked list) | No `java.util.Queue` — demonstrates DS understanding. |
| **Thread Safety** | `ReentrantLock` or `synchronized` on queue operations | Concurrent pub-sub; bonus requirement. |

---

## Phase 4: Core Logic — Matches Code

### 4.1 Custom Queue Implementation

**Option A: Circular Buffer (bounded)**

```
Internal: Object[] buffer, int head, int tail, int count
enqueue(msg): if full → block or reject; buffer[tail] = msg; tail = (tail+1) % capacity; count++
dequeue(): if empty → return null; msg = buffer[head]; head = (head+1) % capacity; count--; return msg
dequeueBatch(n): collect up to n messages, return List
```

**Option B: Linked List (unbounded)**

```
Internal: Node head, tail
enqueue(msg): append to tail
dequeue(): remove from head
dequeueBatch(n): traverse and collect n nodes
```

**Thread Safety**: Wrap all operations in `lock.lock() / unlock()` or `synchronized(this)`.

### 4.2 Publish Flow

```
Publisher.publish(queueName, payload)
  → QueueManager receives
  → queue = getOrCreateQueue(queueName)
  → message = new Message(UUID, payload, timestamp)
  → queue.enqueue(message)
  → signal dispatcher / notify workers (if push-based)
```

### 4.3 Subscriber Registration & Delivery

```
QueueManager.registerSubscriber(queueName, subscriber)
  → queue = getQueue(queueName)
  → queue.addSubscriber(subscriber)

Dispatcher (runs in background):
  loop:
    messages = queue.dequeueBatch(subscriber.batchSize)
    if messages.isEmpty(): sleep(configurableDelay); continue
    for each subscriber of queue:
      try:
        retryPolicy.executeWithRetry(() -> subscriber.handle(messages))
      catch (after max retries):
        // optionally move to DLQ or log and drop
```

### 4.4 Retry with Backoff

**Exponential backoff**:

```
attempt 0: immediate
attempt 1: sleep(1s)
attempt 2: sleep(2s)
attempt 3: sleep(4s)
...
cap at maxRetries (e.g., 5)
```

```
executeWithRetry(callback):
  for i = 0 to maxRetries:
    try:
      callback.run()
      return
    catch:
      if i == maxRetries: rethrow
      sleep(backoffStrategy.getDelay(i))
```

### 4.5 Push vs Pull Model

- **Pull**: Dispatcher polls queue at interval; delivers to subscribers. Simpler; good for interview.
- **Push**: On `enqueue`, immediately wake dispatcher to deliver. More real-time; requires signaling (e.g., `Condition.signal()`).

Recommend **Pull with short interval** for simplicity; mention Push as enhancement.

---

## Phase 5: Package Structure (Matches Code)

```
messagequeue/
├── models/
│   ├── Message.java
│   └── MessagePayload.java (or use Map<String, Object>)
├── queue/
│   ├── MessageQueue.java (interface)
│   ├── CustomMessageQueue.java (circular buffer or linked list)
│   └── QueueFactory.java (optional)
├── broker/
│   ├── QueueManager.java (or MessageBroker — Facade)
│   ├── Publisher.java
│   ├── Subscriber.java
│   └── MessageHandler.java (functional interface)
├── dispatcher/
│   ├── MessageDispatcher.java
│   └── DeliveryWorker.java (optional)
├── retry/
│   ├── RetryPolicy.java
│   └── BackoffStrategy.java (interface) + ExponentialBackoff
├── exceptions/
│   ├── QueueFullException.java
│   ├── QueueNotFoundException.java
│   └── SubscriberException.java
├── config/
│   └── QueueConfig.java (batchSize, pollInterval, maxRetries)
└── Main.java
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Create queue | Queue name non-empty; unique per broker. |
| Publish | Queue exists; queue not full (if bounded). |
| Register subscriber | Queue exists; subscriberId unique per queue. |
| Remove subscriber | Subscriber exists; safe removal while dispatcher may be delivering. |
| Callback failure | Retry with backoff; don't block other subscribers. |
| Empty queue | Dispatcher sleeps; no NPE. |
| Concurrent publish + dequeue | Thread-safe queue; lock granularity (per-queue). |

---

## Phase 7: Implementation Order (Recommended)

1. **Message** — id, payload (Map), timestamp
2. **CustomMessageQueue** — enqueue, dequeue, dequeueBatch; start single-threaded
3. **Thread safety** — Add ReentrantLock to queue
4. **QueueManager** — createQueue, getQueue, store queues by name
5. **Publisher** — publish(queueName, payload) → QueueManager
6. **Subscriber** — id, callback, batchSize; MessageHandler interface
7. **QueueManager.registerSubscriber** — add subscriber to queue's list
8. **MessageDispatcher** — loop: dequeueBatch, invoke each subscriber's callback
9. **RetryPolicy** — executeWithRetry with exponential backoff
10. **Configurable delay** — ScheduledExecutorService for poll interval
11. **Bonus** — CLI/REPL for create queue, publish, subscribe, unsubscribe

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **Custom queue** | Implement circular buffer or linked list; no `java.util.Queue`. |
| **Decoupled pub-sub** | Publisher doesn't know subscribers; Subscriber doesn't know publisher. |
| **Batch consumption** | Subscriber receives `List<Message>`; configurable batch size. |
| **Failure resilience** | Retry with backoff; one subscriber's failure doesn't crash others. |
| **Thread safety** | ReentrantLock on queue; safe concurrent publish + multiple subscriber callbacks. |
| **SOLID** | SRP: Queue = storage; Dispatcher = delivery; RetryPolicy = retry. OCP: New BackoffStrategy without changing RetryPolicy. DIP: Depend on MessageHandler interface. |
| **Extensibility** | Add DLQ for poison messages; add persistence; add priority queues. |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| Custom Queue | `CustomMessageQueue` (array or linked list) |
| Named queues (EmailQueue, etc.) | `QueueManager` — Map<name, Queue> |
| Publisher pushes JSON | `Publisher.publish()` → `Message` with `Map<String, Object>` payload |
| Multiple subscribers per queue | `Queue.addSubscriber()`; List of Subscribers in Queue |
| Add/remove subscriber at runtime | `QueueManager.registerSubscriber`, `removeSubscriber` |
| Callback to receive messages | `Subscriber` with `MessageHandler` / `Consumer<List<Message>>` |
| Batch consumption | `queue.dequeueBatch(batchSize)`; callback receives List |
| Loosely coupled | Publisher → Queue; Subscriber ← Dispatcher; no direct dependency |
| Failure handling | `RetryPolicy.executeWithRetry()` around callback |
| Retry with backoff | `ExponentialBackoff` (or LinearBackoff) in RetryPolicy |
| Thread-safe queue | `ReentrantLock` on enqueue/dequeue |
| Configurable delay between pulls | `ScheduledExecutorService.scheduleWithFixedDelay` |
| CLI/REPL (bonus) | Main + command parser for create, publish, subscribe, unsubscribe |

---

## Interview Tips: How to Present This

1. **Start with the queue** — "I'll build a custom queue first — circular buffer for bounded or linked list for unbounded. No java.util.Queue."
2. **Call out pub-sub decoupling** — "Publisher pushes to a named queue; subscribers register callbacks. The dispatcher delivers messages asynchronously — publisher and subscriber never talk directly."
3. **Handle batch + retry early** — "Subscribers get configurable batch sizes. On callback failure, I'll retry with exponential backoff so one bad subscriber doesn't block others."
4. **Mention thread safety** — "Multiple publishers and subscribers; I'll use ReentrantLock on the queue and run the dispatcher in a separate thread."
5. **Bonus features show depth** — Configurable poll delay, CLI/REPL, DLQ for poison messages.

---

## Run (After Implementation)

```bash
./gradlew runMessagequeue
# Or add runMessagequeue task to build.gradle
```
