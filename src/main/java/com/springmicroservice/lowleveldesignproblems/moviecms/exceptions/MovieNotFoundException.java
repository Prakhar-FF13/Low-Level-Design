package com.springmicroservice.lowleveldesignproblems.moviecms.exceptions;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(String message) {
        super(message);
    }
    public MovieNotFoundException(String id, Throwable cause) {
        super("Movie not found: " + id, cause);
    }
}
