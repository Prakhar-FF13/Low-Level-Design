package com.springmicroservice.lowleveldesignproblems.tictactoe.state;

import java.util.List;
import java.util.Stack;

import com.springmicroservice.lowleveldesignproblems.tictactoe.io.OutputPresenter;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Board;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Move;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Player;
import com.springmicroservice.lowleveldesignproblems.tictactoe.strategies.WinningStrategy;

/**
 * Context for the State pattern - holds game data and delegates state-specific behavior.
 */
public class GameStateContext {
    private GameState currentState;
    private final Board board;
    private final List<Player> players;
    private final List<WinningStrategy> winningStrategies;
    private final Stack<Move> moves;
    private Player currentPlayer;
    private Player winningPlayer;
    private OutputPresenter outputPresenter;

    public GameStateContext(Board board, List<Player> players, List<WinningStrategy> winningStrategies) {
        this.board = board;
        this.players = players;
        this.winningStrategies = winningStrategies;
        this.moves = new Stack<>();
        this.currentPlayer = players.get(0);
        this.currentState = new InProgressState();
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(GameState state) {
        this.currentState = state;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<WinningStrategy> getWinningStrategies() {
        return winningStrategies;
    }

    public Stack<Move> getMoves() {
        return moves;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public void setWinningPlayer(Player player) {
        this.winningPlayer = player;
    }

    public Player getWinningPlayer() {
        return winningPlayer;
    }

    public OutputPresenter getOutputPresenter() {
        return outputPresenter;
    }

    public void setOutputPresenter(OutputPresenter outputPresenter) {
        this.outputPresenter = outputPresenter;
    }
}
