package com.springmicroservice.lowleveldesignproblems.texteditor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TextEditorServiceTest {

    private TextEditorService editor;

    @BeforeEach
    void setUp() {
        editor = new TextEditorService();
    }

    @Test
    void addText_appendsAtCursor() {
        editor.addText("hi");
        assertEquals("hi", editor.getText());
        assertEquals(2, editor.getCursorPosition());
    }

    @Test
    void deleteText_removesBeforeCursor() {
        editor.addText("hello");
        editor.deleteText(2);
        assertEquals("hel", editor.getText());
        assertEquals(3, editor.getCursorPosition());
    }

    @Test
    void undo_afterAdd_revertsAdd() {
        editor.addText("a");
        editor.addText("b");
        assertTrue(editor.undo());
        assertEquals("a", editor.getText());
        assertTrue(editor.undo());
        assertEquals("", editor.getText());
    }

    @Test
    void undo_afterDelete_restoresDeleted() {
        editor.addText("xyz");
        editor.deleteText(1);
        assertEquals("xy", editor.getText());
        assertTrue(editor.undo());
        assertEquals("xyz", editor.getText());
    }

    @Test
    void undo_whenEmpty_returnsFalse() {
        assertFalse(editor.undo());
    }
}
