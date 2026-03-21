package com.springmicroservice.lowleveldesignproblems.tictactoe.io;

/**
 * Abstraction for getting user input - allows swapping console for GUI/testing.
 */
public interface InputProvider {
    /**
     * @return int array [row, column] for the next move
     */
    int[] getNextMove(int boardSize);
}
