package com.springmicroservice.lowleveldesignproblems.texteditor;

/**
 * Command pattern: one reversible user step. {@link #execute} applies the change; {@link #undo} reverses it.
 */
public interface EditorCommand {

    void execute(TextBuffer buffer);

    void undo(TextBuffer buffer);
}
