package com.springmicroservice.lowleveldesignproblems.battleship.game;

import com.springmicroservice.lowleveldesignproblems.battleship.grid.Bounds;
import com.springmicroservice.lowleveldesignproblems.battleship.grid.GridDivisionStrategy;
import com.springmicroservice.lowleveldesignproblems.battleship.models.Battlefield;
import com.springmicroservice.lowleveldesignproblems.battleship.models.Player;
import com.springmicroservice.lowleveldesignproblems.battleship.models.Ship;
import com.springmicroservice.lowleveldesignproblems.battleship.placement.ShipPlacementStrategy;
import com.springmicroservice.lowleveldesignproblems.battleship.targeting.TargetingStrategy;

import java.util.List;
import java.util.UUID;

/**
 * Builds a fully configured Game with battlefield, players, and ships placed.
 */
public class GameBuilder {
    private final GameConfig config;

    public GameBuilder(GameConfig config) {
        this.config = config != null ? config : GameConfig.defaultConfig();
    }

    public static GameBuilder withDefaults() {
        return new GameBuilder(GameConfig.defaultConfig());
    }

    public Game build() {
        Battlefield battlefield = new Battlefield(config.getGridSize());
        GridDivisionStrategy gridStrategy = config.getGridDivisionStrategy();
        ShipPlacementStrategy placementStrategy = config.getShipPlacementStrategy();
        TargetingStrategy targetingStrategy = config.getTargetingStrategy();

        Bounds p1Bounds = gridStrategy.getTerritoryBounds(1, config.getGridSize());
        Bounds p2Bounds = gridStrategy.getTerritoryBounds(2, config.getGridSize());

        List<Ship> p1Ships = placementStrategy.placeShips(battlefield, p1Bounds, config.getShipSizes());
        List<Ship> p2Ships = placementStrategy.placeShips(battlefield, p2Bounds, config.getShipSizes());

        Player player1 = new Player("player1", "Player 1", battlefield, p1Bounds, p1Ships);
        Player player2 = new Player("player2", "Player 2", battlefield, p2Bounds, p2Ships);

        TurnManager turnManager = new TurnManager(player1, player2);

        return new Game(
                UUID.randomUUID().toString(),
                battlefield,
                player1,
                player2,
                turnManager,
                targetingStrategy,
                config
        );
    }
}
