package com.springmicroservice.lowleveldesignproblems.tictactoe.state;

import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Move;

/**
 * State pattern: Interface for game states (InProgress, Win, Draw).
 * Each state encapsulates behavior and transitions.
 */
public interface GameState {
    boolean isTerminal();

    /**
     * Handles the result display when game ends (Win/Draw).
     * No-op for InProgressState.
     */
    void handleEndGame(GameStateContext context);

    /**
     * Called when a move is applied - may transition to Win or Draw state.
     *
     * @return the new state (could be this, WinState, or DrawState)
     */
    GameState onMoveApplied(GameStateContext context, Move move);
}
