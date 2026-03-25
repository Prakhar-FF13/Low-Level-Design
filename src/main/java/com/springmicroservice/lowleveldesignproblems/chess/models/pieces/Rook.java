package com.springmicroservice.lowleveldesignproblems.chess.models.pieces;

import com.springmicroservice.lowleveldesignproblems.chess.models.board.Board;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;

public class Rook extends AbstractChessPiece {

    public Rook(Color color) {
        super(color, PieceType.ROOK);
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
        if (fr != tr && fc != tc) {
            return false;
        }
        if (!board.isPathClear(from, to)) {
            return false;
        }
        return board.getCell(to).getPiece().map(p -> !isFriendly(p)).orElse(true);
    }
}
