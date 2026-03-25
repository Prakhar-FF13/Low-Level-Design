package com.springmicroservice.lowleveldesignproblems.chess.models.pieces;

import java.util.List;

import com.springmicroservice.lowleveldesignproblems.chess.models.board.Board;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;

public interface ChessPiece {
    Color getColor();

    PieceType getPieceType();

    void setKilled(boolean killed);

    boolean isKilled();

    /**
     * Whether this piece may move from {@code from} to {@code to} on the current {@code board}.
     * Implementations should require that {@code this} is the piece occupying {@code from}.
     */
    boolean canMove(Square from, Square to, Board board);

    /**
     * All squares reachable in one move from {@code from} for this piece, given the current board.
     */
    List<Square> legalMovesFrom(Square from, Board board);
}
