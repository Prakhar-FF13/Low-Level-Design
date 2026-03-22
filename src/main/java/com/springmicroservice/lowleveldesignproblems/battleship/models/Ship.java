package com.springmicroservice.lowleveldesignproblems.battleship.models;

import lombok.Getter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Represents a square ship on the battlefield.
 */
@Getter
public class Ship {
    private final String id;
    private final int size;
    private final int x;
    private final int y;
    private final Set<Coordinate> hitCells = new HashSet<>();

    public Ship(String id, int size, int x, int y) {
        this.id = Objects.requireNonNull(id, "Ship id cannot be null");
        this.size = size;
        this.x = x;
        this.y = y;
    }

    public Coordinate getTopLeft() {
        return new Coordinate(x, y);
    }

    /**
     * Returns all coordinates occupied by this ship.
     */
    public Set<Coordinate> getCells() {
        return IntStream.range(0, size)
                .boxed()
                .flatMap(i -> IntStream.range(0, size)
                        .mapToObj(j -> new Coordinate(x + i, y + j)))
                .collect(Collectors.toSet());
    }

    public boolean occupies(Coordinate coordinate) {
        return coordinate.getX() >= x && coordinate.getX() < x + size
                && coordinate.getY() >= y && coordinate.getY() < y + size;
    }

    /**
     * Records a hit at the given coordinate. Called by Cell.markHit().
     */
    public void recordHit(Coordinate coordinate) {
        if (!occupies(coordinate)) {
            throw new IllegalArgumentException("Coordinate " + coordinate + " is not part of ship " + id);
        }
        hitCells.add(coordinate);
    }

    public boolean isDestroyed() {
        return hitCells.size() == size * size;
    }
}
