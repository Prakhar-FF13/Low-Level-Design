# Tic Tac Toe LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the Tic Tac Toe game in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your Assumption |
|----------|-----------------|
| Board size? | N x N configurable. |
| Win length? | Configurable (e.g., 3 in a row on 5×5). |
| Player count? | 2 by default; extensible to N (PlayerFactory.createPlayers(n)). |
| Undo? | Optional — Stack<Move> for undo. |
| I/O abstraction? | InputProvider, OutputPresenter — swap Console for GUI/tests. |

**Why this matters**: N×N + configurable win length is a common interview extension.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
Board
├── size (N)
├── winLength
├── board (String[][])
└── printBoard(), setBoard(), isEmpty(), getCell()

Player
├── name
└── symbol (X, O, P1, P2, ...)

Move
├── player
├── row, column
└── (immutable)

GameState (interface)
├── isTerminal()
├── handleEndGame(GameStateContext)
└── onMoveApplied(context, move) → GameState

GameStateContext
├── currentState, board, players
├── winningStrategies, moves (Stack)
├── currentPlayer, winningPlayer
└── outputPresenter

InProgressState, WinState, DrawState (implements GameState)

WinningStrategy (interface)
└── isWinningMove(Board, Move) → boolean

RowWinningStrategy, ColumnWinningStrategy
MajorDiagonalWinningStrategy, MinorDiagonalWinningStrategy

InputProvider (interface)
└── getNextMove(boardSize) → int[]

OutputPresenter (interface)
└── displayBoard(), displayMessage(), displayCurrentPlayer(), displayPrompt()

Game (Template Method)
├── context, inputProvider, outputPresenter
├── playGame() — template loop
└── makeMove(), undoMove()
```

**Relationships**:
- GameStateContext holds state; Game delegates to currentState
- InProgressState uses WinningStrategy list to decide Win/Draw/Continue
- Game uses Template Method for playGame loop

---

## Phase 3: Choose Design Patterns

| Pattern | Where to Use | Why Interviewers Care |
|---------|--------------|------------------------|
| **State** | GameState (InProgress, Win, Draw) | Behavior changes by state; transitions in onMoveApplied. |
| **Strategy** | WinningStrategy (Row, Column, Diagonals) | Pluggable win checks; easy to add new rules. |
| **Factory** | WinningStrategyFactory, GameFactory, PlayerFactory | Centralized creation. |
| **Template Method** | Game.playGame() | Skeleton with hooks (displayWelcome, executeTurn, etc.). |
| **I/O abstraction** | InputProvider, OutputPresenter | Testable; swap Console for GUI. |

---

## Phase 4: Core Logic — Matches Code

### 4.1 Game Loop (Template Method)

```
playGame():
  displayWelcome()
  while (!context.getCurrentState().isTerminal())
    displayBoard()
    displayCurrentPlayer()
    move = getNextMove()
    makeMove(move)
  handleEndGame()
```

### 4.2 makeMove Flow

```
makeMove(row, col):
  context.getBoard().setBoard(row, col, currentPlayer.symbol)
  context.getMoves().push(move)
  newState = context.getCurrentState().onMoveApplied(context, move)
  context.setCurrentState(newState)
```

### 4.3 InProgressState.onMoveApplied

- For each WinningStrategy: if isWinningMove(board, move) → return WinState
- If board full → return DrawState
- Else: set next player, return this (InProgressState)

### 4.4 WinningStrategy Examples

- RowWinningStrategy: same symbol in entire row
- ColumnWinningStrategy: same symbol in entire column
- MajorDiagonalWinningStrategy: row == col
- MinorDiagonalWinningStrategy: row + col == size - 1
- All respect winLength (e.g., count consecutive in row up to winLength)

### 4.5 Undo

- Pop from moves stack; clear cell; set currentPlayer to previous

---

## Phase 5: Package Structure (Matches Code)

```
tictactoe/
├── models/
│   ├── Board.java
│   ├── Player.java
│   └── Move.java
├── state/
│   ├── GameState.java
│   ├── GameStateContext.java
│   ├── InProgressState.java
│   ├── WinState.java
│   └── DrawState.java
├── strategies/
│   ├── WinningStrategy.java
│   ├── RowWinningStrategy.java
│   ├── ColumnWinningStrategy.java
│   ├── MajorDiagonalWinningStrategy.java
│   └── MinorDiagonalWinningStrategy.java
├── factory/
│   ├── WinningStrategyFactory.java
│   ├── GameFactory.java
│   └── PlayerFactory.java
├── io/
│   ├── InputProvider.java
│   ├── OutputPresenter.java
│   ├── ConsoleInputProvider.java
│   └── ConsoleOutputPresenter.java
├── services/
│   └── Game.java
├── Main.java
└── README.md
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Validation |
|----------|------------|
| Invalid move (out of bounds) | Board or Game throws; retry prompt |
| Cell already occupied | Check isEmpty(); retry |
| Undo when no moves | Check moves.isEmpty() |
| N players | PlayerFactory.createPlayers(n) with symbols P1..Pn |
| winLength on N×N | Board(size, winLength) constructor |

---

## Phase 7: Implementation Order (Recommended)

1. **Models** — Board, Player, Move
2. **WinningStrategy** — Row, Column, Diagonals
3. **GameState** — interface; InProgressState, WinState, DrawState
4. **GameStateContext** — holds all game data
5. **InputProvider, OutputPresenter** — interfaces + Console impl
6. **Game** — Template Method playGame
7. **Factories** — WinningStrategyFactory, GameFactory, PlayerFactory
8. **Main** — wire and run

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to Show It |
|-----------|----------------|
| **State + Strategy** | State for game lifecycle; Strategy for win detection |
| **Decoupled I/O** | InputProvider, OutputPresenter — test with mocks |
| **Configurability** | N×N, winLength, N players |
| **Defensive copy** | Board.getBoard() returns copy to prevent mutation |
| **SOLID** | SRP per strategy/state; OCP for new strategies |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary Component |
|-------------|-------------------|
| N×N board | Board(size, winLength) |
| Win detection | WinningStrategy implementations |
| Game states | GameState, InProgressState, WinState, DrawState |
| Turn management | GameStateContext.currentPlayer |
| Undo | Stack<Move> in context |
| I/O | InputProvider, OutputPresenter |
| N players | PlayerFactory.createPlayers(n) |

---

## Run

```bash
./gradlew runTictactoe
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.tictactoe.*"
```
