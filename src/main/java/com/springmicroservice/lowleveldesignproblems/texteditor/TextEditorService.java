package com.springmicroservice.lowleveldesignproblems.texteditor;

/**
 * Service layer: add text, delete before cursor, undo — coordinates {@link TextBuffer} and {@link UndoManager}.
 */
public final class TextEditorService {

    private final TextBuffer buffer = new TextBuffer();
    private final UndoManager undoManager = new UndoManager();

    public String getText() {
        return buffer.getText();
    }

    public int getCursorPosition() {
        return buffer.getCursorPosition();
    }

    public void addText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }
        if (text.isEmpty()) {
            return;
        }
        int offsetBefore = buffer.getCursorPosition();
        InsertCommand command = new InsertCommand(offsetBefore, text);
        command.execute(buffer);
        undoManager.record(command);
    }

    public void deleteText(int characterCount) {
        if (characterCount <= 0) {
            return;
        }
        int cursorBefore = buffer.getCursorPosition();
        int start = Math.max(0, cursorBefore - characterCount);
        if (start == cursorBefore) {
            return;
        }
        String removed = buffer.getText().substring(start, cursorBefore);
        DeleteCommand command = new DeleteCommand(start, removed);
        command.execute(buffer);
        undoManager.record(command);
    }

    public boolean undo() {
        return undoManager.undo(buffer);
    }
}
