package com.springmicroservice.lowleveldesignproblems.tictactoe;

import java.util.Scanner;

import com.springmicroservice.lowleveldesignproblems.tictactoe.factory.GameFactory;
import com.springmicroservice.lowleveldesignproblems.tictactoe.factory.PlayerFactory;
import com.springmicroservice.lowleveldesignproblems.tictactoe.io.ConsoleInputProvider;
import com.springmicroservice.lowleveldesignproblems.tictactoe.io.ConsoleOutputPresenter;
import com.springmicroservice.lowleveldesignproblems.tictactoe.io.InputProvider;
import com.springmicroservice.lowleveldesignproblems.tictactoe.io.OutputPresenter;
import com.springmicroservice.lowleveldesignproblems.tictactoe.services.Game;

public class Main {
    public static void main(String[] args) {
        int boardSize = 3;
        int winLength = 3;
        int playerCount = 2;

        // Factory pattern: Create game with dependencies
        var players = PlayerFactory.createPlayers(playerCount);
        Game game = GameFactory.createGame(boardSize, winLength, players);

        // I/O abstraction
        Scanner scanner = new Scanner(System.in);
        InputProvider inputProvider = new ConsoleInputProvider(scanner);
        OutputPresenter outputPresenter = new ConsoleOutputPresenter();

        game.setInputProvider(inputProvider);
        game.setOutputPresenter(outputPresenter);

        game.playGame();

        scanner.close();
    }
}
