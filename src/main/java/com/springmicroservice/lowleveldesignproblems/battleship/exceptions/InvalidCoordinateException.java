package com.springmicroservice.lowleveldesignproblems.battleship.exceptions;

import com.springmicroservice.lowleveldesignproblems.battleship.models.Coordinate;

public class InvalidCoordinateException extends RuntimeException {
    public InvalidCoordinateException(Coordinate coordinate, String reason) {
        super("Invalid coordinate (" + coordinate.getX() + ", " + coordinate.getY() + "): " + reason);
    }

    public InvalidCoordinateException(String message) {
        super(message);
    }
}
