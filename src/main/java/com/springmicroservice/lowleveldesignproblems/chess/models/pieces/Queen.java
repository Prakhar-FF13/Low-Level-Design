package com.springmicroservice.lowleveldesignproblems.chess.models.pieces;

import com.springmicroservice.lowleveldesignproblems.chess.models.board.Board;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;

public class Queen extends AbstractChessPiece {

    public Queen(Color color) {
        super(color, PieceType.QUEEN);
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
        int dr = tr - fr;
        int dc = tc - fc;
        boolean orthogonal = (dr == 0 && dc != 0) || (dc == 0 && dr != 0);
        boolean diagonal = dr != 0 && dc != 0 && Math.abs(dr) == Math.abs(dc);
        if (!orthogonal && !diagonal) {
            return false;
        }
        if (!board.isPathClear(from, to)) {
            return false;
        }
        return board.getCell(to).getPiece().map(p -> !isFriendly(p)).orElse(true);
    }
}
