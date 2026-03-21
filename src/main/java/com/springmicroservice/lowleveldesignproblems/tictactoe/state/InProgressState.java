package com.springmicroservice.lowleveldesignproblems.tictactoe.state;

import java.util.List;

import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Move;
import com.springmicroservice.lowleveldesignproblems.tictactoe.models.Player;
import com.springmicroservice.lowleveldesignproblems.tictactoe.strategies.WinningStrategy;

/**
 * State pattern: Game is in progress - accepts moves and transitions to Win/Draw.
 */
public class InProgressState implements GameState {

    @Override
    public boolean isTerminal() {
        return false;
    }

    @Override
    public void handleEndGame(GameStateContext context) {
        // No-op - game not ended
    }

    @Override
    public GameState onMoveApplied(GameStateContext context, Move move) {
        // Check winning conditions
        for (WinningStrategy strategy : context.getWinningStrategies()) {
            if (strategy.isWinningMove(context.getBoard(), move)) {
                context.setWinningPlayer(move.getPlayer());
                return new WinState();
            }
        }
        // Check draw
        int totalCells = context.getBoard().getSize() * context.getBoard().getSize();
        if (context.getMoves().size() == totalCells) {
            return new DrawState();
        }
        // Advance to next player
        advanceToNextPlayer(context);
        return this;
    }

    private void advanceToNextPlayer(GameStateContext context) {
        List<Player> players = context.getPlayers();
        int currentIndex = players.indexOf(context.getCurrentPlayer());
        int nextIndex = (currentIndex + 1) % players.size();
        context.setCurrentPlayer(players.get(nextIndex));
    }
}
