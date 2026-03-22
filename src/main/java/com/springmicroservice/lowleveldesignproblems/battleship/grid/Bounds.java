package com.springmicroservice.lowleveldesignproblems.battleship.grid;

import com.springmicroservice.lowleveldesignproblems.battleship.models.Coordinate;

import java.util.Objects;

/**
 * Defines the rectangular bounds of a player's territory on the grid.
 */
public class Bounds {
    private final int minRow;
    private final int maxRow;
    private final int minCol;
    private final int maxCol;

    public Bounds(int minRow, int maxRow, int minCol, int maxCol) {
        this.minRow = minRow;
        this.maxRow = maxRow;
        this.minCol = minCol;
        this.maxCol = maxCol;
    }

    public boolean contains(Coordinate coord) {
        return coord.getX() >= minRow && coord.getX() <= maxRow
                && coord.getY() >= minCol && coord.getY() <= maxCol;
    }

    public boolean contains(int row, int col) {
        return row >= minRow && row <= maxRow && col >= minCol && col <= maxCol;
    }

    public int getMinRow() {
        return minRow;
    }

    public int getMaxRow() {
        return maxRow;
    }

    public int getMinCol() {
        return minCol;
    }

    public int getMaxCol() {
        return maxCol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bounds bounds = (Bounds) o;
        return minRow == bounds.minRow && maxRow == bounds.maxRow
                && minCol == bounds.minCol && maxCol == bounds.maxCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minRow, maxRow, minCol, maxCol);
    }
}
