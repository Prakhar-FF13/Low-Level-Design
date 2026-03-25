package com.springmicroservice.lowleveldesignproblems.chess.models.pieces;

import com.springmicroservice.lowleveldesignproblems.chess.models.board.Board;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;

public class King extends AbstractChessPiece {

    public King(Color color) {
        super(color, PieceType.KING);
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
        int adr = Math.abs(tr - fr);
        int adc = Math.abs(tc - fc);
        if (adr > 1 || adc > 1) {
            return false;
        }
        return board.getCell(to).getPiece().map(p -> !isFriendly(p)).orElse(true);
    }
}
