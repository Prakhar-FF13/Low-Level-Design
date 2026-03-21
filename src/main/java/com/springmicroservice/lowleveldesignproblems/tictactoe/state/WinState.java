package com.springmicroservice.lowleveldesignproblems.tictactoe.state;

import com.springmicroservice.lowleveldesignproblems.tictactoe.io.OutputPresenter;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Move;

/**
 * State pattern: Terminal state - a player has won.
 */
public class WinState implements GameState {

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public void handleEndGame(GameStateContext context) {
        OutputPresenter output = context.getOutputPresenter();
        if (output != null && context.getWinningPlayer() != null) {
            output.displayMessage("Player " + context.getWinningPlayer().getName() + " wins!");
        }
    }

    @Override
    public GameState onMoveApplied(GameStateContext context, Move move) {
        return this; // Already terminal
    }
}
