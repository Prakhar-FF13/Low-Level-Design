package com.springmicroservice.lowleveldesignproblems.chess;

/**
 * Application entry point for the chess console demo. Delegates to {@link ChessConsoleApp}.
 *
 * @see ChessConsoleApp
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        ChessConsoleApp.main(args);
    }
}
