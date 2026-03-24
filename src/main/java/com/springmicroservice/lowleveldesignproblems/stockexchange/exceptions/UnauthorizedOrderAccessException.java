package com.springmicroservice.lowleveldesignproblems.stockexchange.exceptions;

public class UnauthorizedOrderAccessException extends TradingException {

    public UnauthorizedOrderAccessException(String orderId, String userId) {
        super("User " + userId + " is not allowed to access order " + orderId);
    }
}
