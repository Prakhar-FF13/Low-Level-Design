package com.springmicroservice.lowleveldesignproblems.texteditor;

/**
 * Owns the editable text and a single cursor. All index/cursor rules for insert/delete live here.
 */
public final class TextBuffer {

    private final StringBuilder text = new StringBuilder();
    private int cursor;

    public String getText() {
        return text.toString();
    }

    public int getCursorPosition() {
        return cursor;
    }

    public void moveCursorTo(int position) {
        if (position < 0 || position > text.length()) {
            throw new IllegalArgumentException("cursor out of bounds: " + position);
        }
        this.cursor = position;
    }

    public void insertAtCursor(String s) {
        text.insert(cursor, s);
        cursor += s.length();
    }

    public void insertAt(int offset, String s) {
        if (offset < 0 || offset > text.length()) {
            throw new IllegalArgumentException("offset out of bounds: " + offset);
        }
        text.insert(offset, s);
        cursor = offset + s.length();
    }

    public String deleteBeforeCursor(int characterCount) {
        if (characterCount <= 0) {
            return "";
        }
        int start = Math.max(0, cursor - characterCount);
        if (start == cursor) {
            return "";
        }
        String removed = text.substring(start, cursor);
        text.delete(start, cursor);
        cursor = start;
        return removed;
    }

    public void deleteRange(int startInclusive, int endExclusive) {
        if (startInclusive < 0 || endExclusive > text.length() || startInclusive > endExclusive) {
            throw new IllegalArgumentException("invalid range: [" + startInclusive + ", " + endExclusive + ")");
        }
        int len = endExclusive - startInclusive;
        text.delete(startInclusive, endExclusive);
        if (cursor > endExclusive) {
            cursor -= len;
        } else if (cursor > startInclusive) {
            cursor = startInclusive;
        }
    }
}
