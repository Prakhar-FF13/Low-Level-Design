package com.springmicroservice.lowleveldesignproblems.battleship.exceptions;

import com.springmicroservice.lowleveldesignproblems.battleship.models.Coordinate;

public class CoordinateAlreadyFiredException extends RuntimeException {
    public CoordinateAlreadyFiredException(Coordinate coordinate) {
        super("Coordinate (" + coordinate.getX() + ", " + coordinate.getY() + ") has already been fired at");
    }
}
