package com.springmicroservice.lowleveldesignproblems.chess.models.pieces;

import java.util.ArrayList;
import java.util.List;

import com.springmicroservice.lowleveldesignproblems.chess.models.board.Board;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;

public abstract class AbstractChessPiece implements ChessPiece {
    protected final Color color;
    protected final PieceType pieceType;
    protected boolean killed;

    protected AbstractChessPiece(Color color, PieceType pieceType) {
        this.color = color;
        this.pieceType = pieceType;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public PieceType getPieceType() {
        return pieceType;
    }

    @Override
    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    @Override
    public boolean isKilled() {
        return killed;
    }

    @Override
    public List<Square> legalMovesFrom(Square from, Board board) {
        List<Square> moves = new ArrayList<>();
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Square candidate = new Square(r, c);
                if (canMove(from, candidate, board)) {
                    moves.add(candidate);
                }
            }
        }
        return moves;
    }

    /**
     * True if {@code this} is the piece occupying {@code from} on {@code board}.
     */
    protected final boolean isPieceAt(Square from, Board board) {
        return board.getPieceAt(from).filter(p -> p == this).isPresent();
    }

    protected boolean isFriendly(ChessPiece other) {
        return other != null && other.getColor() == color;
    }
}
