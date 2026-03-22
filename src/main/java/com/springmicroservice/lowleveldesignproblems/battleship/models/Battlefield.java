package com.springmicroservice.lowleveldesignproblems.battleship.models;

/**
 * Represents the N×N grid battlefield. Uses Cell[][] for per-cell status and ship references.
 */
public class Battlefield {
    private final int size;
    private final Cell[][] cells;

    public Battlefield(int size) {
        this.size = size;
        this.cells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.cells[i][j] = new Cell(new Coordinate(i, j));
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Cell getCell(Coordinate coordinate) {
        return getCell(coordinate.getX(), coordinate.getY());
    }

    public Cell getCell(int x, int y) {
        if (!isInBounds(x, y)) {
            throw new IllegalArgumentException("Coordinate (" + x + ", " + y + ") is out of bounds");
        }
        return cells[x][y];
    }

    public boolean isInBounds(Coordinate coordinate) {
        return coordinate.isValid(size);
    }

    public boolean isInBounds(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size;
    }

    public boolean canPlaceShip(Ship ship) {
        if (ship.getX() + ship.getSize() > size || ship.getY() + ship.getSize() > size) {
            return false;
        }
        for (Coordinate coord : ship.getCells()) {
            if (!getCell(coord).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public void placeShip(Ship ship) {
        if (ship.getX() + ship.getSize() > size || ship.getY() + ship.getSize() > size) {
            throw new IllegalArgumentException("Ship is out of boundaries");
        }
        for (Coordinate coord : ship.getCells()) {
            Cell cell = getCell(coord);
            if (!cell.isEmpty()) {
                throw new IllegalArgumentException("Ship is overlapping with another ship");
            }
        }
        for (Coordinate coord : ship.getCells()) {
            getCell(coord).placeShip(ship);
        }
    }
}
