package com.springmicroservice.lowleveldesignproblems.battleship.models;

import lombok.Getter;

/**
 * Represents a single cell on the battlefield.
 */
@Getter
public class Cell {
    private final Coordinate coordinate;
    private CellStatus status;
    private Ship ship;

    public Cell(Coordinate coordinate) {
        this.coordinate = coordinate;
        this.status = CellStatus.EMPTY;
        this.ship = null;
    }

    public void placeShip(Ship ship) {
        this.ship = ship;
        this.status = CellStatus.OCCUPIED;
    }

    public void markHit() {
        if (status != CellStatus.OCCUPIED) {
            throw new IllegalStateException("Cannot hit cell that is not occupied");
        }
        this.status = CellStatus.HIT;
        if (ship != null) {
            ship.recordHit(coordinate);
        }
    }

    public void markMiss() {
        if (status != CellStatus.EMPTY) {
            throw new IllegalStateException("Cannot mark miss on non-empty cell");
        }
        this.status = CellStatus.MISS;
    }

    public boolean isEmpty() {
        return status == CellStatus.EMPTY;
    }

    public boolean isOccupied() {
        return status == CellStatus.OCCUPIED || status == CellStatus.HIT;
    }
}
