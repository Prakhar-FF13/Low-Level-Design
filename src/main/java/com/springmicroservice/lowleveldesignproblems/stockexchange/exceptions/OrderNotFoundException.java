package com.springmicroservice.lowleveldesignproblems.stockexchange.exceptions;

public class OrderNotFoundException extends TradingException {

    public OrderNotFoundException(String orderId) {
        super("Order not found: " + orderId);
    }
}
