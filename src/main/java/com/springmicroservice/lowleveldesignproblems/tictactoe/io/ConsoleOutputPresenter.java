package com.springmicroservice.lowleveldesignproblems.tictactoe.io;

import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Board;

public class ConsoleOutputPresenter implements OutputPresenter {

    @Override
    public void displayBoard(Board board) {
        board.printBoard();
    }

    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void displayCurrentPlayer(String playerName) {
        System.out.println("Current player: " + playerName);
    }

    @Override
    public void displayPrompt(String prompt) {
        System.out.print(prompt);
    }
}
