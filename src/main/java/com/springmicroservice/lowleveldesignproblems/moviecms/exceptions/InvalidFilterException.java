package com.springmicroservice.lowleveldesignproblems.moviecms.exceptions;

public class InvalidFilterException extends RuntimeException {
    public InvalidFilterException(String message) {
        super(message);
    }
    public InvalidFilterException(String message, Throwable cause) {
        super(message, cause);
    }
}
