package com.springmicroservice.lowleveldesignproblems.tictactoe.state;

import com.springmicroservice.lowleveldesignproblems.tictactoe.io.OutputPresenter;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Move;

/**
 * State pattern: Terminal state - game ended in a draw.
 */
public class DrawState implements GameState {

    @Override
    public boolean isTerminal() {
        return true;
    }

    @Override
    public void handleEndGame(GameStateContext context) {
        OutputPresenter output = context.getOutputPresenter();
        if (output != null) {
            output.displayMessage("Game is a draw!");
        }
    }

    @Override
    public GameState onMoveApplied(GameStateContext context, Move move) {
        return this; // Already terminal
    }
}
