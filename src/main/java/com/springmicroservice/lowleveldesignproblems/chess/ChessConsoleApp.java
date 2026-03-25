package com.springmicroservice.lowleveldesignproblems.chess;

import java.util.Scanner;

import com.springmicroservice.lowleveldesignproblems.chess.game.ChessGame;
import com.springmicroservice.lowleveldesignproblems.chess.game.MoveResult;
import com.springmicroservice.lowleveldesignproblems.chess.io.AlgebraicNotation;
import com.springmicroservice.lowleveldesignproblems.chess.io.AsciiBoardRenderer;
import com.springmicroservice.lowleveldesignproblems.chess.models.helpers.Move;

/**
 * Console loop: two players enter moves in algebraic form (e.g. {@code e2 e4}). Commands: {@code quit}, {@code help}.
 * <p>
 * Architecture, diagrams, and run instructions: {@code README.md} and {@code DESIGN_GUIDE.md} in this package.
 * Application entry for Gradle: {@link Main}.
 */
public final class ChessConsoleApp {

    private ChessConsoleApp() {
    }

    @SuppressWarnings("resource")
    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Chess (basic rules; no castling/en passant). White moves first.");
        System.out.println("Enter moves as two squares: e2 e4  (or e2e4). Type 'help' or 'quit'.");
        System.out.println();

        while (true) {
            System.out.print(AsciiBoardRenderer.render(game.getBoard()));
            System.out.println(game.getSideToMove() + " to move.");

            System.out.print("> ");
            String line = scanner.nextLine();
            if (line == null) {
                break;
            }
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (trimmed.equalsIgnoreCase("quit") || trimmed.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye.");
                break;
            }
            if (trimmed.equalsIgnoreCase("help")) {
                printHelp();
                continue;
            }

            var parsed = AlgebraicNotation.parseMove(trimmed);
            if (parsed.isEmpty()) {
                System.out.println("Could not parse move. Example: e2 e4\n");
                continue;
            }
            Move move = parsed.get();
            MoveResult result = game.playMove(move);
            switch (result) {
                case SUCCESS -> System.out.println("OK.\n");
                case NO_PIECE_ON_FROM -> System.out.println("No piece on " + AlgebraicNotation.format(move.from()) + ".\n");
                case WRONG_SIDE -> System.out.println("That is not " + game.getSideToMove() + "'s piece.\n");
                case ILLEGAL_MOVE -> System.out.println("Illegal move for that piece.\n");
            }
        }
    }

    private static void printHelp() {
        System.out.println("""
                Files are a-h (columns), ranks are 1-8 (rows from White's side).
                White pieces are uppercase (KQRBNP), black are lowercase.
                Examples: e2 e4   Nf3   e7e5
                """);
    }
}
