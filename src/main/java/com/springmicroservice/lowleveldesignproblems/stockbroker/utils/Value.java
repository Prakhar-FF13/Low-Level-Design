package com.springmicroservice.lowleveldesignproblems.stockbroker.utils;

import lombok.Data;

@Data
public class Value {
    private Currency currency;
    private double value;
    
    public Value(Currency currency, double value) {
        this.currency = currency;
        this.value = value;
    }
}
