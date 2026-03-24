package com.springmicroservice.lowleveldesignproblems.stockexchange.dto;

import java.time.Instant;

public record StockResponse(
        String id,
        String symbol,
        String name,
        double lastPrice,
        Instant updatedAt
) {}
