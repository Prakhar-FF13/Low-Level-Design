package com.springmicroservice.lowleveldesignproblems.stockexchange.services;

import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Order;
import com.springmicroservice.lowleveldesignproblems.stockexchange.orderbook.IOrderBook;
import com.springmicroservice.lowleveldesignproblems.stockexchange.services.strategies.OrderMatchingStrategy;

/**
 * Exchange-facing API: order book updates and async scheduling of matching. Matching details live in {@link OrderMatchingExecutor}.
 */
public class ExchangeService {

    private final IOrderBook orderBook;
    private final OrderMatchingExecutor matchingExecutor;
    private final ExecutorService executorService;

    /**
     * Full control: inject executor for tests or production tuning (DIP).
     */
    public ExchangeService(
            IOrderBook orderBook,
            OrderMatchingExecutor matchingExecutor,
            ExecutorService executorService) {
        this.orderBook = Objects.requireNonNull(orderBook);
        this.matchingExecutor = Objects.requireNonNull(matchingExecutor);
        this.executorService = Objects.requireNonNull(executorService);
    }

    /**
     * Convenience wiring with a default fixed thread pool and a new {@link OrderMatchingExecutor}.
     */
    public ExchangeService(IOrderBook orderBook, OrderMatchingStrategy strategy, TradeService tradeService) {
        this(
                orderBook,
                new OrderMatchingExecutor(orderBook, strategy, tradeService),
                Executors.newFixedThreadPool(10));
    }

    public void placeOrder(Order order) {
        order.setCreatedAt(Instant.now());
        orderBook.addOrder(order);
        CompletableFuture.runAsync(() -> matchingExecutor.executeMatch(order), executorService);
    }

    public void cancelOrder(String orderId, String stockId) {
        orderBook.removeOrder(orderId, stockId);
    }

    public void modifyOrder(Order order) {
        order.setCreatedAt(Instant.now());
        orderBook.updateOrder(order);
        CompletableFuture.runAsync(() -> matchingExecutor.executeMatch(order), executorService);
    }
}
