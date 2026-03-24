package com.springmicroservice.lowleveldesignproblems.stockexchange.services.strategies;

import java.util.List;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Order;
import com.springmicroservice.lowleveldesignproblems.stockexchange.models.OrderType;
import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Trade;

public class FifoOrderMatchingStrategy implements OrderMatchingStrategy {
    @Override
    public String getStrategyName() {
        return "FIFO";
    }

    @Override
    public List<Trade> matchOrders(Order newOrder, List<Order> existingOrders) {
        return newOrder.getType() == OrderType.BUY
                ? matchBuyOrder(newOrder, existingOrders)
                : newOrder.getType() == OrderType.SELL
                        ? matchSellOrder(newOrder, existingOrders)
                        : List.of();
    }

    private List<Trade> matchBuyOrder(Order buy, List<Order> existingOrders) {
        return existingOrders.stream()
                .filter(other -> buy.getReamingingQuantity() > 0)
                .filter(other -> other.getType() == OrderType.SELL)
                .filter(other -> other.getPrice() == buy.getPrice())
                .filter(other -> other.getReamingingQuantity() > 0)
                .map(sell -> matchBuyAgainstSell(buy, sell))
                .toList();
    }

    private List<Trade> matchSellOrder(Order sell, List<Order> existingOrders) {
        return existingOrders.stream()
                .filter(other -> sell.getReamingingQuantity() > 0)
                .filter(other -> other.getType() == OrderType.BUY)
                .filter(other -> other.getPrice() == sell.getPrice())
                .filter(other -> other.getReamingingQuantity() > 0)
                .map(buy -> matchSellAgainstBuy(sell, buy))
                .toList();
    }

    private static Trade matchBuyAgainstSell(Order buy, Order sell) {
        int qty = Math.min(buy.getReamingingQuantity(), sell.getReamingingQuantity());
        buy.setFilledQuantity(buy.getFilledQuantity() + qty);
        buy.setReamingingQuantity(buy.getReamingingQuantity() - qty);
        sell.setFilledQuantity(sell.getFilledQuantity() + qty);
        sell.setReamingingQuantity(sell.getReamingingQuantity() - qty);
        return Trade.builder()
                .buyerOrderId(buy.getId())
                .sellerOrderId(sell.getId())
                .stockId(buy.getStockId())
                .quantity(qty)
                .price(buy.getPrice())
                .build();
    }

    private static Trade matchSellAgainstBuy(Order sell, Order buy) {
        int qty = Math.min(sell.getReamingingQuantity(), buy.getReamingingQuantity());
        sell.setFilledQuantity(sell.getFilledQuantity() + qty);
        sell.setReamingingQuantity(sell.getReamingingQuantity() - qty);
        buy.setFilledQuantity(buy.getFilledQuantity() + qty);
        buy.setReamingingQuantity(buy.getReamingingQuantity() - qty);
        return Trade.builder()
                .buyerOrderId(buy.getId())
                .sellerOrderId(sell.getId())
                .stockId(sell.getStockId())
                .quantity(qty)
                .price(sell.getPrice())
                .build();
    }
}
