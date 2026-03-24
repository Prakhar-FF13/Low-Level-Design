package com.springmicroservice.lowleveldesignproblems.stockexchange.exceptions;

public class OrderNotCancellableException extends TradingException {

    public OrderNotCancellableException(String orderId, String reason) {
        super("Cannot cancel order " + orderId + ": " + reason);
    }
}
