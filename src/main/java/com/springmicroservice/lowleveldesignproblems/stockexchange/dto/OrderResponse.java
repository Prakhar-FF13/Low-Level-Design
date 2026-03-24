package com.springmicroservice.lowleveldesignproblems.stockexchange.dto;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.OrderStatus;
import com.springmicroservice.lowleveldesignproblems.stockexchange.models.OrderType;

import java.time.Instant;

/**
 * Outbound view of an order (API / service boundary).
 */
public record OrderResponse(
        String id,
        String userId,
        OrderType orderType,
        String stockSymbol,
        int quantity,
        int filledQuantity,
        int remainingQuantity,
        double price,
        Instant orderAcceptedAt,
        OrderStatus status
) {}
