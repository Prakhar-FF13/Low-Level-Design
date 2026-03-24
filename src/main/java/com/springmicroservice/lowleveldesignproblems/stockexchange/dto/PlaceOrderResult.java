package com.springmicroservice.lowleveldesignproblems.stockexchange.dto;

import java.util.List;

/**
 * Result of placing an order: the persisted order view and any trades produced by matching.
 */
public record PlaceOrderResult(
        OrderResponse order,
        List<TradeResponse> trades
) {}
