package com.springmicroservice.lowleveldesignproblems.parkinglot.exceptions;

public class InvalidTicketException extends RuntimeException {
    public InvalidTicketException(String message) {
        super(message);
    }
}
