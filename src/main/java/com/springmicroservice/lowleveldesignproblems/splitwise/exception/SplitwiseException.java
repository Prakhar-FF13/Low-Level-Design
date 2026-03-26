package com.springmicroservice.lowleveldesignproblems.splitwise.exception;

/** One exception for the module: use {@link #notFound(String)} vs {@link #badRequest(String)}. */
public class SplitwiseException extends RuntimeException {

    private final boolean notFound;

    private SplitwiseException(String message, boolean notFound) {
        super(message);
        this.notFound = notFound;
    }

    public static SplitwiseException notFound(String message) {
        return new SplitwiseException(message, true);
    }

    public static SplitwiseException badRequest(String message) {
        return new SplitwiseException(message, false);
    }

    public boolean isNotFound() {
        return notFound;
    }
}
