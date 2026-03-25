package com.springmicroservice.lowleveldesignproblems.chess.models.helpers;

/**
 * Immutable board coordinate: {@code row} and {@code col} are zero-based indices into the 8×8 grid
 * (same convention as {@link com.springmicroservice.lowleveldesignproblems.chess.models.board.Board}).
 */
public record Square(int row, int col) {

    public static final int BOARD_SIZE = 8;

    public boolean isOnBoard() {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }
}
