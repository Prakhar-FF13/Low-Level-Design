package com.springmicroservice.lowleveldesignproblems.chess.models.board;

import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Move;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.Bishop;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.ChessPiece;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.Color;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.King;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.Knight;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.Pawn;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.Queen;
import com.springmicroservice.lowleveldesignproblems.chess.models.pieces.Rook;

/**
 * Standard layout: row 0 = rank 8 (black's back rank), row 7 = rank 1 (white's back rank).
 * Column 0 = file a, column 7 = file h.
 */
public class Board {
    public static final int SIZE = Square.BOARD_SIZE;

    private final Cell[][] cells;

    public Board() {
        cells = new Cell[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = new Cell(new Square(r, c), Optional.empty());
            }
        }
    }

    public static Board createWithStandardSetup() {
        Board board = new Board();
        board.setupStandardPosition();
        return board;
    }

    public void setupStandardPosition() {
        clearPieces();
        placeRank0();
        placeRank1();
        placeRank6();
        placeRank7();
    }

    private void clearPieces() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c].removePiece();
            }
        }
    }

    private void placeRank0() {
        int r = 0;
        cells[r][0].placePiece(new Rook(Color.BLACK));
        cells[r][1].placePiece(new Knight(Color.BLACK));
        cells[r][2].placePiece(new Bishop(Color.BLACK));
        cells[r][3].placePiece(new Queen(Color.BLACK));
        cells[r][4].placePiece(new King(Color.BLACK));
        cells[r][5].placePiece(new Bishop(Color.BLACK));
        cells[r][6].placePiece(new Knight(Color.BLACK));
        cells[r][7].placePiece(new Rook(Color.BLACK));
    }

    private void placeRank1() {
        int r = 1;
        for (int c = 0; c < SIZE; c++) {
            cells[r][c].placePiece(new Pawn(Color.BLACK));
        }
    }

    private void placeRank6() {
        int r = 6;
        for (int c = 0; c < SIZE; c++) {
            cells[r][c].placePiece(new Pawn(Color.WHITE));
        }
    }

    private void placeRank7() {
        int r = 7;
        cells[r][0].placePiece(new Rook(Color.WHITE));
        cells[r][1].placePiece(new Knight(Color.WHITE));
        cells[r][2].placePiece(new Bishop(Color.WHITE));
        cells[r][3].placePiece(new Queen(Color.WHITE));
        cells[r][4].placePiece(new King(Color.WHITE));
        cells[r][5].placePiece(new Bishop(Color.WHITE));
        cells[r][6].placePiece(new Knight(Color.WHITE));
        cells[r][7].placePiece(new Rook(Color.WHITE));
    }

    public Cell getCell(int row, int col) {
        return cells[row][col];
    }

    public Cell getCell(Square square) {
        return cells[square.row()][square.col()];
    }

    public boolean isValidCoordinate(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public boolean isValidCoordinate(Square square) {
        return square != null && square.isOnBoard();
    }

    public Optional<ChessPiece> getPieceAt(int row, int col) {
        return cells[row][col].getPiece();
    }

    public Optional<ChessPiece> getPieceAt(Square square) {
        return cells[square.row()][square.col()].getPiece();
    }

    /**
     * Straight line between two squares (excluding endpoints) must be empty. Used by rook, bishop, queen.
     */
    public boolean isPathClear(int fromRow, int fromCol, int toRow, int toCol) {
        int dr = Integer.signum(toRow - fromRow);
        int dc = Integer.signum(toCol - fromCol);
        int r = fromRow + dr;
        int c = fromCol + dc;
        while (r != toRow || c != toCol) {
            if (cells[r][c].isOccupied()) {
                return false;
            }
            r += dr;
            c += dc;
        }
        return true;
    }

    public boolean isPathClear(Square from, Square to) {
        return isPathClear(from.row(), from.col(), to.row(), to.col());
    }

    /**
     * Whether this move obeys piece movement rules for the piece on {@code move.from()}.
     */
    public boolean isLegalMove(Move move) {
        if (!isValidCoordinate(move.from()) || !isValidCoordinate(move.to())) {
            return false;
        }
        Optional<ChessPiece> piece = getPieceAt(move.from());
        if (piece.isEmpty()) {
            return false;
        }
        return piece.get().canMove(move.from(), move.to(), this);
    }

    /**
     * Validates with {@link #isLegalMove(Move)} and, if legal, updates the grid and pawn state.
     *
     * @return {@code true} if the move was applied
     */
    public boolean tryMove(Move move) {
        if (!isLegalMove(move)) {
            return false;
        }
        relocate(move);
        return true;
    }

    /**
     * Same as {@link #tryMove(Move)} but throws if the move is illegal.
     */
    public void applyMove(Move move) {
        if (!tryMove(move)) {
            throw new IllegalArgumentException("Illegal move: " + move);
        }
    }

    private void relocate(Move move) {
        Cell from = getCell(move.from());
        Cell to = getCell(move.to());
        ChessPiece piece = from.getPiece().orElseThrow();
        to.getPiece().ifPresent(captured -> captured.setKilled(true));
        from.removePiece();
        to.placePiece(piece);
        if (piece instanceof Pawn pawn) {
            pawn.markMoved();
        }
    }
}
