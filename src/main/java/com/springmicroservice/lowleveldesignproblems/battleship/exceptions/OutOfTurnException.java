package com.springmicroservice.lowleveldesignproblems.battleship.exceptions;

public class OutOfTurnException extends RuntimeException {
    public OutOfTurnException(String message) {
        super(message);
    }
}
