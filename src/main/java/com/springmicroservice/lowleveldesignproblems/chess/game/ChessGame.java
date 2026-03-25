package com.springmicroservice.lowleveldesignproblems.chess.game;

import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.chess.models.board.Board;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Move;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.ChessPiece;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.Color;

/**
 * Two-player session: owns the {@link Board}, enforces side-to-move, delegates legality to the board/pieces.
 * <p>
 * Turn order is not modeled on {@link Board}; only {@code ChessGame} flips {@link #sideToMove} after a successful
 * {@link #playMove(Move)}.
 */
public class ChessGame {

    private final Board board;
    private Color sideToMove;

    public ChessGame() {
        this(Board.createWithStandardSetup(), Color.WHITE);
    }

    public ChessGame(Board board, Color sideToMove) {
        this.board = board;
        this.sideToMove = sideToMove;
    }

    public Board getBoard() {
        return board;
    }

    public Color getSideToMove() {
        return sideToMove;
    }

    /**
     * Applies a move if the piece on {@code move.from()} belongs to the side to move and the move is legal on the board.
     */
    public MoveResult playMove(Move move) {
        Optional<ChessPiece> piece = board.getPieceAt(move.from());
        if (piece.isEmpty()) {
            return MoveResult.NO_PIECE_ON_FROM;
        }
        if (piece.get().getColor() != sideToMove) {
            return MoveResult.WRONG_SIDE;
        }
        if (!board.tryMove(move)) {
            return MoveResult.ILLEGAL_MOVE;
        }
        sideToMove = opposite(sideToMove);
        return MoveResult.SUCCESS;
    }

    private static Color opposite(Color c) {
        return c == Color.WHITE ? Color.BLACK : Color.WHITE;
    }
}
