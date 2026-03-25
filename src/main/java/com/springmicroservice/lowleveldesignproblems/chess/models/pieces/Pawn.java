package com.springmicroservice.lowleveldesignproblems.chess.models.pieces;

import com.springmicroservice.lowleveldesignproblems.chess.models.board.Board;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;

public class Pawn extends AbstractChessPiece {
    private boolean hasMoved;

    public Pawn(Color color) {
        super(color, PieceType.PAWN);
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Invoked from {@link com.springmicroservice.lowleveldesignproblems.chess.models.board.Board}'s relocation path
     * after a legal move (e.g. {@link com.springmicroservice.lowleveldesignproblems.chess.models.board.Board#tryMove}).
     */
    public void markMoved() {
        hasMoved = true;
    }

    private int forwardDelta() {
        return color == Color.WHITE ? -1 : 1;
    }

    private int startingRankRow() {
        return color == Color.WHITE ? 6 : 1;
    }

    @Override
    public boolean canMove(Square from, Square to, Board board) {
        if (!isPieceAt(from, board)) {
            return false;
        }
        int fr = from.row();
        int fc = from.col();
        int tr = to.row();
        int tc = to.col();
        if (fr == tr && fc == tc) {
            return false;
        }
        int forward = forwardDelta();
        int dr = tr - fr;
        int dc = tc - fc;

        var toCell = board.getCell(to);
        if (dc == 0 && dr == forward && toCell.isEmpty()) {
            return true;
        }
        if (dc == 0 && dr == 2 * forward && fr == startingRankRow()) {
            int midRow = fr + forward;
            return board.getCell(midRow, fc).isEmpty() && toCell.isEmpty();
        }
        if (Math.abs(dc) == 1 && dr == forward && toCell.isOccupied()) {
            return toCell.getPiece().map(p -> !isFriendly(p)).orElse(false);
        }
        return false;
    }
}
