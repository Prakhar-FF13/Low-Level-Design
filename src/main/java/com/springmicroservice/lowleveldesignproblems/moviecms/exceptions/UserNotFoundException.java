package com.springmicroservice.lowleveldesignproblems.moviecms.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
    public UserNotFoundException(String id, Throwable cause) {
        super("User not found: " + id, cause);
    }
}
