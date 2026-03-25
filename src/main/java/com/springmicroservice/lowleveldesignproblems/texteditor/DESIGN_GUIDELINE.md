# Text Editor LLD — Design Guide for Strong Interview Performance

> **Implemented solution & diagrams:** [README.md](./README.md)

This guide walks through designing an in-memory text editor with **add**, **delete**, and **undo** in a way that showcases strong LLD skills. Follow the steps in order.

---

## Phase 1: Clarify & Scope (Interview Tip: Do This First)

Before coding, clarify these with the interviewer (or decide for yourself):

| Question | Typical assumption (matches this repo) |
|----------|----------------------------------------|
| Where does “add” insert? | At the **cursor** (single insertion point). |
| What is “delete”? | **N characters before the cursor** (backspace-style). |
| Undo granularity | One undo = **one** `addText` or **one** `deleteText` that actually changed the buffer. |
| Redo? | Out of scope here; if asked, add a **second stack** and mirror undo with **`execute`** on popped redo commands. |
| Multi-user / concurrency? | **Single-threaded** service unless stated. |
| Character set | Java `String` (Unicode); no extra encoding layer unless asked. |

**Why this matters:** Cursor semantics and undo boundaries determine your command fields and buffer API.

---

## Phase 2: Identify Core Entities & Relationships

Start with **nouns** from requirements. Map to entities:

```
TextBuffer
├── StringBuilder (or similar) for mutable text
├── cursorPosition (int)
└── insert/delete helpers used by commands

TextEditorService (facade — “service layer”)
├── TextBuffer
├── UndoManager
└── addText, deleteText, undo, getText, getCursorPosition

EditorCommand (interface)
├── execute(TextBuffer)   ← apply the operation
└── undo(TextBuffer)      ← inverse operation

InsertCommand
├── offset (where insert starts), text (what was inserted)
├── execute → insertAt(offset, text)
└── undo → deleteRange(offset, offset + text.length())

DeleteCommand
├── offset, deletedText
├── execute → deleteRange(offset, offset + deletedText.length())
└── undo → insertAt(offset, deletedText)

UndoManager
├── record(EditorCommand) after a successful execute
└── undo(buffer) → pop and command.undo(buffer)
```

**Relationships:**

- **`TextEditorService`** builds a command, calls **`execute(buffer)`**, then **`record(command)`**. Undo never re-executes; it only calls **`undo(buffer)`** on the popped command.
- **`TextBuffer`** is the **single source of truth** for current text and cursor.
- **`UndoManager`** owns **LIFO** history (stack).

---

## Phase 3: Choose Design Patterns

| Pattern | Where to use | Why interviewers care |
|---------|--------------|------------------------|
| **Command** | `EditorCommand` with **`execute` + `undo`** | Symmetric API: forward action and reverse live on the same object; natural fit for **redo** later. |
| **Memento (alternative)** | Store full buffer snapshot per step | Easy to reason about; **memory** grows with document size and history depth. |
| **Facade** | `TextEditorService` | One entry point; callers do not see `InsertCommand` / `DeleteCommand`. |
| **Strategy (optional)** | Pluggable buffer: gap buffer, rope | Mention only if asked about **very large** documents. |

**Recommendation for interviews:** **Command + inverse operations** on the buffer. Avoid copying the entire string on every keystroke unless the scope is trivial.

---

## Phase 4: Core Logic — Flows (aligned with code)

### 4.1 Add text

```
1. Reject null text; treat empty string as no-op (nothing to record).
2. offset = buffer.getCursorPosition()
3. command = new InsertCommand(offset, text)
4. command.execute(buffer)   // inserts at offset, updates cursor
5. undoManager.record(command)
```

### 4.2 Delete text (backspace up to N characters)

```
1. If characterCount <= 0, return.
2. cursorBefore = buffer.getCursorPosition()
3. start = max(0, cursorBefore - characterCount)
4. If start == cursorBefore, nothing to delete — return.
5. removed = buffer.getText().substring(start, cursorBefore)
6. command = new DeleteCommand(start, removed)
7. command.execute(buffer)   // deleteRange(start, start + removed.length())
8. undoManager.record(command)
```

### 4.3 Undo

```
1. If stack empty → return false (or no-op).
2. command = pop
3. command.undo(buffer)
4. Return true
```

**Inverse rules (what `undo` does):**

- After **insert**: undo **deletes** `[offset, offset + text.length())`.
- After **delete**: undo **inserts** `deletedText` at `offset`.

---

## Phase 5: API Shape (Service Layer)

| Method | Responsibility |
|--------|----------------|
| `addText(String text)` | `InsertCommand` + execute + record. |
| `deleteText(int characterCount)` | Build range, `DeleteCommand` + execute + record. |
| `undo()` | Pop and undo; return whether an action ran. |
| `getText()` / `getCursorPosition()` | Observability for tests or a future UI. |

Avoid exposing `EditorCommand` to callers unless the problem asks for macros or scripting.

---

## Phase 6: Edge Cases Checklist

- **Empty buffer / delete at start:** `deleteText` is a no-op when there is nothing to remove.
- **Undo on empty stack:** returns `false`; does not throw.
- **Null `addText`:** throw `IllegalArgumentException` (explicit contract).
- **Empty `addText`:** no command recorded.

---

## Phase 7: Testing Mindset (What to Mention)

- **`TextEditorService`:** add → undo restores previous text; chained adds undo in reverse order; delete → undo restores; undo on empty history.
- Optional: **`TextBuffer`** in isolation if buffer logic grows.

---

## Phase 8: Optional Extensions (If Time Permits)

- **Redo:** keep a second stack; on **undo**, push the command to redo; on **redo**, pop from redo and **`execute`**.
- **Composite command:** replace selection = one command that deletes a range then inserts (or two commands with a single record).
- **Efficient buffer:** gap buffer or rope for huge documents — only if the interviewer asks about scale.

---

## Summary

| Piece | Role |
|-------|------|
| `TextBuffer` | Holds text + cursor; low-level mutations. |
| `EditorCommand` + concrete commands | **`execute`** applies; **`undo`** reverses. |
| `UndoManager` | Stack of commands; **undo** pops and runs **`undo`**. |
| `TextEditorService` | Facade: the **service layer** required by the problem. |

This structure satisfies: add text, delete text, undo last operation, no UI, no persistence, and a single service-style API.
