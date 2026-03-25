package com.springmicroservice.lowleveldesignproblems.texteditor;

/**
 * Deletes a contiguous range {@code [offset, offset + deletedText.length())}.
 * Undo puts the same text back at {@code offset}.
 */
public final class DeleteCommand implements EditorCommand {

    private final int offset;
    private final String deletedText;

    public DeleteCommand(int offset, String deletedText) {
        if (deletedText == null || deletedText.isEmpty()) {
            throw new IllegalArgumentException("deletedText must be non-empty");
        }
        this.offset = offset;
        this.deletedText = deletedText;
    }

    @Override
    public void execute(TextBuffer buffer) {
        buffer.deleteRange(offset, offset + deletedText.length());
    }

    @Override
    public void undo(TextBuffer buffer) {
        buffer.insertAt(offset, deletedText);
    }
}
