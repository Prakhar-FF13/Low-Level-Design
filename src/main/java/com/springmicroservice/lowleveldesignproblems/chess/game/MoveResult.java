package com.springmicroservice.lowleveldesignproblems.chess.game;

/**
 * Outcome of attempting a move in {@link ChessGame}.
 */
public enum MoveResult {
    SUCCESS,
    NO_PIECE_ON_FROM,
    WRONG_SIDE,
    ILLEGAL_MOVE
}
