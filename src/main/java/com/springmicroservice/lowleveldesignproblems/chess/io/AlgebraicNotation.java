package com.springmicroservice.lowleveldesignproblems.chess.io;

import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Move;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Square;

/**
 * Parses algebraic square names (file a–h, rank 1–8) matching the board convention:
 * rank 8 is row 0, rank 1 is row 7.
 */
public final class AlgebraicNotation {

    private AlgebraicNotation() {
    }

    /**
     * @param algebraic e.g. {@code "e4"}, {@code "a1"}
     */
    public static Optional<Square> parseSquare(String algebraic) {
        if (algebraic == null) {
            return Optional.empty();
        }
        String s = algebraic.trim().toLowerCase();
        if (s.length() != 2) {
            return Optional.empty();
        }
        char file = s.charAt(0);
        char rank = s.charAt(1);
        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            return Optional.empty();
        }
        int col = file - 'a';
        int rankNum = rank - '0';
        int row = 8 - rankNum;
        return Optional.of(new Square(row, col));
    }

    public static String format(Square square) {
        char file = (char) ('a' + square.col());
        int rankNum = 8 - square.row();
        return "" + file + rankNum;
    }

    /**
     * @param line e.g. {@code "e2 e4"} or {@code "e2e4"}
     */
    public static Optional<Move> parseMove(String line) {
        if (line == null || line.isBlank()) {
            return Optional.empty();
        }
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 2) {
            return parseSquare(parts[0]).flatMap(from -> parseSquare(parts[1]).map(to -> new Move(from, to)));
        }
        String compact = parts[0].replaceAll("\\s", "");
        if (compact.length() == 4) {
            return parseSquare(compact.substring(0, 2))
                    .flatMap(from -> parseSquare(compact.substring(2, 4)).map(to -> new Move(from, to)));
        }
        return Optional.empty();
    }
}
