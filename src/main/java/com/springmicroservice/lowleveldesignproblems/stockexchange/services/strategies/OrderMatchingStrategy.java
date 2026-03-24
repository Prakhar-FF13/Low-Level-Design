package com.springmicroservice.lowleveldesignproblems.stockexchange.services.strategies;

import java.util.List;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Order;
import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Trade;

public interface OrderMatchingStrategy {
    String getStrategyName();
    List<Trade> matchOrders(Order newOrder, List<Order> existingOrders);
}
