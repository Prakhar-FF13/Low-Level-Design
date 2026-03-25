# Chess LLD — Design Guide for Strong Interview Performance

This guide walks you through designing the chess module in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Your assumption (this codebase) |
|----------|----------------------------------|
| Board size? | Fixed 8×8. |
| Algebraic notation? | Files `a`–`h`, ranks `1`–`8`; maps to `Square(row, col)`. |
| Castling / en passant? | **Out of scope** for v1 — extend `Board` / `Move` later. |
| Check / checkmate? | **Out of scope** for v1 — add pseudo-legal + king safety later. |
| Turn order? | `ChessGame.sideToMove`; White starts. |
| Where do piece rules live? | `ChessPiece.canMove`; path checks on `Board.isPathClear`. |
| I/O abstraction? | Optional next step — today `AlgebraicNotation` + `AsciiBoardRenderer` are concrete helpers. |

**Why this matters**: Separating **board physics** from **whose turn it is** is a common interview talking point.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
Square (record)
├── row, col
└── isOnBoard()

Move (record)
├── from: Square
└── to: Square

Cell
├── coordinate: Square
├── piece: Optional<ChessPiece>
└── placePiece, removePiece, isEmpty

Board
├── Cell[8][8]
├── setupStandardPosition()
├── getCell(Square), getPieceAt(Square)
├── isPathClear (sliding pieces)
├── isLegalMove(Move), tryMove(Move), applyMove(Move)

ChessPiece (interface)
├── Color, PieceType
├── canMove(Square, Square, Board)
├── legalMovesFrom(Square, Board)
└── killed flag

AbstractChessPiece
├── isPieceAt(Square, Board)
└── default legalMovesFrom (scan 64 squares)

King, Queen, Rook, Bishop, Knight, Pawn

ChessGame
├── Board board
├── Color sideToMove
└── playMove(Move) → MoveResult

MoveResult (enum)
├── SUCCESS, NO_PIECE_ON_FROM, WRONG_SIDE, ILLEGAL_MOVE

AlgebraicNotation (utility)
├── parseSquare, parseMove, format

AsciiBoardRenderer (utility)
└── render(Board)

Main → ChessConsoleApp → loop + I/O
```

**Relationships**:

- `Board` composes `Cell`s; each `Cell` optionally holds one `ChessPiece`.
- `ChessGame` owns `Board` and enforces **side to move** before calling `Board.tryMove`.
- `Move` pairs two `Square`s; `Board` performs relocation if rules pass.

---

## Phase 3: Choose Design Patterns

| Pattern | Where to use | Why interviewers care |
|---------|----------------|------------------------|
| **Polymorphism** | `ChessPiece` + concrete pieces | Open for new piece types; no giant `switch`. |
| **Value object** | `Square`, `Move` | Immutability, clear APIs, future undo/history. |
| **Facade** | `ChessGame` | One call for “play move with turn rules”. |
| **Utility / pure helpers** | `AlgebraicNotation`, `AsciiBoardRenderer` | Rules stay independent of string format. |

---

## Phase 4: Core Logic — Matches Code

### 4.1 Console loop (`ChessConsoleApp`)

```
while not quit:
  print AsciiBoardRenderer.render(board)
  print sideToMove
  line = readLine()
  if help → print help
  if quit → exit
  move = AlgebraicNotation.parseMove(line)
  if empty → error, continue
  result = chessGame.playMove(move)
  switch result → message
```

### 4.2 `ChessGame.playMove`

```
piece = board.getPieceAt(move.from())
if piece empty → NO_PIECE_ON_FROM
if piece.color != sideToMove → WRONG_SIDE
if !board.tryMove(move) → ILLEGAL_MOVE
sideToMove = opposite(sideToMove)
return SUCCESS
```

### 4.3 `Board.tryMove`

```
if !isLegalMove(move) → false
relocate(move): capture?, from.remove, to.place, Pawn.markMoved if applicable
return true
```

### 4.4 `Board.isLegalMove`

```
valid squares, piece on from
return piece.canMove(from, to, this)
```

### 4.5 Concrete piece (example: `Rook`)

- Same row or same column; `Board.isPathClear(from, to)`.
- Destination empty or enemy; not friendly capture.

---

## Phase 5: Package Structure (Matches Code)

```
chess/
├── Main.java
├── ChessConsoleApp.java
├── README.md
├── DESIGN_GUIDE.md
├── game/
│   ├── ChessGame.java
│   └── MoveResult.java
├── io/
│   ├── AlgebraicNotation.java
│   └── AsciiBoardRenderer.java
└── models/
    ├── board/
    │   ├── Board.java
    │   └── Cell.java
    ├── helpers/
    │   ├── Move.java
    │   └── Square.java
    └── pieces/
        ├── ChessPiece.java
        ├── AbstractChessPiece.java
        ├── Color.java
        ├── PieceType.java
        ├── King.java
        ├── Queen.java
        ├── Rook.java
        ├── Bishop.java
        ├── Knight.java
        └── Pawn.java
```

---

## Phase 6: Key Validations & Edge Cases

| Scenario | Handling |
|----------|----------|
| Unparseable input | `parseMove` returns empty; user prompted again |
| Empty from square | `MoveResult.NO_PIECE_ON_FROM` |
| Moving opponent’s piece | `MoveResult.WRONG_SIDE` |
| Illegal piece move | `Board.tryMove` false → `ILLEGAL_MOVE` |
| Capture | `relocate` marks captured piece `killed` before replace |
| Pawn double step | `Pawn.canMove` + `markMoved` after successful move |

---

## Phase 7: Implementation Order (Recommended)

1. **Value objects** — `Square`, `Move`
2. **Board + Cell** — grid, `getCell`, `getPieceAt`, `isPathClear`
3. **ChessPiece** — `AbstractChessPiece`, then each piece type
4. **Board.tryMove / isLegalMove** — wire to `canMove`
5. **ChessGame** — `sideToMove`, `playMove`, `MoveResult`
6. **I/O** — `AlgebraicNotation`, `AsciiBoardRenderer`
7. **ChessConsoleApp + Main** — loop and Gradle entry

---

## Phase 8: What Makes a "Strong Hire" LLD

| Attribute | How to show it |
|-----------|----------------|
| **Layering** | `Board` = rules + state; `ChessGame` = session + turns |
| **Extensibility** | New pieces via `ChessPiece`; new rules without breaking `Square`/`Move` |
| **Value objects** | `Square`/`Move` records for clarity and history |
| **Testability** | `Board` / `AlgebraicNotation` testable without `System.in` |
| **SOLID** | SRP per class; OCP for new pieces and I/O formats |

---

## Phase 9: Quick Reference — Requirement → Component

| Requirement | Primary component |
|-------------|-------------------|
| 8×8 grid | `Board`, `Cell`, `Square` |
| Piece movement | `ChessPiece` implementations + `Board.isPathClear` |
| From/to move | `Move` record |
| Apply relocation | `Board` private `relocate` |
| Whose turn | `ChessGame.sideToMove` |
| Algebraic input | `AlgebraicNotation` |
| ASCII display | `AsciiBoardRenderer` |
| Console app | `ChessConsoleApp`, `Main` |

---

## Run

```bash
./gradlew runChess
```

When chess unit tests are added under this package:

```bash
./gradlew test --tests "com.springmicroservice.lowleveldesignproblems.chess.**"
```
