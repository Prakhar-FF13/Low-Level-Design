package com.springmicroservice.lowleveldesignproblems.battleship.grid;

/**
 * Splits the grid vertically: Player 1 gets top half (rows 0..mid-1), Player 2 gets bottom half.
 */
public class VerticalSplitStrategy implements GridDivisionStrategy {
    @Override
    public Bounds getTerritoryBounds(int playerNumber, int gridSize) {
        int mid = gridSize / 2;
        if (playerNumber == 1) {
            return new Bounds(0, mid - 1, 0, gridSize - 1);
        } else {
            return new Bounds(mid, gridSize - 1, 0, gridSize - 1);
        }
    }
}
