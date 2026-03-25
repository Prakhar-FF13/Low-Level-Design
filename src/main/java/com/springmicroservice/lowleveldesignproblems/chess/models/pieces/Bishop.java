package com.springmicroservice.lowleveldesignproblems.chess.models.pieces;

import com.springmicroservice.lowleveldesignproblems.chess.models.board.Board;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;

public class Bishop extends AbstractChessPiece {

    public Bishop(Color color) {
        super(color, PieceType.BISHOP);
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
        if (Math.abs(tr - fr) != Math.abs(tc - fc)) {
            return false;
        }
        if (!board.isPathClear(from, to)) {
            return false;
        }
        return board.getCell(to).getPiece().map(p -> !isFriendly(p)).orElse(true);
    }
}
