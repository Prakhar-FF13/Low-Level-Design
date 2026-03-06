# Rule Engine LLD

## Requirements:

You are tasked with designing a system that evaluates business expenses submitted
by employees.
Managers can define policies (rules) to control expenses, ensuring employees do not
misuse corporate cards or exceed allowances.
Your goal is to build a rules engine that:
1. Evaluates individual expenses against a set of rules.
2. Evaluates aggregated trip-level expenses against a set of rules.
3. Flags any violations clearly.

A list of rules to evaluate. Rules can be applied at:
Expense level (per individual expense).
Trip level (across all expenses belonging to a trip).

#### Basic Rules
Start with the following rules:
1. No restaurant expense can exceed $75.
2. No airfare expenses are allowed.
3. No entertainment expenses are allowed. 
4. No single expense can exceed $250.

#### Extended Rules
Later, add support for trip-level rules such as:
1. A trip cannot exceed $2000 in total expenses. 
2. Total meal (restaurant) expenses per trip cannot exceed $1000.

#### Additional Notes
The system should be extensible: managers may eventually have hundreds of
rules, so adding rules should not require rewriting the core evaluation logic.
The same framework should support future custom rules (e.g., weekend-only
expenses, vendor blacklists, monthly budget caps).

### Out of Scope
- No priority based execution of rules / no strategy of rule executions. We are going with execution of rules in the order we get them.
- No DB interactions.
- No Rest API interactions, simple JUnit testing would do.
- No dynamic rules based on expenses provided i.e. static rules provided at creation are used.

### Non functional
- SOLID principles
- Extensible - should be easy to add new rules later on

## Solution

### Structure:

##### api package:
- not used here but can host the controller layer if exposing REST APIs
- has demo code to show how it can be done.
- has spring beans defined for creation of controller.

##### application package:
- hosts orchestrating layer like services.
- not used here as we are not exposing any controllers and stuff.
- has spring beans defined for creation of services.

##### domain package:
- hosts the actual logic of the problem.
- does not use any spring related annotations to allow unit testing easily.
- subpackages
  - **engine**: hosts main class / entry point.
  - **model**: hosts models like Expense, Violations etc. The classes which hold data.
  - **rule**: contains rules interfaces and their implementations.

##### repository package:
- JPA repository for DB interaction
- not used in this problem.

### What this problem teaches:
- SOLID principles like
  - Dependency Injection (depending on Rules interface)
  - Open Closed Principles (No modifications to main class handling validation of expenses when new rules are added)
  - Single Responsibility
  - Liskov Substitution (not used here)
  - Interface Segregation (sort of as we had 2 interfaces for different things, not a combined one)
- Java streams
- Clean Code
  - small functions
  - clear separation of domain and spring wrapper.

## Remarks:

- Easy problem
- Structuring is probably more important in this problem.
- Adding rule execution strategy could make it challenging. Might introduce strategy pattern.

