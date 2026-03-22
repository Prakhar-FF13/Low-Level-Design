package com.springmicroservice.lowleveldesignproblems.battleship.models;

import lombok.Getter;

/**
 * Result of a missile fire action.
 */
@Getter
public class FireResult {
    private final boolean hit;
    private final boolean shipDestroyed;
    private final Coordinate coordinate;
    private final String message;

    public FireResult(boolean hit, boolean shipDestroyed, Coordinate coordinate, String message) {
        this.hit = hit;
        this.shipDestroyed = shipDestroyed;
        this.coordinate = coordinate;
        this.message = message;
    }
}
