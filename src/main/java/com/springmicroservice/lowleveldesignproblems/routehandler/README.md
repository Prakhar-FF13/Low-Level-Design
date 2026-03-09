# Route handler / Middleware design

## Requirements:

Design a route handler:

1. route handler should match the incoming request to a controller
2. Should allow handling of request via middlewares before reaching the controller
3. Order of middleware can be different for reach route.
4. Logic of route handler can be run before or after processing the request.

Out of scope / Extensions:

1. Most specific controller should be matched, we assume we match from a list, first match is first chosen.

---

## Solution: Chain of Responsibility Pattern

To allow various middleware (e.g., Logging, Authentication) to process a request before or after the main `Controller` logic executes, the **Chain of Responsibility** pattern is used. 

By implementing an `IHandler` interface, each link in the chain (a specific middleware or the final controller) is completely decoupled. The `RouteHandler` itself simply holds a map of routes to the starting `IHandler` for each path, ensuring O(1) route resolution.

### UML Class Diagram

```mermaid
classDiagram
    class RouteHandler {
        -Map~String, IHandler~ handlers
        +serveRequest(Request request) Response
    }

    class IHandler {
        <<interface>>
        +handle(Request request) Response
    }

    class AuthHandler {
        -IHandler next
        +handle(Request request) Response
    }

    class LogBeforeHandler {
        -IHandler next
        +handle(Request request) Response
    }

    class LogAfterHandler {
        -IHandler next
        +handle(Request request) Response
    }

    class DoNothingController {
        -IHandler next
        +handle(Request request) Response
    }

    class Request {
        -String url
        -String method
        -String body
        -Map~String, String~ headers
        -Map~String, String~ pathParameters
        -Map~String, String~ queryParameters
    }

    class Response {
        
    }

    RouteHandler --> IHandler : mapped by URL
    RouteHandler ..> Request : processes
    RouteHandler ..> Response : returns

    IHandler <|.. AuthHandler : implements
    IHandler <|.. LogBeforeHandler : implements
    IHandler <|.. LogAfterHandler : implements
    IHandler <|.. DoNothingController : implements

    AuthHandler --> IHandler : next
    LogBeforeHandler --> IHandler : next
    LogAfterHandler --> IHandler : next
```