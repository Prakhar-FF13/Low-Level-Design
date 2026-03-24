package com.springmicroservice.lowleveldesignproblems.stockexchange.orderbook;

import java.util.List;
import java.util.Optional;

import com.springmicroservice.lowleveldesignproblems.stockexchange.models.Order;

public interface IOrderBook {
    void addOrder(Order order);

    boolean removeOrder(String orderId, String stockId);

    boolean updateOrder(Order order);

    List<Order> getOrders();

    List<Order> getOrders(String stockId);

    Optional<Order> findOrderById(String orderId);
}
