package com.springmicroservice.lowleveldesignproblems.stockexchange.dto;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.OrderType;

import java.time.Instant;

/**
 * Outbound view of an executed trade. {@code tradeType} is optional context (e.g. aggressor side) when known.
 */
public record TradeResponse(
        String id,
        OrderType tradeType,
        String buyerOrderId,
        String sellerOrderId,
        String stockSymbol,
        int quantity,
        double price,
        Instant tradeTimestamp
) {}
