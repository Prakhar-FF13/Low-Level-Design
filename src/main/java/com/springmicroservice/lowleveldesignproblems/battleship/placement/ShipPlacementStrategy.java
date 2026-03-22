package com.springmicroservice.lowleveldesignproblems.battleship.placement;

import com.springmicroservice.lowleveldesignproblems.battleship.grid.Bounds;
import com.springmicroservice.lowleveldesignproblems.battleship.models.Battlefield;
import com.springmicroservice.lowleveldesignproblems.battleship.models.Ship;

import java.util.List;

/**
 * Strategy for placing ships on a player's territory.
 */
public interface ShipPlacementStrategy {
    /**
     * Returns a list of ships placed within the given bounds on the battlefield.
     */
    List<Ship> placeShips(Battlefield battlefield, Bounds bounds, List<Integer> shipSizes);
}
