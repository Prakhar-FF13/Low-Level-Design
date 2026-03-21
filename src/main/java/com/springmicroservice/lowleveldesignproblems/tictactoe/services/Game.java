package com.springmicroservice.lowleveldesignproblems.tictactoe.services;

import com.springmicroservice.lowleveldesignproblems.tictactoe.exceptions.GameException;
import com.springmicroservice.lowleveldesignproblems.tictactoe.io.InputProvider;
import com.springmicroservice.lowleveldesignproblems.tictactoe.io.OutputPresenter;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Board;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Move;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Player;
import com.springmicroservice.lowleveldesignproblems.tictactoe.state.GameState;
import com.springmicroservice.lowleveldesignproblems.tictactoe.state.GameStateContext;

/**
 * Template pattern: Defines the skeleton of the game flow.
 * playGame() is the template method; subclasses or injected components provide I/O hooks.
 */
public class Game {
    protected final GameStateContext context;
    protected InputProvider inputProvider;
    protected OutputPresenter outputPresenter;

    public Game(GameStateContext context) {
        this.context = context;
    }

    public void setInputProvider(InputProvider inputProvider) {
        this.inputProvider = inputProvider;
    }

    public void setOutputPresenter(OutputPresenter outputPresenter) {
        this.outputPresenter = outputPresenter;
        context.setOutputPresenter(outputPresenter);
    }

    /**
     * Template method: Defines the game loop skeleton.
     */
    public final void playGame() {
        displayWelcome();
        while (!context.getCurrentState().isTerminal()) {
            executeTurn();
        }
        context.getCurrentState().handleEndGame(context);
    }

    /**
     * Hook: Display welcome message (override for customization).
     */
    protected void displayWelcome() {
        if (outputPresenter != null) {
            outputPresenter.displayMessage("\n=== Tic Tac Toe Game ===\n");
        }
    }

    /**
     * Template method for a single turn - gets move, validates with retry, applies move.
     */
    protected void executeTurn() {
        displayBoard();
        displayCurrentPlayer();

        boolean validMove = false;
        while (!validMove) {
            int[] coords = getNextMove();
            if (coords == null) {
                continue;
            }
            try {
                makeMove(coords[0], coords[1]);
                validMove = true;
            } catch (GameException e) {
                displayMessage("Invalid move: " + e.getMessage() + " Please try again.");
            }
        }
    }

    /**
     * Hook: Get next move from input (provided by InputProvider).
     */
    protected int[] getNextMove() {
        if (inputProvider != null) {
            return inputProvider.getNextMove(context.getBoard().getSize());
        }
        return null;
    }

    /**
     * Hook: Display board (override for customization).
     */
    protected void displayBoard() {
        if (outputPresenter != null) {
            outputPresenter.displayBoard(context.getBoard());
            outputPresenter.displayMessage("");
        }
    }

    /**
     * Hook: Display current player.
     */
    protected void displayCurrentPlayer() {
        if (outputPresenter != null && context.getCurrentPlayer() != null) {
            outputPresenter.displayCurrentPlayer(context.getCurrentPlayer().getName());
        }
    }

    /**
     * Hook: Display a message.
     */
    protected void displayMessage(String message) {
        if (outputPresenter != null) {
            outputPresenter.displayMessage(message);
        }
    }

    public void makeMove(int row, int column) {
        GameState currentState = context.getCurrentState();
        if (currentState.isTerminal()) {
            throw new GameException("Game is not in progress");
        }
        validateMove(row, column);

        Player player = context.getCurrentPlayer();
        Move move = new Move(player, row, column);

        context.getBoard().setBoard(row, column, player.getSymbol());
        context.getMoves().push(move);

        GameState newState = currentState.onMoveApplied(context, move);
        context.setCurrentState(newState);
    }

    private void validateMove(int row, int column) {
        Board board = context.getBoard();
        int size = board.getSize();
        if (context.getMoves().size() >= size * size) {
            throw new GameException("Board is full");
        }
        if (row < 0 || row >= size || column < 0 || column >= size) {
            throw new GameException("Invalid row or column");
        }
        if (!board.isEmpty(row, column)) {
            throw new GameException("Cell is already occupied");
        }
    }

    public void undoMove() {
        if (!context.getMoves().isEmpty()) {
            Move move = context.getMoves().pop();
            context.getBoard().setBoard(move.getRow(), move.getColumn(), " ");
            // Revert to previous player - would need to track player history for full undo
        }
    }

    public GameStateContext getContext() {
        return context;
    }
}
