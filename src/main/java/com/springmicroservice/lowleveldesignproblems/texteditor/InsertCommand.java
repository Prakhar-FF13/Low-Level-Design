package com.springmicroservice.lowleveldesignproblems.texteditor;

/**
 * Inserts a string at a fixed offset (the cursor position when the command was created).
 * Undo removes exactly that many characters at that offset.
 */
public final class InsertCommand implements EditorCommand {

    private final int offset;
    private final String text;

    public InsertCommand(int offset, String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("text must be non-empty");
        }
        this.offset = offset;
        this.text = text;
    }

    @Override
    public void execute(TextBuffer buffer) {
        buffer.insertAt(offset, text);
    }

    @Override
    public void undo(TextBuffer buffer) {
        buffer.deleteRange(offset, offset + text.length());
    }
}
