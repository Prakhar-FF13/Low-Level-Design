package com.springmicroservice.lowleveldesignproblems.battleship.game;

import com.springmicroservice.lowleveldesignproblems.battleship.exceptions.CoordinateAlreadyFiredException;
import com.springmicroservice.lowleveldesignproblems.battleship.exceptions.InvalidCoordinateException;
import com.springmicroservice.lowleveldesignproblems.battleship.models.*;
import com.springmicroservice.lowleveldesignproblems.battleship.targeting.TargetingStrategy;
import lombok.Getter;

import java.util.Objects;

/**
 * Represents a battleship game session.
 */
@Getter
public class Game {
    private final String id;
    private final Battlefield battlefield;
    private final Player player1;
    private final Player player2;
    private final TurnManager turnManager;
    private final TargetingStrategy targetingStrategy;
    private final GameConfig config;
    private GameStatus gameStatus;

    public Game(String id, Battlefield battlefield, Player player1, Player player2,
                TurnManager turnManager, TargetingStrategy targetingStrategy, GameConfig config) {
        this.id = Objects.requireNonNull(id, "Game id cannot be null");
        this.battlefield = Objects.requireNonNull(battlefield, "Battlefield cannot be null");
        this.player1 = Objects.requireNonNull(player1, "Player 1 cannot be null");
        this.player2 = Objects.requireNonNull(player2, "Player 2 cannot be null");
        this.turnManager = Objects.requireNonNull(turnManager, "TurnManager cannot be null");
        this.targetingStrategy = Objects.requireNonNull(targetingStrategy, "TargetingStrategy cannot be null");
        this.config = config != null ? config : GameConfig.defaultConfig();
        this.gameStatus = GameStatus.NOT_STARTED;
    }

    public Player getCurrentTurn() {
        return turnManager.getCurrentTurn();
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    /**
     * Fires at the given coordinate. Validates turn, coordinate, and applies hit/miss.
     */
    public FireResult fire(Coordinate coordinate) {
        if (gameStatus != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress");
        }

        Player attacker = turnManager.getCurrentTurn();
        Player target = turnManager.getOpponent(attacker);

        if (!target.getBounds().contains(coordinate)) {
            throw new InvalidCoordinateException(coordinate, "Coordinate not in target's territory");
        }
        if (attacker.hasAttacked(coordinate)) {
            throw new CoordinateAlreadyFiredException(coordinate);
        }

        attacker.recordAttack(coordinate);
        Cell targetCell = battlefield.getCell(coordinate);

        if (targetCell.isOccupied() && targetCell.getStatus() == CellStatus.OCCUPIED) {
            targetCell.markHit();
            boolean shipDestroyed = targetCell.getShip() != null && targetCell.getShip().isDestroyed();
            turnManager.nextTurn();

            if (target.hasLost()) {
                gameStatus = (attacker == player1) ? GameStatus.PLAYER1_WINS : GameStatus.PLAYER2_WINS;
            }

            String message = shipDestroyed ? "Hit! Ship destroyed!" : "Hit!";
            return new FireResult(true, shipDestroyed, coordinate, message);
        } else {
            targetCell.markMiss();
            turnManager.nextTurn();
            return new FireResult(false, false, coordinate, "Miss!");
        }
    }

    /**
     * Fires at a coordinate selected by the targeting strategy.
     */
    public FireResult fire() {
        Player attacker = turnManager.getCurrentTurn();
        Player target = turnManager.getOpponent(attacker);
        Coordinate targetCoord = targetingStrategy.getNextTarget(attacker, target);
        return fire(targetCoord);
    }

    public void startGame() {
        if (gameStatus != GameStatus.NOT_STARTED) {
            throw new IllegalStateException("Game already started");
        }
        this.gameStatus = GameStatus.IN_PROGRESS;
    }
}
