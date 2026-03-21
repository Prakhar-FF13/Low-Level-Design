package com.springmicroservice.lowleveldesignproblems.tictactoe.exceptions;

public class GameException extends RuntimeException {
    public GameException(String message) {
        super(message);
    }
}