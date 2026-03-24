package com.springmicroservice.lowleveldesignproblems.stockexchange.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Order;
import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Trade;
import com.springmicroservice.lowleveldesignproblems.stockexchange.orderbook.IOrderBook;
import com.springmicroservice.lowleveldesignproblems.stockexchange.services.strategies.OrderMatchingStrategy;

/**
 * Runs matching for one symbol: serializes per {@code stockId}, applies the strategy, records trades, removes fully filled orders.
 * Isolated from {@link ExchangeService} for SRP (exchange API vs match lifecycle).
 */
public class OrderMatchingExecutor {

    private final IOrderBook orderBook;
    private final OrderMatchingStrategy orderMatchingStrategy;
    private final TradeService tradeService;
    private final ConcurrentHashMap<String, Object> matchLocks = new ConcurrentHashMap<>();

    public OrderMatchingExecutor(
            IOrderBook orderBook,
            OrderMatchingStrategy orderMatchingStrategy,
            TradeService tradeService) {
        this.orderBook = orderBook;
        this.orderMatchingStrategy = orderMatchingStrategy;
        this.tradeService = tradeService;
    }

    public void executeMatch(Order order) {
        String stockId = order.getStockId();
        Object symbolLock = matchLocks.computeIfAbsent(stockId, k -> new Object());
        synchronized (symbolLock) {
            List<Order> existingOrders = orderBook.getOrders(stockId).stream()
                    .filter(o -> !o.getId().equals(order.getId()))
                    .toList();

            List<Trade> trades = orderMatchingStrategy.matchOrders(order, existingOrders);

            if (trades.isEmpty()) {
                return;
            }

            tradeService.recordTrades(trades);

            Set<String> affectedOrderIds = new HashSet<>();
            affectedOrderIds.add(order.getId());
            for (Trade trade : trades) {
                affectedOrderIds.add(trade.getBuyerOrderId());
                affectedOrderIds.add(trade.getSellerOrderId());
            }

            for (String orderId : affectedOrderIds) {
                orderBook.getOrders(stockId).stream()
                        .filter(o -> o.getId().equals(orderId))
                        .findFirst()
                        .ifPresent(o -> {
                            if (o.getReamingingQuantity() <= 0) {
                                orderBook.removeOrder(orderId, stockId);
                            }
                        });
            }
        }
    }
}
