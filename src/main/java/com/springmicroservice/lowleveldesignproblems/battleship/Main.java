package com.springmicroservice.lowleveldesignproblems.battleship;

import com.springmicroservice.lowleveldesignproblems.battleship.game.Game;
import com.springmicroservice.lowleveldesignproblems.battleship.game.GameBuilder;
import com.springmicroservice.lowleveldesignproblems.battleship.models.FireResult;
import com.springmicroservice.lowleveldesignproblems.battleship.models.GameStatus;
import com.springmicroservice.lowleveldesignproblems.battleship.view.BattlefieldView;

/**
 * Entry point for the Battleship game.
 */
public class Main {
    public static void main(String[] args) {
        Game game = GameBuilder.withDefaults().build();
        game.startGame();

        System.out.println("=== Battleship Game Started ===\n");
        System.out.println("Grid: 10x10 | Ships: 2x2, 2x2, 3x3 per player");
        System.out.println("Player 1 (top half) vs Player 2 (bottom half)\n");

        int round = 0;
        while (game.getGameStatus() == GameStatus.IN_PROGRESS) {
            round++;
            System.out.println("--- Round " + round + " ---");
            System.out.println("Current turn: " + game.getCurrentTurn().getName());
            System.out.println("P1 ships left: " + game.getPlayer1().getShipsRemaining() + 
                    " | P2 ships left: " + game.getPlayer2().getShipsRemaining());

            FireResult result = game.fire();
            System.out.println("Fired at (" + result.getCoordinate().getX() + "," + result.getCoordinate().getY() + "): " + result.getMessage());
            System.out.println();
        }

        System.out.println("=== Game Over ===");
        if (game.getGameStatus() == GameStatus.PLAYER1_WINS) {
            System.out.println("Player 1 wins!");
        } else {
            System.out.println("Player 2 wins!");
        }
        System.out.println("Total rounds: " + round);
    }
}
