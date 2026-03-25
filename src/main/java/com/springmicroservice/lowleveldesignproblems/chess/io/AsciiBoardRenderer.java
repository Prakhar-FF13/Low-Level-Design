package com.springmicroservice.lowleveldesignproblems.chess.io;

import com.springmicroservice.lowleveldesignproblems.chess.models.board.Board;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.ChessPiece;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.Color;

/**
 * Renders the board with rank 8 at the top; white pieces as uppercase letters, black as lowercase.
 */
public final class AsciiBoardRenderer {

    private AsciiBoardRenderer() {
    }

    public static String render(Board board) {
        StringBuilder sb = new StringBuilder();
        sb.append("   a b c d e f g h\n");
        for (int r = 0; r < Board.SIZE; r++) {
            int rankLabel = 8 - r;
            sb.append(rankLabel).append("  ");
            for (int c = 0; c < Board.SIZE; c++) {
                char ch = board.getPieceAt(new Square(r, c))
                        .map(AsciiBoardRenderer::symbolFor)
                        .orElse('.');
                sb.append(ch).append(' ');
            }
            sb.append(' ').append(rankLabel).append('\n');
        }
        sb.append("   a b c d e f g h\n");
        return sb.toString();
    }

    private static char symbolFor(ChessPiece piece) {
        char base = switch (piece.getPieceType()) {
            case KING -> 'K';
            case QUEEN -> 'Q';
            case ROOK -> 'R';
            case BISHOP -> 'B';
            case KNIGHT -> 'N';
            case PAWN -> 'P';
        };
        return piece.getColor() == Color.WHITE ? base : Character.toLowerCase(base);
    }
}
