package com.springmicroservice.lowleveldesignproblems.texteditor;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LIFO history: the most recently recorded command is undone first.
 */
public final class UndoManager {

    private final Deque<EditorCommand> history = new ArrayDeque<>();

    public void record(EditorCommand command) {
        history.push(command);
    }

    public boolean undo(TextBuffer buffer) {
        if (history.isEmpty()) {
            return false;
        }
        history.pop().undo(buffer);
        return true;
    }
}
