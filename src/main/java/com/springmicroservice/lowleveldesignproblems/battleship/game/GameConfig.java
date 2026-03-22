package com.springmicroservice.lowleveldesignproblems.battleship.game;

import com.springmicroservice.lowleveldesignproblems.battleship.grid.GridDivisionStrategy;
import com.springmicroservice.lowleveldesignproblems.battleship.placement.ShipPlacementStrategy;
import com.springmicroservice.lowleveldesignproblems.battleship.targeting.TargetingStrategy;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Configuration for a battleship game.
 */
@Getter
@Builder
public class GameConfig {
    private final int gridSize;
    private final List<Integer> shipSizes;
    private final GridDivisionStrategy gridDivisionStrategy;
    private final ShipPlacementStrategy shipPlacementStrategy;
    private final TargetingStrategy targetingStrategy;

    public static GameConfig defaultConfig() {
        return GameConfig.builder()
                .gridSize(10)
                .shipSizes(List.of(2, 2, 3))
                .gridDivisionStrategy(new com.springmicroservice.lowleveldesignproblems.battleship.grid.VerticalSplitStrategy())
                .shipPlacementStrategy(new com.springmicroservice.lowleveldesignproblems.battleship.placement.RandomPlacementStrategy())
                .targetingStrategy(new com.springmicroservice.lowleveldesignproblems.battleship.targeting.RandomTargetingStrategy())
                .build();
    }
}
