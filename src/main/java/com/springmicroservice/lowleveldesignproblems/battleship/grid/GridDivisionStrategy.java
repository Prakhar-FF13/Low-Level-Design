package com.springmicroservice.lowleveldesignproblems.battleship.grid;

/**
 * Strategy for dividing the grid between two players.
 */
public interface GridDivisionStrategy {
    /**
     * Returns the bounds for the given player (1 or 2) on a grid of the given size.
     */
    Bounds getTerritoryBounds(int playerNumber, int gridSize);
}
