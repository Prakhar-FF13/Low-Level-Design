package com.springmicroservice.lowleveldesignproblems.tictactoe.factory;

import java.util.List;

import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Board;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Player;
import com.springmicroservice.lowleveldesignproblems.tictactoe.services.Game;
import com.springmicroservice.lowleveldesignproblems.tictactoe.state.GameStateContext;
import com.springmicroservice.lowleveldesignproblems.tictactoe.strategies.WinningStrategy;

/**
 * Factory pattern: Creates fully configured game with all dependencies.
 */
public class GameFactory {

    public static Game createGame(int boardSize, List<Player> players) {
        return createGame(boardSize, Math.min(3, boardSize), players);
    }

    public static Game createGame(int boardSize, int winLength, List<Player> players) {
        Board board = new Board(boardSize, winLength);
        List<WinningStrategy> strategies = WinningStrategyFactory.createDefaultStrategies();
        GameStateContext context = new GameStateContext(board, players, strategies);
        return new Game(context);
    }

    public static Game createGame(int boardSize, int winLength, List<Player> players, List<WinningStrategy> customStrategies) {
        Board board = new Board(boardSize, winLength);
        GameStateContext context = new GameStateContext(board, players, customStrategies);
        return new Game(context);
    }
}
