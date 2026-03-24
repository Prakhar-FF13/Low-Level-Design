package com.springmicroservice.lowleveldesignproblems.stockexchange.exceptions;

public class StockNotFoundException extends TradingException {

    public StockNotFoundException(String stockId) {
        super("Stock not found: " + stockId);
    }
}
