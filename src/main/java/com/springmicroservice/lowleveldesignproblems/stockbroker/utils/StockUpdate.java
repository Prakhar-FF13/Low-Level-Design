package com.springmicroservice.lowleveldesignproblems.stockbroker.utils;

import java.time.Instant;

import lombok.Data;

@Data
public class StockUpdate {
    private String exchangeId;
    private StockSymbols symbol;
    private Value price;
    private Instant timestamp;

    public StockUpdate(String exchangeId, StockSymbols symbol, Value price, Instant timestamp) {
        this.exchangeId = exchangeId;
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }
}
