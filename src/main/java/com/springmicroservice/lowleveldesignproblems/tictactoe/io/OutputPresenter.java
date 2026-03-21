package com.springmicroservice.lowleveldesignproblems.tictactoe.io;

import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Board;

/**
 * Abstraction for displaying output - allows swapping console for GUI/testing.
 */
public interface OutputPresenter {
    void displayBoard(Board board);

    void displayMessage(String message);

    void displayCurrentPlayer(String playerName);

    void displayPrompt(String prompt);
}
