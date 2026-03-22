package com.springmicroservice.lowleveldesignproblems.battleship.game;

import com.springmicroservice.lowleveldesignproblems.battleship.models.Player;

/**
 * Manages turn alternation between players.
 */
public class TurnManager {
    private final Player player1;
    private final Player player2;
    private Player currentTurn;

    public TurnManager(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentTurn = player1;
    }

    public Player getCurrentTurn() {
        return currentTurn;
    }

    public void nextTurn() {
        currentTurn = (currentTurn == player1) ? player2 : player1;
    }

    public Player getOpponent(Player player) {
        return player == player1 ? player2 : player1;
    }
}
