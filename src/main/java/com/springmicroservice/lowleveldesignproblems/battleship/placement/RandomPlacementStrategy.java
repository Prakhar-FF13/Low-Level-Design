package com.springmicroservice.lowleveldesignproblems.battleship.placement;

import com.springmicroservice.lowleveldesignproblems.battleship.grid.Bounds;
import com.springmicroservice.lowleveldesignproblems.battleship.models.Battlefield;
import com.springmicroservice.lowleveldesignproblems.battleship.models.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Randomly places ships within the given bounds.
 */
public class RandomPlacementStrategy implements ShipPlacementStrategy {
    private final Random random = new Random();

    @Override
    public List<Ship> placeShips(Battlefield battlefield, Bounds bounds, List<Integer> shipSizes) {
        List<Ship> ships = new ArrayList<>();
        int shipIndex = 0;
        for (Integer size : shipSizes) {
            Ship ship = placeOneShip(battlefield, bounds, size, "ship-" + shipIndex);
            ships.add(ship);
            shipIndex++;
        }
        return ships;
    }

    private Ship placeOneShip(Battlefield battlefield, Bounds bounds, int size, String baseId) {
        int maxAttempts = 100;
        int maxValidX = bounds.getMaxRow() - size + 1;
        int maxValidY = bounds.getMaxCol() - size + 1;
        if (maxValidX < bounds.getMinRow() || maxValidY < bounds.getMinCol()) {
            throw new IllegalArgumentException("Ship size " + size + " too large for bounds");
        }
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            int x = bounds.getMinRow() + random.nextInt(maxValidX - bounds.getMinRow() + 1);
            int y = bounds.getMinCol() + random.nextInt(maxValidY - bounds.getMinCol() + 1);
            Ship ship = new Ship(UUID.randomUUID().toString(), size, x, y);
            if (battlefield.canPlaceShip(ship)) {
                battlefield.placeShip(ship);
                return ship;
            }
        }
        throw new IllegalStateException("Could not place ship of size " + size + " after " + maxAttempts + " attempts");
    }
}
