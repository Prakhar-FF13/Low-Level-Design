package com.springmicroservice.lowleveldesignproblems.stockexchange.exceptions;

public class InvalidOrderException extends TradingException {

    public InvalidOrderException(String message) {
        super(message);
    }

    public InvalidOrderException(String message, Throwable cause) {
        super(message, cause);
    }
}
