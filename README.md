# 🚀 The Ultimate Low-Level Design Playground!

Welcome to my LLD repo! If you're tired of boring, textbook explanations of design patterns and want to see how to actually build cool stuff from scratch, you're in the right place. 

I built this repository to practice, revise, and completely master Object-Oriented Design (OOD). Each folder here is a deep dive into a classic system design interview problem. No fluffy theory just hard-hitting Java code, strict design patterns, and solid architecture!

Here is what we are building and learning:

## 🎮 The Problems

### 1. [Rule Engine 🧮](src/main/java/com/springmicroservice/lowleveldesignproblems/ruleengine)
Ever wonder how corporate expense systems dynamically flag bad transactions without having a billion `if/else` statements? This teaches you how to build a dynamic, plug-and-play validation engine.
*   **What it teaches:** How to stop hardcoding business logic and make systems scalable.
*   **Design Patterns:** Extensible Rule Pattern (Strategy/Composite vibes).
*   **SOLID Principles:** **Open/Closed Principle** (add new rules without touching the core engine) and **Single Responsibility** (each rule does one thing!).

### 2. [ATM Machine 🏧](src/main/java/com/springmicroservice/lowleveldesignproblems/atmmachine)
ATMs are super tricky because giving out cash when there's no card inserted is a disaster. This problem teaches you how to lock down a system's behavior based on exactly what "state" it is currently in.
*   **What it teaches:** How to perfectly model state machines in code so your hardware/software doesn't glitch out.
*   **Design Patterns:** **State Design Pattern** (Absolute lifesaver for this!).
*   **SOLID Principles:** **Open/Closed Principle** (add new states like 'ReadPIN' easily) and strong **Encapsulation**.

### 3. [Route Handler / Web Middleware 🌐](src/main/java/com/springmicroservice/lowleveldesignproblems/routehandler)
How do web servers like Express or Spring Boot pass your HTTP request through a gauntlet of authenticators, loggers, and parsers before it hits the controller? We build that gauntlet from scratch here.
*   **What it teaches:** How to chain a bunch of independent operations together sequentially.
*   **Design Patterns:** **Chain of Responsibility Pattern**.
*   **SOLID Principles:** **Single Responsibility** (each middleware ONLY cares about its one specific job).

### 4. [Cache System ⚡](src/main/java/com/springmicroservice/lowleveldesignproblems/cachesystem)
Writing a dictionary to memory is easy. Writing an ultra-fast, thread-safe Cache that intuitively knows what data to delete when it gets full? That's engineering.
*   **What it teaches:** How to build pluggable architectures and manage memory eviction.
*   **Design Patterns:** **Strategy Pattern** (for swapping eviction policies like LRU vs LFU) and **Factory Pattern** (for generating the cache).
*   **SOLID Principles:** Heavy emphasis on **Dependency Inversion** (depending on abstract policies, not concrete algorithms).

### 5. [Cricbuzz / Live Score Broadcaster 🏏](src/main/java/com/springmicroservice/lowleveldesignproblems/cricbuzz)
Building a system that violently blasts live cricket scores to 50 million phones the millisecond a wicket falls.
*   **What it teaches:** How to build insanely decoupled systems where the backend has zero clue who it's sending data to—it just yells it into the void, and the right apps catch it.
*   **Design Patterns:** **Observer Pattern** (Publisher/Subscriber) equipped with custom **Predicate Filtering** so users only get the exact updates they ask for.
*   **SOLID Principles:** **Dependency Inversion Principle** (The producer only depends on a simple Consumer Interface, allowing infinite types of apps to connect!).

### 6. [Parking Lot System 🚗](src/main/java/com/springmicroservice/lowleveldesignproblems/parkinglot)
The classic LLD problem on steroids. Instead of generic `if (type == CAR)` matching, this builds a mathematical subset-matching engine using vehicle "capabilities", alongside real-time event publishing.
*   **What it teaches:** Domain modeling, Capability-based configuration, and decoupling cross-cutting concerns (like payments and analytics).
*   **Design Patterns:** **Strategy Pattern** (Best-Fit Slot Matching & Modular Payments), **Observer Pattern** (Live Display Boards), and **Facade Pattern**.
*   **SOLID Principles:** High emphasis on **Single Responsibility** (splitting ticket management, payments, and events into micro-services) and the **Open/Closed Principle**.

### 7. [BookMyShow 🎬](src/main/java/com/springmicroservice/lowleveldesignproblems/bookmyshow)
A movie ticket booking system with seat selection, concurrent booking handling, and REST API. Built with DDD and Hexagonal Architecture.
*   **What it teaches:** Domain-Driven Design, Ports & Adapters, pessimistic locking for concurrency, and layered testing (unit → controller → integration).
*   **Design Patterns:** **Hexagonal Architecture** (Ports & Adapters), **Repository Pattern**, **Domain Services**.
*   **SOLID Principles:** **Dependency Inversion** (domain depends on ports, not adapters), **Single Responsibility** (domain vs infrastructure vs API).