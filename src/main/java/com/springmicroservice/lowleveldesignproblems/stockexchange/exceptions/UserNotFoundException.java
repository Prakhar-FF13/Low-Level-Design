package com.springmicroservice.lowleveldesignproblems.stockexchange.exceptions;

public class UserNotFoundException extends TradingException {

    public UserNotFoundException(String userId) {
        super("User not found: " + userId);
    }
}
